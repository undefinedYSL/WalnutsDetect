package camera;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.cv_lib.opencv_model_lib.R;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import JavaNavtive.Config;

public class OpencvCameraActivity extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2{


    String TAG = "OpencvCameraActivity";
    private CameraBridgeViewBase mOpencvView;//摄像头对象


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        initView();

    }
    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "内部OpenCV库没有找到。使用OpenCV管理器进行初始化");
        } else {
            Log.d(TAG, "OpenCV库在包中找到。使用它!");

            mOpencvView.enableView();
        }
    }

    private void initView() {
        mOpencvView = (CameraBridgeViewBase) findViewById(R.id.tutorial2_activity_surface_view);
        mOpencvView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpencvView.setCvCameraViewListener(this);
        mOpencvView.setClickable(true);
        mOpencvView.setCameraIndex(0);//0 后置摄像头 1 前置
        mOpencvView.setFocusableInTouchMode(true);
        mOpencvView.setFocusable(true);
        mOpencvView.setKeepScreenOn(true);
    }



    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat rgba=inputFrame.rgba();

        Mat temp=new Mat();
        Config.nativa_Deal(rgba.nativeObj,temp.nativeObj);
        return temp;
    }


}
