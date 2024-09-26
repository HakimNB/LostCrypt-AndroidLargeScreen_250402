# Android Large Screen x Unity Sample Project
This is a Large Screen optimized behavior example project, based on the Unity 2D demonstration project [_Lost Crypt_](https://assetstore.unity.com/packages/essentials/tutorial-projects/lost-crypt-2d-sample-project-158673).

Supporting Large Screen and Foldable devices uses the new [_AndroidApplication.onConfigurationChanged_](https://docs.unity3d.com/6000.0/Documentation/ScriptReference/Android.AndroidApplication-onConfigurationChanged.html) callback that is available in Unity 6.  The Unity build option for resizable screens is also required, as well as considerations in the layout of your camera and UI canvases.

This sample responds to configuration changes and automatically pauses the gameplay for a short period of time, allowing the user to reorient.  For devices that are hinge-enabled, the sample also provides a demonstration of splitting the screen when in landscape mode and hinge angles between 40 and 60 degrees to simulate a mini-game handheld console experience.
