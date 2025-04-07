using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Text;
using UnityEngine.Events;

public class ConfigurationManager : MonoBehaviour {
	public Action<OrientationInfo> ActionOnOrientationChange;
	public Action<FoldInfo> ActionOnFoldChange;

	public Action<int> ActionOnDualDisplayAvailabilityChanged;

	AndroidJavaObject foldablePlayerActivity = null;

	// For any scene/inspector based interest in responding to events
	public UnityEvent<OrientationInfo> OnConfigurationChanged;
	public UnityEvent<FoldInfo> OnFoldChanged;

	public UnityEvent<int> OnDualDisplayAvailabilityChanged;

	// To use the JsonUtility.FromJson, we start with a serializable struct or class with public fields
	[System.Serializable]
	public class OrientationInfo {
		public string rotation;
		public string orientation;
		public int screenWidth;
		public int screenHeight;
		public int visibleFrameLeft;
		public int visibleFrameRight;
		public int visibleFrameTop;
		public int visibleFrameBottom;
	}

	[System.Serializable]
	public class FoldInfo {
		public string orientation;
		public string state;
		public int isSeparating;
		public int boundsLeft;
		public int boundsTop;
		public int boundsRight;
		public int boundsBottom;
	}

	void Awake() {
		Debug.Log("Awake Configuration Manager");

		// Grab a copy of the Android objects that we might reference for foldable activity or hinge info
		// There are only valid when we interface with our LargeScreenPlayableActivity.java class, and this is only loaded on Android builds
		if (Application.platform == RuntimePlatform.Android) {
			AndroidJavaClass unityPlayerClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
			foldablePlayerActivity = unityPlayerClass.GetStatic<AndroidJavaObject>("currentActivity");
			var staticCalcClass = new AndroidJavaClass("androidx.window.layout.WindowMetricsCalculator");
		}

		ActionOnOrientationChange += HandleLocalOnConfigurationChanged;
		ActionOnFoldChange += HandleLocalOnFoldChanged;
		ActionOnDualDisplayAvailabilityChanged += HandleOnDualDisplayAvailabilityChanged;
	}

	private void OnDestroy() {
        Debug.Log("Destroy Configuration Manager");
		ActionOnOrientationChange -= HandleLocalOnConfigurationChanged;
		ActionOnFoldChange -= HandleLocalOnFoldChanged;
		ActionOnDualDisplayAvailabilityChanged -= HandleOnDualDisplayAvailabilityChanged;
	}

	// This will be called from the OverrideForLargeScreen.java class, from the activity callback onConfigurationChanged
	public void onConfigurationChanged(string strOrientationInfo) {
		OrientationInfo info = JsonUtility.FromJson<OrientationInfo>(strOrientationInfo);
		Debug.LogFormat(string.Format("orientation: {1}, rotation: {2}\n   width/height: {3}/{4}\n   l/r/t/b: {5}/{6}/{7}/{8}",
			0, info.orientation, info.rotation, info.screenWidth, info.screenHeight, info.visibleFrameLeft, info.visibleFrameRight, info.visibleFrameTop, info.visibleFrameBottom));

		// Always call the refresh from the main Unity thread, since this is where the UI updates occur
		StartCoroutine(ExecuteOnMainUnityThread(ActionOnOrientationChange, info));
	}

	public bool isDualDisplayAvailable() {
		bool bAvailable = false;
		if ( Application.platform == RuntimePlatform.Android ) {
			bAvailable = foldablePlayerActivity.Call<Boolean>("isDualDisplayAvailable");
		}
		Debug.Log("ConfigurationManager.isDualDisplayAvailable: " + bAvailable);
		return bAvailable;
	}

	public void onDualDisplayAvailabilityChanged(string strAvailability) {
		Debug.Log("ConfigurationManager.onDualDisplayAvailabilityChanged : " + strAvailability);
		int iAvailable = 0;
		try { 
			iAvailable = Int32.Parse(strAvailability);
		} catch ( FormatException e ) {
			Debug.Log("ConfigurationManager.onDualDisplayAvailabilityChanged: " + e.Message);
		}

		// Always call the refresh from the main Unity thread, since this is where the UI updates occur
		StartCoroutine(ExecuteOnMainUnityThread(ActionOnDualDisplayAvailabilityChanged, iAvailable));
	}

	public void toggleDualDisplay(bool turnOn) {
		Debug.Log("ConfigurationManager.toggleDualDisplay: " + turnOn);
		if ( Application.platform == RuntimePlatform.Android ) {
			foldablePlayerActivity.Call("toggleDualScreenMode", turnOn);
		}
	}

	public void onFoldChanged(string strFoldInfo) {
		FoldInfo info = JsonUtility.FromJson<FoldInfo>(strFoldInfo);
		Debug.LogFormat(string.Format("orientation: {0}, state: {1}, isSeparating: {2}\n   l/r/t/b: {3}/{4}/{5}/{6}",
			info.orientation, info.state, info.isSeparating, info.boundsLeft, info.boundsRight, info.boundsTop, info.boundsBottom));

		// Always call the refresh from the main Unity thread, since this is where the UI updates occur
		StartCoroutine(ExecuteOnMainUnityThread(ActionOnFoldChange, info));
	}

	public string getFoldableState {
		get {
			if (Application.platform == RuntimePlatform.Android) {
				var foldingFeatureObject = foldablePlayerActivity.Call<AndroidJavaObject>("getFoldingFeature");
				if (foldingFeatureObject == null) {
					Debug.Log("[FoldableState] Returning NONE");
					return "NONE";
				} else {
					var state = foldingFeatureObject.Call<AndroidJavaObject>("getState");
					var stateString = state.Call<string>("toString");
					Debug.LogFormat("[FoldableState] Returning {0}", stateString);
					return stateString;
				}
			} else {
				return "NONE";
			}
		}
	}

	IEnumerator ExecuteOnMainUnityThread<T>(Action<T> whichAction, T data) {
		yield return null;  // Will be called from main thread on next update
		whichAction.Invoke(data);
	}

	void HandleLocalOnConfigurationChanged(OrientationInfo info) {
		OnConfigurationChanged?.Invoke(info);
	}
	void HandleLocalOnFoldChanged(FoldInfo info) {
		OnFoldChanged?.Invoke(info);
	}

	void HandleOnDualDisplayAvailabilityChanged(int availability) {
		OnDualDisplayAvailabilityChanged?.Invoke(availability);
	}
}