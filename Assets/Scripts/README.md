# Scripts Directory Overview

The [Scripts](./) directory is the programming core of the project, housing all the custom C# scripts that dictate gameplay interactions, graphics control, and adaptive layout adjustments. It is organized into distinct subfolders targeting specific systems: gameplay logic and character movement, device posture and orientation awareness for foldable and large screen devices, and custom Unity Editor tools.

These scripts leverage Unity's Universal Render Pipeline, 2D physics, and the new Input System to deliver responsive and immersive 2D experiences. In addition, the directory contains layout adaptation components that communicate with native Android activities to dynamically reposition controls and resize UI panels based on screen aspect ratios, fold states, and hardware notches.

## Files and Subdirectories

### Editor Scripts

*   [LightColorControllerEditor.cs](./Editor/LightColorControllerEditor.cs): A custom editor script for `LightColorController` that triggers setter refreshes whenever a parameter change is detected in the Unity Inspector. This ensures that scene preview lighting automatically matches inspector edits without needing to enter play mode.
*   [ScreenshotTaker.cs](./Editor/ScreenshotTaker.cs): A simple editor utility script that captures the current editor game screen. It formats the screenshot name with the current timestamp and saves it in a local directory for design review.
*   [LargeScreenHelperTools.cs](./OrientationScripts/Editor/LargeScreenHelperTools.cs): An editor menu extension script that provides a shortcut to configure player window settings. Specifically, it updates the Unity PlayerSettings to enable the resizable window feature required for testing Android multi-window resizing.

### Gameplay Scripts

*   [CharacterAudio.cs](./GameScripts/CharacterAudio.cs): Manages the audio feedback loop for character actions, such as playing step sounds, landing bumps, or jumping grunts. It dynamically selects footstep audio clips depending on ground type and player speed.
*   [CharacterController2D.cs](./GameScripts/CharacterController2D.cs): Controls the player character's movement, jump physics, collision mapping, and sprite changes using Unity's new Input System. It updates character orientation, animations, and rigidbodies, and manages item grabbing mechanics.
*   [ConfigurationResponse.cs](./GameScripts/ConfigurationResponse.cs): Listens for layout orientation or screen fold changes and conditionally pauses the game to provide a smooth transition interface. It shows a temporary countdown overlay before automatically resuming gameplay.
*   [GameplayTrigger.cs](./GameScripts/GameplayTrigger.cs): Triggers Unity Timeline animations or cutscenes when a 2D GameObject tagged as a Player enters its trigger bounds. It can be configured to execute repeatedly or disable itself after the first activation.
*   [LightColorController.cs](./GameScripts/LightColorController.cs): Coordinates the update of child components implementing the color setter interface using a standardized time parameter (from 0 to 1). It enables artists to preview daytime/nighttime lighting transitions directly in edit mode.
*   [LightColorSetter.cs](./GameScripts/LightColorSetter.cs): Implements the color setter interface to adjust the color parameters of child Light2D components based on a evaluated gradient. This allows dynamic adjustments to local lighting arrays.
*   [LightIntensityController.cs](./GameScripts/LightIntensityController.cs): Continuously modulates the intensity of a Light2D component using a coroutine that interpolates between randomized targets within configured ranges. This creates organic flickering or pulsing behaviors suitable for lanterns or torches.
*   [MaterialColorSetter.cs](./GameScripts/MaterialColorSetter.cs): Updates named color variables on target materials by evaluating a color gradient corresponding to the controller's time. This allows custom sprite shaders to transition day and night colors on materials.
*   [MaterialVectorSetter.cs](./GameScripts/MaterialVectorSetter.cs): Maps the current GameObject's transform position to a designated vector variable in target materials. This passes spatial tracking coordinates directly to rendering shaders.
*   [ObjectActivator.cs](./GameScripts/ObjectActivator.cs): Activates or deactivates a target array of GameObjects depending on whether colliders carrying a specific tag enter or exit its trigger zone. This helps optimize scene management by loading/unloading elements dynamically.
*   [OnScreenControls.cs](./GameScripts/OnScreenControls.cs): Activates a virtual touch joystick controls canvas if the game executes on a handheld/mobile platform. It also sets the target mobile frame rate limit and handles navigation transitions back to the main menu.
*   [ParallaxLayer.cs](./GameScripts/ParallaxLayer.cs): Offsets the local GameObject's position relative to camera movement to simulate visual depth. It can be configured with distinct movement multipliers and restricted to horizontal-only panning.
*   [VisibleOnlyDuringNight.cs](./GameScripts/VisibleOnlyDuringNight.cs): Enables or disables a GameObject's Renderer depending on whether the day/night time parameter is greater than zero. This ensures certain night-specific elements are hidden during the day.

### Orientation and Layout Scripts

*   [ConfigurationManager.cs](./OrientationScripts/ConfigurationManager.cs): Serves as the primary bridge to receive screen resizing, orientation, and fold state JSON payloads from Android activities. It handles these events on the main thread and propagates updates to other UI subsystems.
*   [DebuggingInfo.cs](./OrientationScripts/DebuggingInfo.cs): Renders system telemetry data, including screen metrics and anchor adjustments, to a TextMesh Pro text label for diagnostics. It also displays the current application elapsed time since startup.
*   [NotchAware.cs](./OrientationScripts/NotchAware.cs): Retrieves device screen notch parameters using Unity's cutouts API and maps them to visual GUI box coordinates. It overlays green debugging rectangles over the notch regions to test cutout compatibility.
*   [SafeZoneUI.cs](./OrientationScripts/SafeZoneUI.cs): Automatically adjusts UI panel anchors to align precisely with the device's hardware safe area. It registers with the configuration manager to redraw and recalculate boundaries when device orientation or size changes.
