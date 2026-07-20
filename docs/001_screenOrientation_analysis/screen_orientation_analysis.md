# Design Document: Android 17 Screen Orientation Migration Analysis

## 1. Introduction & Context
Android 17 introduces deprecations to various `android:screenOrientation` values in `AndroidManifest.xml` to promote adaptive layouts and consistent user experiences across diverse device form factors (e.g., foldables, tablets, large screens).

Based on the upcoming specification:
*   **Remaining/Supported Values:** `landscape`, `fullSensor` (and implicitly `portrait` and `unspecified`).
*   **Deprecated Values:** `reverseLandscape`, `sensorLandscape`, `userLandscape`, `noSensor`, `sensor`, `fullUser` (and their portrait counterparts).

---

## 2. Current Project Status
Our Unity sample project specifies the following orientation attribute:
*   **Manifest File:** [AndroidManifest.xml](file:///Users/hakimh/dev/jetski/lsf-lostcrypt/Assets/Plugins/Android/AndroidManifest.xml#L9)
*   **Current Setting:** `android:screenOrientation="fullSensor"`
*   **Activity Settings:** `android:resizeableActivity="true"`

### Direct Impact Assessment
1.  **Is this project using a deprecated value?**
    **No.** `fullSensor` is a remaining/supported value. The project will continue to compile and run on Android 17 without warnings or failures related to this attribute.
2.  **Does this project need to be modified to adapt to the change?**
    Technically, **no modifications are strictly required** to keep the project compiling and running. However, keeping `fullSensor` presents usability challenges that we should critically evaluate.

---

## 3. Critical Analysis & User Experience Challenges

While `fullSensor` is not deprecated, its behavior has significant user experience trade-offs:

### A. Ignoring the System Auto-Rotate Lock
*   **Behavior of `fullSensor`:** The app utilizes the physical accelerometer for all 4 directions (portrait, landscape, reverse portrait, reverse landscape). **It completely ignores the system-wide auto-rotate lock.**
*   **UX Issue:** If a user has disabled auto-rotation in their system settings (e.g., they want to read/play while lying down in portrait), the game will still forcefully rotate to whatever orientation the physical sensor reports. This violates user intent and system-level settings.

### B. Portrait Layout Suitability
*   **Current implementation:** [MainmenuControl.cs](file:///Users/hakimh/dev/jetski/lsf-lostcrypt/Assets/Scenes/Mainmenu/MainmenuControl.cs#L198-L208) implements a dual-canvas system (`portraitCanvas` and `landscapeCanvas`) to handle portrait orientation, and [SafeZoneUI.cs](file:///Users/hakimh/dev/jetski/lsf-lostcrypt/Assets/Scripts/OrientationScripts/SafeZoneUI.cs) adjusts anchors dynamically.
*   **Gameplay Limitation:** While the menu handles portrait, the core game mechanics of *Lost Crypt* (a 2D sidescrolling platformer) are inherently designed for landscape aspect ratios. Playing the game in a narrow vertical aspect ratio significantly degrades the field of view and gameplay quality.

### C. The Conundrum of `fullUser` Deprecation
*   `fullUser` was previously the recommended setting for adaptive apps because it allowed all 4 orientations but respected the auto-rotate lock.
*   By deprecating `fullUser` and keeping `fullSensor`, Android is nudging developers to either:
    1.  Not restrict orientation at all (default to `unspecified` or let resizable activity configuration handle it).
    2.  Explicitly lock to standard orientations (e.g. `landscape`).

---

## 4. Evaluation of Migration Alternatives

We have three main paths forward. Below is an analysis of each:

### Option A: Retain `fullSensor` (Recommended)
Keep `android:screenOrientation="fullSensor"`.
*   **Recommendation Rationale:** 
    This project is a developer-facing sample demonstrating LargeScreen and Foldable optimization practices. The codebase has been fully adapted to handle layout changes dynamically in both portrait and landscape configurations. Keeping `fullSensor` forces the app to rotate to all physical orientations (ignoring the system auto-rotate lock), which is highly desirable for a showcase app as it guarantees developers can easily trigger and test orientation-responsive UI components and posture transitions.
*   **Pros:**
    *   Zero code/manifest changes.
    *   Guarantees rotation to all 4 angles on all devices, making the sample's adaptive features easy to demonstrate and inspect.
*   **Cons:**
    *   Ignores user settings (system auto-rotate lock) for standard production app environments.
    *   Forces portrait orientation on the main gameplay scene if physically rotated, though the UI components are adapted to handle this.

### Option B: Migrate to `unspecified`
Remove `android:screenOrientation` (defaults to `unspecified`) or explicitly set `android:screenOrientation="unspecified"`.
*   **Behavior:** The system decides the orientation. It fully respects the system auto-rotate lock. If resizable is true, the activity rotates according to the device rotation settings.
*   **Pros:**
    *   Strictly respects the user's auto-rotate settings.
    *   Allows the system to optimize orientation/posture transitions on foldables and tablets.
*   **Cons:**
    *   Requires verification of how Unity handles orientation changes under `unspecified` on different devices.
    *   Makes testing/showcasing both landscape and portrait behaviors harder if the host device has rotation locked.

### Option C: Lock to `landscape`
Set `android:screenOrientation="landscape"`.
*   **Behavior:** Locks the activity strictly to the standard landscape orientation (0°).
*   **Pros:**
    *   Ensures the game is always played in the intended landscape layout.
    *   Protects core gameplay from narrow portrait viewport degradation.
*   **Cons:**
    *   Does not rotate to `reverseLandscape` (180°), which is frustrating for users charging their device on one side.
    *   Fixed orientation values can trigger letterboxing or compatibility modes on some large screen/foldable devices when resizable is true.
    *   Hides the project's custom portrait layouts and adaptive behaviors.

---

## 5. Conclusion & Action Plan

Since the primary goal of this project is to showcase foldable and large-screen adaptation patterns, and the UI has already been fully optimized for both landscape and portrait layouts, we choose to **Retain `fullSensor` (Option A)**. 

No configuration changes are needed for the upcoming Android 17 release, as `fullSensor` remains a fully supported orientation setting.

