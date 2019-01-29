package com.bytedance.android.lesson.restapi.solution;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.bytedance.android.lesson.restapi.solution.utils.Utils.getOutputMediaFile;

class CustomCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private SurfaceHolder surfaceHolder;
    private MediaRecorder mediaRecorder;

    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;

    private boolean isRecording = false;

    private int rotationDegree = 0;
    private MediaRecorder mMediaRecorder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custom_camera);

        mCamera = getCamera(CAMERA_TYPE);
        mSurfaceView = findViewById(R.id.img);
        surfaceHolder = mSurfaceView.getHolder();
        mMediaRecorder = new MediaRecorder();

        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //给SurfaceHolder添加Callback
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                //释放Camera和MediaRecorder资源
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;

            }
        });



        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            //拍一张照片
            mCamera.takePicture(null,null,mPicture);
        });

        findViewById(R.id.btn_record).setOnClickListener(v -> {
            //录制，第一次点击是start，第二次点击是stop
            mCamera.unlock();
            mediaRecorder.setCamera(mCamera);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            mediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
            mediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
            mediaRecorder.setOrientationHint(rotationDegree);
            if (isRecording) {
                //停止录制
                mediaRecorder.stop();
                isRecording = false;
            } else {
                //录制
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    releaseMediaRecorder();
                }

                isRecording = true;
            }
        });

        findViewById(R.id.btn_facing).setOnClickListener(v -> {
            //切换前后摄像头
            if(CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK){
                mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }
            else{
                mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
            try{
                mCamera.setPreviewDisplay(mSurfaceView.getHolder());
                mCamera.startPreview();
            }catch (Exception e) {
                e.printStackTrace();
            }

        });

        findViewById(R.id.btn_zoom).setOnClickListener(v -> {
            //调焦，需要判断手机是否支持
            if(mCamera!=null) {
                Camera.Parameters parameter = mCamera.getParameters();

                if (parameter.isZoomSupported()) {
                    int MAX_ZOOM = parameter.getMaxZoom();
                    int currnetZoom = parameter.getZoom();
                    if (currnetZoom <= MAX_ZOOM) {
                        parameter.setZoom(++currnetZoom);
                        mCamera.setParameters(parameter);
                    }
                } else
                    Toast.makeText(this, "Zoom Not Avaliable", Toast.LENGTH_LONG).show();
            }
        });
    }

    public Camera getCamera(int position) {
        CAMERA_TYPE = position;
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        // 摄像头添加属性，例是否自动对焦，设置旋转方向等
        Camera cam = Camera.open(position);
        try {

            rotationDegree = getCameraDisplayOrientation(position);
            cam.setDisplayOrientation(rotationDegree);
        }catch (Exception e){
            e.printStackTrace();
        }
        return cam;
    }
    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }

    private void releaseCameraAndPreview() {
        // 释放camera资源
        mCamera.stopPreview();
        mCamera.release();
        mCamera=null;
    }

    Camera.Size size;

    private void startPreview(SurfaceHolder holder) {
        // 开始预览
    }

    private boolean prepareVideoRecorder() throws IOException {
        // 准备MediaRecorder
        mediaRecorder.prepare();
        return true;
    }

    private void releaseMediaRecorder() {
        // 释放MediaRecorder
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();
        isRecording = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 释放Camera和MediaRecorder资源
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private Camera.PictureCallback mPicture = (data, camera) -> {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            Log.d("mPicture", "Error accessing file: " + e.getMessage());
        }

        mCamera.startPreview();
    };

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }



}
