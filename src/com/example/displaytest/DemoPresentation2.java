package com.example.displaytest;

import android.hardware.Camera;

import android.app.Presentation;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Display;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

/**
 * The presentation to show on the secondary display.
 *
 * Note that the presentation display may have different metrics from the display on which
 * the main activity is showing so we must be careful to use the presentation's
 * own {@link Context} whenever we load resources.
 */
public final class DemoPresentation2 extends Presentation {
    static final String TAG = "DemoPresentation2";

    private Context mContext = null;
	private SurfaceView mSurfaceView;
	private HolderCallback mHolderCallback;

    public DemoPresentation2(Context context, Display display, HolderCallback holder) {
        super(context, display);
        mContext = context;
		mHolderCallback = holder;
        Log.d(TAG, "DemoPresentation(): display id  " + display.getDisplayId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Be sure to call the super class.
        super.onCreate(savedInstanceState);

        // Get the resources for the context of the presentation.
        // Notice that we are getting the resources from the context of the presentation.
        Resources r = getContext().getResources();

        // Inflate the layout.
        setContentView(R.layout.activity_camera);
        mSurfaceView = (SurfaceView)findViewById(R.id.previewFrame);
		mSurfaceView.getHolder().addCallback(mHolderCallback);
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "[onStart]");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "[onStop]");
        super.onStop();

        try {
        }catch (Exception e) {
            Log.i(TAG, "remove view Exception");
        }
    }
}
