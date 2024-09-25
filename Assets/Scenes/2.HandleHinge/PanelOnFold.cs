using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Android;
using UnityEngine.InputSystem;

public class PanelOnFold : MonoBehaviour {
	public Camera mainCamera;
	public Camera splitFillCamera;
	public RectTransform panelRect;

	public List<GameObject> disableOnFold;
	public List<GameObject> enableOnFold;

	public bool simulateFoldInEditor;
    public float currentHingeAngle { get { return null != HingeAngle.current ? HingeAngle.current.angle.ReadValue() : 0.0f; } }

    private void Awake() {
		ResetCameras();

        if (HingeAngle.current != null) {
            InputSystem.EnableDevice(HingeAngle.current);
        }
    }

	void ResetCameras() {
		mainCamera.rect = new Rect(0.0f, 0.0f, 1.0f, 1.0f);
		splitFillCamera.rect = new Rect(0.0f, 0.0f, 1.0f, 1.0f);
		splitFillCamera.enabled = false;
		panelRect.gameObject.SetActive(false);
		panelRect.anchorMax = new Vector2(0.0f, 1.0f);
		foreach (var go in disableOnFold) { go.SetActive(true); }
		foreach (var go in enableOnFold) { go.SetActive(false); }
	}

	public void OnOrientationChange(AndroidConfiguration orientationInfo) {
		if (orientationInfo.orientation == AndroidOrientation.Portrait) {
			ResetCameras();
		}
	}

	void OnFoldChange(bool horizontalMode) {
		// If we are in a separating state and half-opened, split the screen
		if (horizontalMode) {
			// If the screen is already split, don't re-apply
			if (splitFillCamera.enabled)
				return;

			float yAnchor = 0.5f;

			// Resize the main camera to fit in the "top" portion of the screen
			mainCamera.rect = new Rect(0.0f, yAnchor, 1.0f, 1.0f);

			// ...while the panelRect is set on the lower portion of the screen.
			// Since panelRect is set to render as a ScreenSpace-Camera, let the camera rect determine render size
			splitFillCamera.rect = new Rect(0.0f, -yAnchor, 1.0f, 1.0f);
			
			panelRect.gameObject.SetActive(true);
			foreach (var go in disableOnFold) { go.SetActive(false); }
			foreach (var go in enableOnFold) { go.SetActive(true); }

			panelRect.anchorMin = Vector2.zero;
			panelRect.anchorMax = new Vector2(1.0f, 1.0f);
			panelRect.ForceUpdateRectTransforms();

			splitFillCamera.enabled = true;
		} else {
			ResetCameras();
		}
	}

	private void Update() {
		if (Application.isEditor) {
			if (simulateFoldInEditor) {
				OnFoldChange(!splitFillCamera.enabled);
				simulateFoldInEditor = false;
			}
		} else {
			if (null != HingeAngle.current && (Screen.orientation == ScreenOrientation.LandscapeLeft || Screen.orientation == ScreenOrientation.LandscapeRight)) {
				// Check for changes to the hinge angle, and if we're in horizontal mode then activate the split camera
				float hingeAngle = HingeAngle.current.angle.ReadValue();
				if (hingeAngle > 40.0f && hingeAngle < 65.0f && !splitFillCamera.enabled)
					OnFoldChange(true);
				else if (splitFillCamera.enabled)
					OnFoldChange(false);
            }
        }
	}
}