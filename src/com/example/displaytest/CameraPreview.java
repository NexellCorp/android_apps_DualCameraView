package com.example.displaytest;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder mHolder; // surfaceview를 컨트롤하기 위한 홀더
    Camera mCamera = null; // 카메가 객체생성

    public CameraPreview(Context context, Camera camera) {
        super(context);
	mCamera = camera;
    }
    
    public CameraPreview(Context context) {
        super(context);
    }

    public SurfaceHolder getmHolder() {
        return mHolder;
    }

    public void setmHolder(SurfaceHolder hd) {
        mHolder = hd;
    }

    public void setCamera(SurfaceHolder hd) {
        setmHolder(hd);
        mHolder.addCallback(this); // SurfaceHolder.Callback 인터페이스 장착. Surface가 생성/소멸되었음을 알 수 있습니다.
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // 카메라가 surfaceview를 독정하기 위한 타입설정. 버퍼사용안함.
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // Surface가 생성가 메모리에 생성될때 호출됨.
        //되었다면, 카메라의 인스턴스를 받아온 후 카메라의
        // Preview 를 표시할 위치를 설정합니다.
	if (mCamera == null)
		mCamera = Camera.open(0);
        try {
		mCamera.setPreviewDisplay(mHolder);
		Log.d("test", "CameraPreview");
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
            // TODO: add more exception handling logic here
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // 다른 화면으로 돌아가면, Surface가 소멸됩니다. 따라서 카메라의 Preview도
        // 중지해야 합니다. 카메라는 공유할 수 있는 자원이 아니기에, 사용하지 않을
        // 경우 -액티비티가 일시정지 상태가 된 경우 등 - 자원을 반환해야합니다.
        try {
            if (mCamera != null) {
                Log.d("test", "surfaceDestroyed - stopPreview");
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }

            //mHolder.removeCallback(this);
        }catch (Exception ex) {
            Log.d("test", "surfaceDestroyed - Exception");
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.d("test", "surfaceChanged()>>>>> width: " + w + ", height: " + h);
        if (holder.getSurface() == null){
            // 프리뷰가 존재하지 않을때
            return;
        }

        mCamera.stopPreview();
        //surfaceview가 보여지기 전, 또는 사이즈변화가 있을때 호출됨.
        Camera.Parameters parameters = mCamera.getParameters();
        List<Size> arSize = parameters.getSupportedPreviewSizes();
        if(arSize==null){
            parameters.setPreviewSize(w, h);
            String msg;
            msg= "width:"+w+"height:"+h;
            Log.i("tag", msg);

        } else {
            int diff = 10000;
            Size opti = null;
            for(Size s : arSize){
                if(Math.abs(s.height - h) < diff) {
                    diff = Math.abs(s.height - h);
                    opti = s;
                }
            }
            Log.d("test", "test>>>>> width: " + opti.width + ", height: " + opti.height);
            if(opti != null) {
                parameters.setPreviewSize(opti.width, opti.height);
            }
        }
        mCamera.setParameters(parameters);
        mCamera.startPreview();
        arSize.clear();
    }
}
