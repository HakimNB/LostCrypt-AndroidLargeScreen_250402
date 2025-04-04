using System.Collections;
using UnityEngine;

[RequireComponent(typeof(Camera))]
public class CameraAspectLock : MonoBehaviour
{
	[Range(0.5f, 2.2f)]
	public float maintainAspect = 1.0f;
	private Camera _camera;

    private void Awake() {
		_camera = GetComponent<Camera>();
    }

	private void Start() {
		MaintainAspectRatio();
    }

	private void Update() {
        // In-editor testing can test and simulate orientation changes
	#if UNITY_EDITOR
        MaintainAspectRatio();
	#endif
    }

    public void OnConfigurationChanged(ConfigurationManager.OrientationInfo config)
	{
		MaintainAspectRatio();
    }

    public void MaintainAspectRatio()
    {
		int viewWidth = Display.main.systemWidth;
		int viewHeight = Display.main.systemHeight;

        float scaleScreen = viewWidth / (float)viewHeight;
		Rect rect = _camera.pixelRect;
		
        if (scaleScreen > maintainAspect) {
			// use height, lock width to max at aspect scale
			rect.width = viewHeight * maintainAspect;
			rect.height = viewHeight;
		} else {
			// use width, lock height to max at aspect scale
			rect.width = viewWidth;
			rect.height = viewWidth / maintainAspect;
		}
        /*
		var widthFactor = Mathf.Floor(Screen.width / minWidth);
		var heightFactor = Mathf.Floor(Screen.height / minHeight);

		// Use the smaller one
		var factorToUse = widthFactor > heightFactor ? heightFactor : widthFactor;

		Rect rect = _camera.pixelRect;
		rect.width = minWidth * factorToUse;
		rect.height = minHeight * factorToUse;
		*/

        rect.x = Mathf.Floor((viewWidth - rect.width) * 0.5f);
		rect.y = Mathf.Floor((viewHeight - rect.height) * 0.5f);

        Debug.Log("camera view width = " + rect.width +
			" height = " + rect.height +
			" x = " + rect.x +
			" y = " + rect.y); 

        _camera.pixelRect = rect;
    }
}