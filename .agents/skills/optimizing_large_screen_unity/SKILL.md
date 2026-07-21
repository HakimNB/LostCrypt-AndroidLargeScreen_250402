---
name: optimizing-large-screen-unity
description: >-
  Guides the optimization of Unity games for Android large screen and foldable devices. Use when adding support for dynamic aspect ratios, configuring safe area or notch alignment, implementing seamless window resizing buffers, or handling foldable hinge postures via native Android Jetpack WindowManager. Don't use for generic mobile UI layout adjustments not related to foldables or large screen aspect ratios, or for non-Unity projects.
---

# Optimizing Unity Games for Large Screen & Foldables

A guide to integrating responsive layouts, dynamic safe areas, dynamic resizes, and foldable hinge postures into Android Unity games.

---

## 1. Native OS & manifest Configuration

To enable foldable and resizable features on Android, the game activity must be configured to handle configuration events natively instead of letting the OS restart the application process.

### Step 1.1: Configure `AndroidManifest.xml`
In your custom `AndroidManifest.xml` (located under `Assets/Plugins/Android/AndroidManifest.xml`), ensure the following attributes are configured on your main `<activity>` block:

- **`android:resizeableActivity="true"`**: Informs the OS that this app supports split-screen, picture-in-picture, and dynamic multi-window resizing.
- **`android:configChanges`**: Include `orientation|screenSize|screenLayout|smallestScreenSize`. This ensures Unity intercepts display size transitions directly rather than tearing down and restarting the Unity process.
- **`android:screenOrientation="fullSensor"`** (or `unspecified`): Allows the layout to rotate dynamically to landscape, reverse-landscape, portrait, and reverse-portrait.

Example:
```xml
<activity android:name="com.unity3d.player.UnityPlayerActivity" ...
          android:resizeableActivity="true"
          android:configChanges="mcc|mnc|locale|touchscreen|keyboard|keyboardHidden|navigation|orientation|screenLayout|uiMode|screenSize|smallestScreenSize|density|layoutDirection|fontScale"
          android:screenOrientation="fullSensor">
```

### Step 1.2: Gradle Configurations
Enable AndroidX and import the Android Jetpack WindowManager library to read hinge features.

1. In **`gradleTemplate.properties`**, add:
   ```properties
   android.useAndroidX=true
   ```
2. In **`mainTemplate.gradle`**, add the WindowManager dependencies:
   ```groovy
   dependencies {
       implementation 'androidx.window:window:1.3.0'
       implementation 'androidx.window:window-java:1.3.0'
       // ... other dependencies
   }
   ```

---

## 2. Implement the Java Native Bridge

Since Jetpack WindowManager functions in the Java runtime, a native Java subclass of `UnityPlayerActivity` must capture screen/hinge layout changes and push them to Unity.

### Step 2.1: Native Activity subclass
Create a custom Java activity (e.g., `LargeScreenPlayableActivity.java` under `Assets/Plugins/Android/`):

1. Initialize `WindowInfoTracker` in `onCreate`.
2. Register a Java `Consumer` callback to listen for `WindowLayoutInfo` updates.
3. Parse the `FoldingFeature` details (state, orientation, bounds) and serialize them to JSON.
4. Dispatch the JSON payload to the Unity runtime using `UnityPlayer.UnitySendMessage`.

Java example for fold/hinge tracker:
```java
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.WindowInfoTracker;
import androidx.window.layout.WindowLayoutInfo;
import com.unity3d.player.UnityPlayer;

public class LargeScreenPlayableActivity extends UnityPlayerActivity {
    private WindowInfoTracker windowInfoTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        windowInfoTracker = WindowInfoTracker.getOrCreate(this);
        
        // Listen to WindowLayoutInfo stream
        windowInfoTracker.windowLayoutInfo(this).collect(new Consumer<WindowLayoutInfo>() {
            @Override
            public void accept(WindowLayoutInfo layoutInfo) {
                processLayoutInfo(layoutInfo);
            }
        });
    }

    private void processLayoutInfo(WindowLayoutInfo layoutInfo) {
        for (DisplayFeature feature : layoutInfo.getDisplayFeatures()) {
            if (feature instanceof FoldingFeature) {
                FoldingFeature fold = (FoldingFeature) feature;
                // Serialize state (e.g. FLAT = 1, HALF_OPENED = 2)
                String json = String.format("{\"state\": %d, \"isHingeHorizontal\": %b}", 
                    fold.getState().toString().equals("HALF_OPENED") ? 2 : 1,
                    fold.getOrientation() == FoldingFeature.Orientation.HORIZONTAL
                );
                UnityPlayer.UnitySendMessage("ConfigurationManager", "onFoldChanged", json);
            }
        }
    }
}
```

---

## 3. Unity Thread-Safe Receiver

Unity runtime execution is bound to its main thread. Calls from Java (via `UnitySendMessage`) execute asynchronously, so any UI updates or scene manipulations must be dispatched back to Unity's main thread.

### C# Receiver Setup
Attach a persistent manager (e.g., `ConfigurationManager.cs` on a GameObject named `ConfigurationManager`) to process native callbacks:

```csharp
using System;
using UnityEngine;
using UnityEngine.Events;

public class ConfigurationManager : MonoBehaviour
{
    public static ConfigurationManager Instance;
    
    // UnityEvents for scripts to register to
    public UnityEvent<string> onFoldChangedEvent;
    public UnityEvent onConfigurationChangedEvent;

    private void Awake()
    {
        if (Instance == null) { Instance = this; DontDestroyOnLoad(gameObject); }
        else { Destroy(gameObject); }
    }

    // Called via JNI from Java Layer
    public void onFoldChanged(string jsonPayload)
    {
        ExecuteOnMainUnityThread(() => {
            onFoldChangedEvent.Invoke(jsonPayload);
        });
    }

    public void onConfigurationChanged(string empty)
    {
        ExecuteOnMainUnityThread(() => {
            onConfigurationChangedEvent.Invoke();
        });
    }

    public void ExecuteOnMainUnityThread(Action action)
    {
        // Enqueue action to execute on main Unity update cycle or coroutine helper
        StartCoroutine(ExecuteActionCoroutine(action));
    }

    private System.Collections.IEnumerator ExecuteActionCoroutine(Action action)
    {
        yield return null; // Wait for next frame update
        action.Invoke();
    }
}
```

---

## 4. UI Canvas & Safe Area Alignment

Devices with physical notches, punch-holes, or screen corners require anchoring coordinates within the active hardware viewport rather than raw screen bounds.

### Implement Dynamic Safe Zone Scaling
Attach `SafeZoneUI.cs` to any canvas parent panel containing HUD controls (joysticks, buttons, menus):

1. Cache the panel's `RectTransform`.
2. Register the script to the `ConfigurationManager.onConfigurationChangedEvent`.
3. Read `Screen.safeArea` dynamically.
4. Scale min/max anchor points to match safe-area pixel coordinates.

C# Safe Area Adjustment:
```csharp
using UnityEngine;

[RequireComponent(typeof(RectTransform))]
public class SafeZoneUI : MonoBehaviour
{
    private RectTransform rectTransform;
    private Rect lastSafeArea = new Rect(0, 0, 0, 0);

    void Start()
    {
        rectTransform = GetComponent<RectTransform>();
        RefreshSafeArea();
        if (ConfigurationManager.Instance != null)
        {
            ConfigurationManager.Instance.onConfigurationChangedEvent.AddListener(RefreshSafeArea);
        }
    }

    public void RefreshSafeArea()
    {
        Rect safeArea = Screen.safeArea;
        if (safeArea == lastSafeArea) return;
        
        lastSafeArea = safeArea;

        // Convert safe area coordinates to normalized anchors [0, 1]
        Vector2 anchorMin = safeArea.position;
        Vector2 anchorMax = safeArea.position + safeArea.size;

        anchorMin.x /= Screen.width;
        anchorMin.y /= Screen.height;
        anchorMax.x /= Screen.width;
        anchorMax.y /= Screen.height;

        rectTransform.anchorMin = anchorMin;
        rectTransform.anchorMax = anchorMax;
    }
}
```

---

## 5. Enable Seamless Resizing Buffers

Resizing windows (e.g., entering split-screen or unfolding screen) causes brief camera jumps or rendering glitches. Implement a transition buffer to pause calculations while anchors adjust.

### Implement a Pause Buffer Overlay
Attach `ConfigurationResponse.cs` to a canvas overlay containing a pause state representation:

```csharp
using System.Collections;
using UnityEngine;
using UnityEngine.UI;

public class ConfigurationResponse : MonoBehaviour
{
    public GameObject pausePanel; // Visual buffer panel
    private float pauseDurationSeconds = 1.0f;

    void Start()
    {
        if (ConfigurationManager.Instance != null)
        {
            ConfigurationManager.Instance.onConfigurationChangedEvent.AddListener(OnConfigurationResized);
        }
    }

    private void OnConfigurationResized()
    {
        StartCoroutine(ResizeBufferCoroutine());
    }

    private IEnumerator ResizeBufferCoroutine()
    {
        // Pause gameplay
        Time.timeScale = 0f;
        pausePanel.SetActive(true);

        // Wait for layouts to settle
        yield return new WaitForSecondsRealtime(pauseDurationSeconds);

        // Resume gameplay
        Time.timeScale = 1f;
        pausePanel.SetActive(false);
    }
}
```

---

## 6. Verification Checklist

To confirm the optimizations function correctly:

Validate your implementation with the following checklist:

*   [ ] Verify the game does not crash when rotated or split in half-screen/multi-window mode.
*   [ ] Verify `AndroidManifest.xml` has `android:resizeableActivity="true"`.
*   [ ] Confirm UI anchors scale accurately to avoid overlapping physical screen cutout bounds.
*   [ ] Check that JNI callbacks execute updates on the main Unity Thread to prevent thread-safety exceptions.
*   [ ] Verify gameplay pausing triggers correctly and resumes after 1 second when folding state changes.

