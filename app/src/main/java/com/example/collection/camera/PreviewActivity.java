package com.example.collection.camera;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.collection.R;

import java.io.IOException;


public class PreviewActivity extends AppCompatActivity {
    private static final String TAG = "PreviewActivity";

    private Camera mCamera;
    private SurfaceView mSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        mSurface = findViewById(R.id.surface);
        SurfaceHolder holder = mSurface.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                Log.d(TAG, "surfaceCreated");
                nativeSetVideoSurface(holder.getSurface());
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                // TODO Auto-generated method stub

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        mCamera.stopPreview();
        mCamera.release();
    }

    private void openCamera() {
        mCamera = Camera.open(0);

        Camera.Parameters parameters = mCamera.getParameters();

        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);//设置关闭闪光灯
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); //对焦设置为自动
        parameters.setPictureFormat(ImageFormat.NV21);//拍照格式
        parameters.setPreviewSize(1280, 720);//设置预览尺寸
        parameters.setPictureSize(1280, 720);//分辨率尺寸
        parameters.set("orientation", "portrait");//相片方向
        parameters.set("rotation", 90); //相片镜头角度转90度（默认摄像头是横拍）
        mCamera.setParameters(parameters);//添加参数
        mCamera.setDisplayOrientation(90);//设置显示方向
        mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                mCamera.addCallbackBuffer(data);
                nativeShowYUV(data, 1280, 720);
            }
        });
        mCamera.addCallbackBuffer(new byte[((1280 * 720) * ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8]);
        try {
            mCamera.setPreviewTexture(new SurfaceTexture(0));
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private native boolean nativeSetVideoSurface(Surface surface);
    private native void nativeShowYUV(byte[] yuvArray,int width,int height);
    static {
        System.loadLibrary("showYUV");
    }
}