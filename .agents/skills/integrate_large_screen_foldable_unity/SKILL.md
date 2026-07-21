---
name: integrate-large-screen-foldable-unity
description: >-
  Integrate Android Jetpack WindowManager, JNI serialization bridge, and
  responsive safe area UI anchoring to support large screens and foldables
  (posture/hinge detection) in Unity mobile games. Use this skill when asked
  to make a Unity game responsive to foldable/large-screen device layouts, or
  when configuring JNI communication between Android native views and Unity.
---

# Integrate Large Screen & Foldable Unity

This skill provides a standardized set of instructions to configure a Unity project to support Android large screens and foldable devices. This includes setting up Android Jetpack WindowManager dependencies, native Java Activity integration, JNI message routing, thread-safe configuration managers, responsive safe zone UI anchoring, and pause-resume transition controllers.

## 1. Environment & Android Build Configuration

Before writing code, verify that the Android Build template and manifest are properly configured to support resizing and modern AndroidX components:

1.  **Configure AndroidManifest.xml**:
    *   Set `android:resizeableActivity="true"` on the primary `<activity>`.
    *   In `android:configChanges`, include: `orientation|screenSize|screenLayout|smallestScreenSize`. This ensures that screen updates do not tear down the Unity Activity context.
    *   Set `android:screenOrientation="fullSensor"` or similar to support rotation across all directions.
2.  **Import Jetpack WindowManager Dependencies**:
    *   In `mainTemplate.gradle`, add implementations for:
        *   `androidx.window:window:1.3.0`
        *   `androidx.window:window-java:1.3.0`
3.  **Enable AndroidX Support**:
    *   In `gradleTemplate.properties`, add `android.useAndroidX=true` and `android.enableJetifier=true`.
4.  **Set Unity PlayerSettings**:
    *   Ensure resizable window option is enabled (`PlayerSettings.resizableWindow = true` via custom editor script or Unity Build settings).

## 2. Native Android Java Activity (Window Info Tracker)

Implement or extend a custom `UnityPlayerActivity` subclass in the native Java layer (e.g. under `Assets/Plugins/Android`):

1.  **Initialize WindowInfoTracker**:
    *   Use `WindowInfoTracker.getOrCreate(activity)` to query layout updates.
2.  **Track Display Layout & Hinge Postures**:
    *   Listen to layout changes via a custom Java executor thread using `WindowInfoTracker.windowLayoutInfoFlow()`.
    *   Extract `FoldingFeature` details such as bounding box coordinates, occlusion mode, and state (`FLAT`, `HALF_OPENED`).
3.  **Serialize and Dispatch to C#**:
    *   Construct lightweight JSON payloads detailing:
        *   `foldingFeatures`: Array of fold coordinates, orientation (horizontal/vertical), and posture states.
        *   `displayMetrics`: Current width, height, and display density.
    *   Route updates using Unity's native bridge:
        `UnityPlayer.UnitySendMessage("ConfigurationManager", "onFoldChanged", jsonString)` or `onConfigurationChanged`.

## 3. C# JNI Serialization Bridge

In the Unity engine layer, create a central script (`ConfigurationManager.cs`) to receive native OS notifications:

1.  **Expose JNI-Accessible Receivers**:
    *   Define public void methods matching the signature expected by native `UnitySendMessage` (e.g. `onConfigurationChanged(string json)`, `onFoldChanged(string json)`).
2.  **Thread Safety & Dispatcher**:
    *   Since JNI callbacks can execute off the main loop, enqueue parsed actions into a main-thread helper or coroutine execution wrapper (e.g., `ExecuteOnMainUnityThread`).
3.  **Action Broadcasters**:
    *   Parse the JSON strings into C# structs.
    *   Invoke appropriate C# `Action` delegates or `UnityEvent` actions so responsive scenes can adjust accordingly.

## 4. UI Anchoring & Pause Buffering

Implement responsive UI and transition elements that listen to configuration events:

1.  **Reactive UI Anchoring (Safe Zone Adjustments)**:
    *   Implement scripts (e.g. `SafeZoneUI.cs`) that register for configuration change actions.
    *   On layout changes, query `Screen.safeArea` and update the local `RectTransform` anchor boundaries so interactive components avoid overlap with physical camera notches and display cutouts.
2.  **Pause-Resume Transition Buffers**:
    *   Viewport resizing can cause momentary frame jumps or visual artifacting.
    *   Implement a coordinator (e.g. `ConfigurationResponse.cs`) to pause gameplay loops (e.g., setting `Time.timeScale = 0`) and show a blackout/countdown overlay during resizing and fold state transitions, then smoothly resume.

## 5. Verification & Testing Checklist

Validate your implementation with the following checklist:

*   [ ] Verify the game does not crash when rotated or split in half-screen/multi-window mode.
*   [ ] Verify `AndroidManifest.xml` has `android:resizeableActivity="true"`.
*   [ ] Confirm UI anchors scale accurately to avoid overlapping physical screen cutout bounds.
*   [ ] Check that JNI callbacks execute updates on the main Unity Thread to prevent thread-safety exceptions.
*   [ ] Verify gameplay pausing triggers correctly and resumes after 1 second when folding state changes.
