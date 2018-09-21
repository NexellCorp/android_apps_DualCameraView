package com.example.displaytest;

import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraInstance {
    public static final String LOG_TAG = "CameraInstance";

    private static final String ASSERT_MSG = "CameraDevice is null!!!";

    private Camera mCameraDevice;
    private Camera.Parameters mParams;

    private boolean mIsPreviewing = false;

    private int mDefaultCameraID = -1;

    private static CameraInstance mThisInstance;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private int mPreferPreviewWidth;
    private int mPreferPreviewHeight;

    private int mFacing = 0;

    private CameraInstance() {}

    public static synchronized CameraInstance getInstance() {
        if(mThisInstance == null) {
            mThisInstance = new CameraInstance();
        }
        return mThisInstance;
    }

    public boolean isPreviewing() { return mIsPreviewing; }

    public int previewWidth() { return mPreviewWidth; }
    public int previewHeight() { return mPreviewHeight; }

    public void setPreferPreviewSize(int w, int h) {
        mPreferPreviewHeight = w;
        mPreferPreviewWidth = h;
    }

    public interface CameraOpenCallback {
        void cameraReady();
    }

    public boolean tryOpenCamera(CameraOpenCallback callback) {
        return tryOpenCamera(callback, Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    public int getFacing() {
        return mFacing;
    }

    public synchronized boolean tryOpenCamera(CameraOpenCallback callback, int facing) {
        Log.i(LOG_TAG, "try open camera " + facing);

        try {
            stopPreview();
            if(mCameraDevice != null)
                mCameraDevice.release();

			mDefaultCameraID = facing;

            if(mDefaultCameraID >= 0) {
                mCameraDevice = Camera.open(mDefaultCameraID);
            } else {
                mCameraDevice = Camera.open();
                mFacing = Camera.CameraInfo.CAMERA_FACING_BACK; //default: back facing
            }
        } catch(Exception e) {
            Log.e(LOG_TAG, "Open Camera Failed!");
            e.printStackTrace();
            mCameraDevice = null;
            return false;
        }

        if(mCameraDevice != null) {
            Log.i(LOG_TAG, "Camera opened!");

            try {
                initCamera();
            } catch (Exception e) {
                mCameraDevice.release();
                mCameraDevice = null;
                return false;
            }

            if (callback != null) {
                callback.cameraReady();
            }

            return true;
        }

        return false;
    }

    public synchronized void stopCamera() {
        if(mCameraDevice != null) {
            mIsPreviewing = false;
            mCameraDevice.stopPreview();
            mCameraDevice.release();
            mCameraDevice = null;
        }
    }

    public boolean isCameraOpened() {
        return mCameraDevice != null;
    }

    public synchronized void startPreview(SurfaceTexture texture) {
        Log.i(LOG_TAG, "Camera startPreview...");
        if(mIsPreviewing) {
            Log.e(LOG_TAG, "Err: camera is previewing...");
            return ;
        }

        if(mCameraDevice != null) {
            try {
                mCameraDevice.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mCameraDevice.startPreview();
            mIsPreviewing = true;
        }
    }

    public synchronized void stopPreview() {
        if(mIsPreviewing && mCameraDevice != null) {
            Log.i(LOG_TAG, "Camera stopPreview...");
            mIsPreviewing = false;
            mCameraDevice.stopPreview();
        }
    }

    public synchronized Camera.Parameters getParams() {
        if(mCameraDevice != null)
            return mCameraDevice.getParameters();
        assert mCameraDevice != null : ASSERT_MSG;
        return null;
    }

    public synchronized void setParams(Camera.Parameters param) {
        if(mCameraDevice != null) {
            mParams = param;
            mCameraDevice.setParameters(mParams);
        }
        assert mCameraDevice != null : ASSERT_MSG;
    }

    public Camera getCameraDevice() {
        return mCameraDevice;
    }

    private Comparator<Camera.Size> comparatorBigger = new Comparator<Camera.Size>() {
        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            int w = rhs.width - lhs.width;
            if(w == 0)
                return rhs.height - lhs.height;
            return w;
        }
    };

    private Comparator<Camera.Size> comparatorSmaller= new Comparator<Camera.Size>() {
        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            int w = lhs.width - rhs.width;
            if(w == 0)
                return lhs.height - rhs.height;
            return w;
        }
    };

    public void initCamera() {
        if(mCameraDevice == null) {
            Log.e(LOG_TAG, "initCamera: Camera is not opened!");
            return;
        }

        mParams = mCameraDevice.getParameters();

        List<Camera.Size> prevSizes = mParams.getSupportedPreviewSizes();
        Camera.Size prevSz = null;

        Collections.sort(prevSizes, comparatorBigger);

        for(Camera.Size sz : prevSizes) {
            Log.i(LOG_TAG, String.format("Supported preview size: %d x %d", sz.width, sz.height));
            if(prevSz == null || (sz.width >= mPreferPreviewWidth && sz.height >= mPreferPreviewHeight)) {
                prevSz = sz;
            }
        }

        mParams.setPreviewSize(prevSz.width, prevSz.height);

        try {
            mCameraDevice.setParameters(mParams);
        }catch (Exception e) {
            e.printStackTrace();
        }

        mParams = mCameraDevice.getParameters();

        Camera.Size szPrev = mParams.getPreviewSize();

        mPreviewWidth = szPrev.width;
        mPreviewHeight = szPrev.height;

        Log.i(LOG_TAG, String.format("Camera Preview Size: %d x %d", szPrev.width, szPrev.height));
    }
}
