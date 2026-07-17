# Agent Guidelines - Android Large Screen x Unity (Lost Crypt)

This file guides AI agents on how to continue the development of the Android Large Screen and Foldable optimizations in this project.

## Core Rules

1.  **Unity Thread Safety:** Ensure UI redraws and screen updates are called on the main Unity Thread. Use the `ExecuteOnMainUnityThread` helper coroutine/method inside [ConfigurationManager.cs](Assets/Scripts/OrientationScripts/ConfigurationManager.cs) if callbacks originate from native Android threads (via `UnityPlayer.UnitySendMessage`).
2.  **Manifest Integrity:** Do NOT modify or remove configuration-related `<activity>` attributes in [AndroidManifest.xml](Assets/Plugins/Android/AndroidManifest.xml) (such as `android:resizeableActivity="true"` or `android:configChanges`) unless explicitly asked, as this will break dynamic resizing and folding capabilities.
3.  **Cross-Platform Editing/Testing:** Although the target runtime is Android, game logic and UI adjustments should be testable in the Unity Editor using Play Mode.
4.  **Vibe Coding / Collaboration:** Use the Artifact System to propose plans and designs for user review before execution. Keep artifacts in sync with design documentation when required.
5.  **No Unrelated Refactoring:** Adhere strictly to the "Surgical Changes" rule in [GEMINI.md](GEMINI.md). Touch only what is necessary to solve the issue or implement the feature.
6.  **Relative Paths:** Do NOT use absolute file paths in documentation, references, or links. Always use paths relative to the project root.

## Development Workflow

Follow the guidelines in [GEMINI.md](GEMINI.md). Specifically:
1.  **Plan First:** State assumptions and a step-by-step verification plan before editing code.
2.  **Goal-Driven:** Define clear success criteria (e.g., "ensure UI adjustments react to fold change callback in Unity Editor Play Mode" or "verify Java payload serializes fold state correct").
3.  **Step-by-step Implementation:** Implement changes incrementally and verify them before proceeding.

## Tech Stack
-   **Engine:** Unity Editor `6000.5.4f1`
-   **Languages:** C# (for Unity scripts, Game/UI logic), Java (for native Android activity/bridge)
-   **Libraries:** Universal Render Pipeline (URP), Unity Input System, Android Jetpack WindowManager (`androidx.window:window:1.3.0` & `androidx.window:window-java:1.3.0`).

## Key Entry Points
-   **Native Android Controller:** [LargeScreenPlayableActivity.java](Assets/Plugins/Android/LargeScreenPlayableActivity.java) - Hooks into Jetpack WindowManager, formats and sends display/fold telemetry.
-   **Unity Bridge:** [ConfigurationManager.cs](Assets/Scripts/OrientationScripts/ConfigurationManager.cs) - Receives configuration and fold messages from Java, manages callbacks on main thread.
-   **UI Adapters:**
    -   [SafeZoneUI.cs](Assets/Scripts/OrientationScripts/SafeZoneUI.cs) - Adjusts UI canvas anchors to match safe areas.
-   **Gameplay Adapter:**
    -   [ConfigurationResponse.cs](Assets/Scripts/GameScripts/ConfigurationResponse.cs) - Handles game pauses/resumes during configuration changes.

## Verification
-   **Unity Editor Play Mode:** Play the project inside Unity Editor to verify UI scaling and basic layout responsiveness.
-   **Unity Build & Android Run:** Perform Android APK build and test on foldable emulator or physical large screen/foldable device to verify native Jetpack WindowManager integration.
