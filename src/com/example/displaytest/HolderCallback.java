package com.example.displaytest;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class HolderCallback implements SurfaceHolder.Callback {
    static final String TAG = "HolderCallback";
    private GLRenderer mGLRenderer;
    private GLSurface mGLSurface;

    public HolderCallback(GLRenderer renderer) {
        mGLRenderer = renderer;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        mGLSurface = new GLSurface(holder.getSurface(), holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());
        mGLRenderer.addSurface(mGLSurface);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, String.format("surfaceChanged: %dx%d", width, height));
        mGLSurface.setViewport(0, 0, width, height);
        mGLRenderer.requestRender();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mGLRenderer.removeSurface(mGLSurface);
    }
}
