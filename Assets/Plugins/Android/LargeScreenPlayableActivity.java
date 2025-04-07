package com.unity.lostcryptlargescreenexample;

import com.unity3d.player.UnityPlayerActivity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.*;
import android.view.WindowManager;
import android.util.Log;
import android.view.Display;
import android.util.DisplayMetrics;

import org.json.JSONObject;
import org.json.JSONException;
import android.graphics.Rect;
import android.view.*;
import android.widget.*;

import androidx.window.area.WindowAreaCapability;
import androidx.window.area.WindowAreaController;
import androidx.window.area.WindowAreaInfo;
import androidx.window.area.WindowAreaPresentationSessionCallback;
import androidx.window.area.WindowAreaSessionPresenter;
import androidx.window.java.area.WindowAreaControllerCallbackAdapter;
import androidx.window.java.layout.WindowInfoTrackerCallbackAdapter;

import androidx.window.layout.DisplayFeature;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.WindowInfoTracker;
import androidx.window.layout.WindowLayoutInfo;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import java.util.concurrent.Executor;

// For hinge angle sensor readings
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


import androidx.core.content.*;


public class LargeScreenPlayableActivity extends UnityPlayerActivity implements WindowAreaPresentationSessionCallback {
	String TAG = "LargeScreenPlayable";
	private SensorManager mSensorManager;
    private Sensor mHingeAngleSensor;
    private SensorEventListener mSensorListener;
    private float lastValue = -1.0f;
	
    static Context mContext;
    FoldingFeature lastFoldingFeature = null;
    WindowInfoTrackerCallbackAdapter wit;
    private final LayoutStateChangeCallback layoutStateChangeCallback =
            new LayoutStateChangeCallback();

    // + DD
    private WindowAreaControllerCallbackAdapter windowAreaController = null;
    private Executor displayExecutor = null;
    private WindowAreaSessionPresenter windowAreaSession = null;
    private WindowAreaInfo windowAreaInfo = null;
    private WindowAreaCapability.Status capabilityStatus  =
            WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNSUPPORTED;

    private WindowAreaCapability.Operation dualScreenOperation =
            WindowAreaCapability.Operation.OPERATION_PRESENT_ON_AREA;
    private WindowAreaCapability.Operation rearDisplayOperation =
            WindowAreaCapability.Operation.OPERATION_TRANSFER_ACTIVITY_TO_AREA;

    private boolean isDualDisplayAvailable = false;

    private void updateUI() {
        if (capabilityStatus.equals(WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNSUPPORTED)) {
            // The selected display mode is not supported on this device.
            isDualDisplayAvailable = false;
        } else if (capabilityStatus.equals(WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNAVAILABLE)) {
            // The selected display mode is not available.
            isDualDisplayAvailable = false;
        } else if (capabilityStatus.equals(WindowAreaCapability.Status.WINDOW_AREA_STATUS_AVAILABLE)) {
            // The selected display mode is available and can be enabled.
            // toggleDualScreenMode(true);
            isDualDisplayAvailable = true;
        } else if (capabilityStatus.equals(WindowAreaCapability.Status.WINDOW_AREA_STATUS_ACTIVE)) {
            // The selected display mode is already active.
        } else {
            // The selected display mode status is unknown.
            isDualDisplayAvailable = false;
        }

        // This will be sent to the C# layer in Unity, and can be received by the gameObject "ConfigurationManager"
        String param = isDualDisplayAvailable ? "1" : "0";
        mUnityPlayer.UnitySendMessage("ConfigurationManager", "onDualDisplayAvailabilityChanged", param);
    }

    /**
     * Check whether the system support dual display
     * @return true - if the second display is available and can be turned on
     */
    public boolean isDualDisplayAvailable() {
        Log.d("DualDisplay", "DualDisplay isDualDisplayAvailable: " + isDualDisplayAvailable);
        return isDualDisplayAvailable;
    }

    /**
     * Check whether the secondary display is active
     * @return true - if the second display is turned on, false otherwise
     */
    public boolean isDualDisplayActive() {
        Log.d("DualDisplay", "DualDisplay isDualDisplayActive: " + (windowAreaSession != null));
        return windowAreaSession != null;
    }

    private void toggleDualScreenMode(boolean turnOn) {
        Log.d("DualDisplay", "DualDisplay toggleDualScreenMode: " + turnOn + " isDualDisplayAvailable: " + isDualDisplayAvailable);
        if ( !isDualDisplayAvailable ) {
            return;
        }
        if ( turnOn ) {
            // turn dual display on
            if ( windowAreaSession == null ) {
                Binder token = windowAreaInfo.getToken();
                windowAreaController.presentContentOnWindowArea(token, this, displayExecutor, this);
            }
        } else {
            // turn dual display off
            if ( windowAreaSession != null ) {
                windowAreaSession.close();
            }
        }
    }

    private void updateCapabilities() {
        Log.d("DualDisplay", "DualDisplay updateCapabilities");
        windowAreaController.addWindowAreaInfoListListener(displayExecutor,
                windowAreaInfos -> {
                    Log.d("DualDisplay", "DualDisplay updateCapabilities size: " + windowAreaInfos.stream().count());
                    for(WindowAreaInfo newInfo : windowAreaInfos){
                        Log.d("DualDisplay", "DualDisplay updateCapabilities newInfo: " + newInfo.toString());
                        if(newInfo.getType().equals(WindowAreaInfo.Type.TYPE_REAR_FACING)){
                            windowAreaInfo = newInfo;
                            capabilityStatus = newInfo.getCapability(dualScreenOperation).getStatus();
                            break;
                        }
                        Log.d("DualDisplay", "DualDisplay windowAreaInfo: " + newInfo.getCapability(dualScreenOperation).getStatus());
                    }
                    updateUI();
                });
    }

    @Override
    public void onSessionStarted(WindowAreaSessionPresenter session) {
        windowAreaSession = session;
        TextView view = new TextView(session.getContext());
        view.setText("Hello world, from the other screen!");
        session.setContentView(view);
    }

    @Override
    public void onSessionEnded(Throwable t) {
        if(t != null) {
            Log.e(TAG, "DualDisplay Something was broken: " + t.getMessage());
        }
        windowAreaSession = null;
    }

    @Override
    public void onContainerVisibilityChanged(boolean isVisible) {
        Log.d(TAG, "DualDisplay onContainerVisibilityChanged. isVisible = " + isVisible);
    }
    // - DD
	

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mContext = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        }

        wit = new WindowInfoTrackerCallbackAdapter(WindowInfoTracker.getOrCreate(this));
        wit.addWindowLayoutInfoListener(
            this, Runnable::run, layoutStateChangeCallback);

        // + DD
        displayExecutor = ContextCompat.getMainExecutor(this);
        windowAreaController = new WindowAreaControllerCallbackAdapter(WindowAreaController.getOrCreate());

        updateCapabilities();
        // - DD
    }

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG, "on Configuration changed: " + newConfig.toString());

        // winConfig={ mBounds=Rect(0, 0 - 1080, 2092) mAppBounds=Rect(0, 0 - 1080, 1896) mMaxBounds=Rect(0, 0 - 1080, 2092) mDisplayRotation=ROTATION_180 mWindowingMode=fullscreen mDisplayWindowingMode=fullscreen mActivityType=standard mAlwaysOnTop=undefined mRotation=ROTATION_180}}
        try {
            Display display = getWindowManager().getDefaultDisplay();
            JSONObject json = new JSONObject();

            int rotation = display.getRotation();
            if (rotation == Surface.ROTATION_0)
                json.put("rotation", "ROTATION_0");
            else if (rotation == Surface.ROTATION_90)
                json.put("rotation", "ROTATION_90");
            else if (rotation == Surface.ROTATION_180)
                json.put("rotation", "ROTATION_180");
            else if (rotation == Surface.ROTATION_270)
                json.put("rotation", "ROTATION_270");

            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
                json.put("orientation", "ORIENTATION_PORTRAIT");
            else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
                json.put("orientation", "ORIENTATION_LANDSCAPE");
            else
                json.put("orientation", "ORIENTATION_UNDEFINED");

            DisplayMetrics screenMetrics = new DisplayMetrics();
            display.getMetrics(screenMetrics);
            json.put("screenWidth", screenMetrics.widthPixels);
            json.put("screenHeight", screenMetrics.heightPixels);

            Rect r = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            json.put("visibleFrameLeft", r.left);
            json.put("visibleFrameRight", r.right);
            json.put("visibleFrameTop", r.top);
            json.put("visibleFrameBottom", r.bottom);

		    // This will be sent to the C# layer in Unity, and can be received by the gameObject "ConfigurationManager"
            mUnityPlayer.UnitySendMessage("ConfigurationManager", "onConfigurationChanged", json.toString());
        } catch (JSONException e) {
        }  
    }

    protected void HandleFoldingFeatures(FoldingFeature foldingFeature) {        
         try {
            JSONObject json = new JSONObject();
            if (foldingFeature != null) {
                if (foldingFeature.getOrientation() == FoldingFeature.Orientation.HORIZONTAL) {
                    json.put("orientation", "HINGE_ORIENTATION_HORIZONTAL");
                } else {
                    json.put("orientation", "HINGE_ORIENTATION_VERTICAL");
                }

                if (foldingFeature.getState() == FoldingFeature.State.FLAT) {
                    json.put("state", "FLAT");
                } else {
                    json.put("state", "HALF_OPENED");
                }
                            
                json.put("isSeparating", foldingFeature.isSeparating() ? "1" : "0");
                Rect r = foldingFeature.getBounds();
                json.put("boundsLeft", r.left);
                json.put("boundsTop", r.top);
                json.put("boundsRight", r.right);
                json.put("boundsBottom", r.bottom);
            } else {
                json.put("state", "CLOSED");
            }
            Log.d(TAG, "HandleFoldingFeatures: " + json.toString());    
            mUnityPlayer.UnitySendMessage("ConfigurationManager", "onFoldChanged", json.toString());
        } catch (JSONException e) {
            Log.d(TAG, "Exception json");
            throw new RuntimeException(e);
        }
    }
	
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onLayoutStateChange(WindowLayoutInfo newLayoutInfo){
        Log.d(TAG, "onLayoutStateChange: " + newLayoutInfo.toString());

        lastFoldingFeature = null;
        if (newLayoutInfo.getDisplayFeatures().size() > 0) {
            newLayoutInfo.getDisplayFeatures().forEach(displayFeature -> {
                if(displayFeature instanceof FoldingFeature)
                {   
                    // only set if it's a fold, not other feature type. only works for single-fold devices.
                    FoldingFeature foldingFeature = (FoldingFeature)displayFeature;
                    lastFoldingFeature = foldingFeature;
                    HandleFoldingFeatures(foldingFeature);
                    Log.d(TAG, "Fold changed: " + foldingFeature.toString());

                } else {
                    Log.d(TAG, "Fold changed: [Closed]");
                }
            });
        }  
    }

    Executor runOnUiThreadExecutor()
    {
        return new MyExecutor();
    }
	
    class MyExecutor implements Executor
    {
        Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    }

    public FoldingFeature getFoldingFeature()
    {
        return lastFoldingFeature;
    }

    class LayoutStateChangeCallback implements Consumer<WindowLayoutInfo> {
        @Override
        public void accept(WindowLayoutInfo newLayoutInfo) {
            // Use newLayoutInfo to update the Layout
            LargeScreenPlayableActivity.this.runOnUiThread( () -> {
               LargeScreenPlayableActivity.this.onLayoutStateChange(newLayoutInfo);
           });
        }
    }
}