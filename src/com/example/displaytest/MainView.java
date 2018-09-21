package com.example.displaytest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Display;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.graphics.SurfaceTexture;
import android.graphics.Matrix;

public final class MainView extends Activity {
    static final String TAG = "MainView";
    static final String controlTVOutFile = "/sys/devices/platform/c0000000.soc/c0101000.display_drm_tv/enable";

    private Context mContext = null;
    private DisplayManager mDisplayManager;
    private int display_id = 0;
    private DemoPresentation2 mPresentations = null;
    Display[] presentationDisplays;

    private SurfaceView mSurfaceView;
	private GLRenderer mRenderer;
	private HolderCallback mHolderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_subphone);
        mContext = this;

        mDisplayManager = (DisplayManager)getSystemService(Context.DISPLAY_SERVICE);
        presentationDisplays = mDisplayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        Log.d(TAG, "@@@There are currently " + presentationDisplays.length + " displays connected.");

        mSurfaceView = (SurfaceView)findViewById(R.id.previewFrame1);
		mRenderer = new GLRenderer();
		mHolderCallback = new HolderCallback(mRenderer);

		mSurfaceView.getHolder().addCallback(mHolderCallback);

		mRenderer.startRender();
		mRenderer.startPreview();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Be sure to call the super class.
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onResume() {
        // Be sure to call the super class.
        super.onResume();
        Log.d(TAG, "onResume");

        mDisplayManager.registerDisplayListener(mDisplayListener, null);
        enableTVOut(true);
    }

    @Override
    protected void onPause() {
        // Be sure to call the super class.
        super.onPause();
        mDisplayManager.unregisterDisplayListener(mDisplayListener);
        hidePresentation2();
        Log.d(TAG, "onPause");
        enableTVOut(false);
    }

    @Override
    protected void onDestroy() {
        // Be sure to call the super class.
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        hidePresentation2();

        mDisplayManager.unregisterDisplayListener(mDisplayListener);

        if(mPresentations != null) {
            mPresentations.dismiss();
            mPresentations = null;
        }

		mRenderer.stopPreview();
		mRenderer.release();
		mRenderer = null;
    }

    private void enableTVOut(boolean enable) {
        String out = "";
        if (enable)
            out = "1";
        else
            out = "0";

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(controlTVOutFile));
            bw.write(out);
            bw.flush();
            bw.close();
            Log.d(TAG, "enable:" + enable + " TV OUT");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final DisplayManager.DisplayListener mDisplayListener =
        new DisplayManager.DisplayListener() {
            @Override
            public void onDisplayAdded(int displayId) {
                Log.d(TAG, "Display #" + displayId + " added.");
                showPresentationView();
            }

            @Override
            public void onDisplayChanged(int displayId) {
                Log.d(TAG, "Display #" + displayId + " changed.");
            }

            @Override
            public void onDisplayRemoved(int displayId) {
                Log.d(TAG, "Display #" + displayId + " removed.");
                hidePresentation2();
            }
        };

    private final DialogInterface.OnDismissListener mOnDismissListener =
        new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                DemoPresentation2 presentation = (DemoPresentation2)dialog;
                int displayId = presentation.getDisplay().getDisplayId();
                Log.d(TAG, "OnDismissListener() - display #" + displayId + " was dismissed.");
            }
        };

    void showPresentationView() {
        presentationDisplays = mDisplayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        Log.d(TAG, "###There are currently " + presentationDisplays.length + " displays connected.");

        if (presentationDisplays.length > 0) {
            for (Display dp : presentationDisplays) {
                display_id = dp.getDisplayId();
                Log.d(TAG, "showVideo() : display_id=" +  display_id);

                if(display_id == 0)
                    continue;

                showPresentation2(dp);
                break;
            }
        }
    }

    private void showPresentation2(Display display) {
        if (mPresentations != null) {
            Log.d(TAG, "presentation is not null.");
            return;
        }

        Log.d(TAG, "Showing presentation2 on display #" + display_id + ".");
        try {
			mPresentations = new DemoPresentation2(this, display, new HolderCallback(mRenderer));
			mPresentations.show();
			mPresentations.setOnDismissListener(mOnDismissListener);
        }catch (Exception ex) {
            Log.e(TAG, "showPresentation Exception. ex=" + ex.toString());
        }
    }

    private void hidePresentation2() {
        if (mPresentations == null) {
            Log.d(TAG, "presentation is null.");
            return;
        }

        Log.d(TAG, "Dismissing presentation on display #" + display_id + ".");
        try {
            mPresentations.dismiss();
        }catch (Exception ex) {
            Log.e(TAG, "hidePresentation Exception");
        }

        mPresentations = null;
        display_id = 0;
    }
}

