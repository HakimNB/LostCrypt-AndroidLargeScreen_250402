# Optimizing Large Screen Unity - Agent E2E Test Plan

## Prerequisites
Read [SKILL.md](SKILL.md) to understand the requirements for manifests, Java native layers, and C# safe area scripts.

--------------------------------------------------------------------------------

## Test 1: Generate Optimization Manifest & Configs

**Prompt:** "Update my AndroidManifest.xml and Gradle settings to support dynamic resizes and Android Jetpack WindowManager dependencies."

**Verify:**
- Agent identifies the need to configure `AndroidManifest.xml`.
- Agent modifies the target `<activity>` to include `android:resizeableActivity="true"` and updates `android:configChanges` to include `orientation|screenSize|screenLayout|smallestScreenSize`.
- Agent adds `android.useAndroidX=true` to `gradleTemplate.properties`.
- Agent adds `androidx.window` dependencies to `mainTemplate.gradle`.

--------------------------------------------------------------------------------

## Test 2: Implement Dynamic Safe Zone scaling

**Prompt:** "Write a C# script to scale my HUD UI panel so it aligns within the screen notch safe boundaries."

**Verify:**
- Agent generates a script similar to `SafeZoneUI.cs` that obtains `Screen.safeArea` coordinates.
- The script scales min/max anchor points of a `RectTransform` dynamically.
- The script registers to a callback on `ConfigurationManager` to update UI dynamically whenever layout configuration changes.

--------------------------------------------------------------------------------

## Test 3: Implement Main-Thread JNI Callbacks

**Prompt:** "Write a receiver script that processes JNI JSON payloads sent from our native fold change activity and triggers actions on the main gameplay thread."

**Verify:**
- Agent generates a manager class (e.g. `ConfigurationManager.cs`) exposing receiver methods like `onFoldChanged(string json)`.
- The receiver schedules updates back to the main thread via a coroutine or main-thread dispatcher (e.g. `ExecuteOnMainUnityThread`).
