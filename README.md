# Android Large Screen x Unity Sample Project
This is a Large Screen optimized behavior example project, based on the Unity 2D demonstration project [_Lost Crypt_](https://assetstore.unity.com/packages/essentials/tutorial-projects/lost-crypt-2d-sample-project-158673).

Supporting Large Screen and Foldable devices requires a number of changes to the Unity build options, as well as considerations in the layout of your camera and UI canvases.

### Begin with Aspect Ratio Support
To start, it's essential to implement support for various aspect ratios in your Android Large Screen application. This ensures your game or application appears correctly across different screen sizes. To do this, you should utilize the full screen resizable setting. You might consider adjusting the camera or canvas aspect ratio to better fit different screens.  Settings in the example are viewed in the **Build Settings**, and in the _Plugins\Android\AndroidManifest.xml_ file.

### Expand to Full Screen Utilization
After ensuring that your game can adapt to different aspect ratios, the next step is to ensure your game can utilize the entire screen. This makes gameplay more immersive and can enhance the user experience. To achieve this, you should use anchors in your game's UI and camera settings to automatically adjust to the screen size. This allows UI elements to maintain their positions relative to the screen size. Examples of anchoring settings in the UI canvas are provided in the scene called **Anchoring**.

### Optimize for Foldable Devices
With the advent of foldable devices, it's beneficial to add support for these in your game. You can use Android's Jetpack WindowManager to detect the device's folding posture and adjust your game's layout accordingly.  This project provides an example scene called **HingeAware** that contains a _ConfigurationManager_ Monobehaviour and responds to the different folding states of the target device with UnityEvent callbacks.

