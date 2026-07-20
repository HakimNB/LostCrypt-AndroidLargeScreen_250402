# Design Document: Android Large Screen & Foldable Integration

## 1. Goal

The primary goal of this sample is to establish a robust reference implementation for Unity game developers who want to optimize their mobile games for Large Screen and Foldable Android devices (such as the Google Pixel Fold, Pixel Tablet, and other tablets and foldables). 

Specifically, this sample addresses several critical user-experience challenges that arise when a mobile game transitions away from standard single-screen smartphone viewports:

1. **Aspect Ratio and Layout Adaptability:**
   A game must seamlessly adjust its rendering viewport and canvas bounds to accommodate a vast range of aspect ratios—from ultra-wide unfolded displays and tablets, to narrow portrait-locked viewports or compact outer cover screens.

2. **Full Screen Utilization (Safe Area Alignment):**
   The application must utilize the entire screen real estate (including rendering behind camera notches and punch-holes) while ensuring that crucial interactive UI components (such as movement buttons, menus, and HUD displays) remain within the physical hardware safe area and are easily reachable.

3. **Fold Posture Awareness:**
   On foldable devices, the system must detect when the physical state of the hinge changes (e.g., flat, half-opened, or closed). This enables the game to dynamically rearrange UI controls and camera focal points based on device posture (such as adjusting the viewport when a device is partially folded on a table in tabletop mode).

4. **Seamless Viewport Resizing:**
   When the user resizes the game window (e.g., entering split-screen multitasking or folding/unfolding the screen), the application must prevent visual frame drops, rendering artifacts, or abrupt layout jumps by implementing a smooth, controlled pause-and-resume transition buffer.

## 2. Overview

The integration functions as a reactive pipeline. It detects hardware-level display changes at the operating system level, translates those changes into serialized metadata, bridges them into the game engine, and dispatches them to gameplay and UI systems that adapt dynamically.

### 2.1. Architectural Layout

The technical implementation is split into four primary components:

1. **Native OS Listener (Java Layer):**
   This component runs natively on the Android OS. It hooks into Android's window layout framework and the Jetpack WindowManager library. Its main job is to listen for system-level callbacks related to window orientation changes, screen boundary adjustments, and the physical posture of foldable hinges.

2. **JNI Serialization Bridge (JNI Layer):**
   Since the native Android environment (Java) and the Unity Engine runtime (C#) operate in separate VM contexts, this layer bridges the gap. It translates complex Java objects (like `FoldingFeature` boundaries) into lightweight JSON payloads and sends them to the Unity engine via Unity's Java-to-C# JNI message gateway (`UnitySendMessage`).

3. **Central Coordinator (C# Bridge Layer):**
   A persistent script [ConfigurationManager.cs](Assets/Scripts/OrientationScripts/ConfigurationManager.cs) attached to a GameObject in Unity. It serves as the JNI receiver. It deserializes the JSON payloads back into C# structures and uses a thread-safe scheduler to broadcast these changes on the main Unity gameplay thread.

4. **Reactive Subsystems (C# Application Layer):**
   These are individual scripts attached to UI components and game managers in the active scene:
   * **UI Anchoring [SafeZoneUI.cs](Assets/Scripts/OrientationScripts/SafeZoneUI.cs):** Dynamically adjusts panel coordinates to fit within the screen's safe boundaries, avoiding notches and screen corners.
   * **Gameplay Pausing [ConfigurationResponse.cs](Assets/Scripts/GameScripts/ConfigurationResponse.cs):** Temporarily pauses the game loops and presents a countdown overlay when screen boundaries change, hiding visual adjustments and preventing gameplay bugs.

## 3. Detailed Design

This section enumerates all files within the sample codebase involved in supporting Large Screen and Foldable features, along with their roles and rationale.

### 3.1. Android Native Build & Runtime Configuration

* **[AndroidManifest.xml](Assets/Plugins/Android/AndroidManifest.xml)**
  * **Status:** Active
  * **Rationale:** Dictates activity properties needed to support dynamic layout updates. Enabling `android:resizeableActivity="true"` allows the application to enter split-screen or resizable mode. Specifying options in `android:configChanges` instructs the Android OS to let the activity handle window and screen changes internally instead of tearing down and restarting the Unity process. Setting `android:screenOrientation="fullSensor"` allows the game to rotate freely based on physical sensors.
* **[LargeScreenPlayableActivity.java](Assets/Plugins/Android/LargeScreenPlayableActivity.java)**
  * **Status:** Active
  * **Rationale:** Serves as the custom main activity class. It initializes Jetpack WindowManager's `WindowInfoTracker` to monitor folding features and screen metrics. It formats and serializes these metrics into JSON string payloads and uses `UnityPlayer.UnitySendMessage` to notify the Unity engine. It also manages dual-display availability notifications and projection triggers.
* **[mainTemplate.gradle](Assets/Plugins/Android/mainTemplate.gradle)**
  * **Status:** Active
  * **Rationale:** Declares the Gradle dependencies necessary for compiling the custom Java activity. It imports `androidx.window:window:1.3.0` and `androidx.window:window-java:1.3.0` to pull in the WindowManager framework libraries.
* **[gradleTemplate.properties](Assets/Plugins/Android/gradleTemplate.properties)**
  * **Status:** Active
  * **Rationale:** Enables AndroidX library support via `android.useAndroidX=true`. This is required for compatibility with the modern Android Jetpack WindowManager library.

### 3.2. Unity Engine Bridge Components

* **[ConfigurationManager.cs](Assets/Scripts/OrientationScripts/ConfigurationManager.cs)**
  * **Status:** Active
  * **Rationale:** Acts as the primary Unity-side receiver of native notifications. It exposes JNI-accessible functions (`onConfigurationChanged`, `onFoldChanged`, `onDualDisplayAvailabilityChanged`) which read and parse the JNI JSON string payloads into C# data structures. It delegates execution to the main thread via a coroutine to ensure UI components update safely during Unity's update loop. It broadcasts events downstream via C# Actions and `UnityEvents`.

### 3.3. Viewport & UI Adapters

* **[SafeZoneUI.cs](Assets/Scripts/OrientationScripts/SafeZoneUI.cs)**
  * **Status:** Active
  * **Rationale:** Attached to UI panels that need safe-area padding. It listens for orientation changes from `ConfigurationManager`. On trigger, it retrieves the system's safe viewport area via `Screen.safeArea` and modifies the panel's `RectTransform` anchor boundaries so that UI controls remain within the physical safe zone of the screen, preventing overlap with camera notches or system bars.
* **[ConfigurationResponse.cs](Assets/Scripts/GameScripts/ConfigurationResponse.cs)**
  * **Status:** Active
  * **Rationale:** Manages game flow during layout changes. It intercepts configuration and fold events, setting `Time.timeScale` to zero and displaying a visual countdown overlay for 1.0 second. This buffers gameplay and camera adjustments, allowing layout modifications to finish before resuming play.

### 3.4. Diagnostics & Testing Utilities

* **[NotchAware.cs](Assets/Scripts/OrientationScripts/NotchAware.cs)**
  * **Status:** Active (Diagnostics only)
  * **Rationale:** Uses Unity's `Screen.cutouts` API to identify the exact coordinates of screen notches and draws green box outlines over them. This helps developers verify that UI layouts do not bleed into hardware cutout zones.
* **[DebuggingInfo.cs](Assets/Scripts/OrientationScripts/DebuggingInfo.cs)**
  * **Status:** Active (Diagnostics only)
  * **Rationale:** Listens to safe-area updates and renders detailed width, height, and anchor metrics on screen text labels for visual validation during testing.
* **[LargeScreenHelperTools.cs](Assets/Scripts/OrientationScripts/Editor/LargeScreenHelperTools.cs)**
  * **Status:** Active (Editor Tool)
  * **Rationale:** Extends the Unity Editor menu bar. Choosing `Android Large Screen -> Add Resizeable Option` executes code that programmatically sets `PlayerSettings.resizableWindow = true`, ensuring Unity packages the apk with resizable window flags enabled.

### 3.5. Unused / Inactive Components

* **[HingeAngleSensor.java](Assets/Plugins/Android/HingeAngleSensor.java)**
  * **Status:** **Inactive / Unused**
  * **Rationale:** A helper utility designed to register an Android `SensorEventListener` to read raw angular values from the device's hinge sensor. While it is present in the codebase under the Android plugins directory, it is never instantiated or referenced by any active script. This sensor utility is designed to support games that require fine-grained hinge angle metrics (for example, adapting mechanics or UI based on whether a device is opened at an acute or obtuse angle).
