package photo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.cv_lib.opencv_model_lib.R;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.opencv.core.Core.inRange;
import static org.opencv.core.Core.rectangle;


///////////////////////////////////////////////////////////
public class OpencvPhotoActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG = "OpencvPhotoActivity";
    private ImageView imgView;
    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE = 3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    private File output;
    private Uri imageUri;
    private int Y_MIN ;
    private int Cb_MIN ;
    private int Cr_MIN ;
    private int Y ;
    private int Cb ;
    private int Cr ;
    private ImageView picture;

    private Paint mPaint = null;
   public  LoadingDailog dialog;
public int k=0;

    Bitmap mBitmap, mBitmap1, mBitmap2, mBitmap3;
    private Bitmap nBitmap;
    Mat msrcMat;
    public GameView gameView;
    private int FLAG = 0;
    public double b = 0;
    private static final int CHANGE_TEXT=1;
    private TextView mText;

    Handler handler=new Handler();
    private PopupWindow mPopWindow;
    private TextView mMenuTv;




    private MyDataBaseHelper dbHelper;
    private SQLiteDatabase db;
    String pages = new String();
    String salary = new String();
    String name = new String();
    String p3 = new String();
    String dengjia = new String();
    Context context = null;

    ViewPager pager = null;
    TabHost tabHost = null;
    TextView t1,t2,t3;
    public int dengji = 0;
    public double p4 = 0;
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    private ImageView cursor;// 动画图片

    //Message message = new Message();
    //message.what = CHANGE_TEXT;
    //然后将消息发送出去
    //        handler.sendMessage(message);


Handler myHandler = new Handler() {  
          public void handleMessage(Message msg) {   
               switch (msg.what) {   
                    case 1:   
                         dialog.dismiss();
                         break;   
               }   
               super.handleMessage(msg);   
          }   
     };
    Handler myHandler1 = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    AlertDialog.Builder builder  = new AlertDialog.Builder(OpencvPhotoActivity.this);
                    builder.setTitle(name ) ;
                    builder.setMessage("发病率为 百分之"+pages+"\n"+"等级为  "+dengji);
                    builder.setPositiveButton("确定" ,  null );
                    builder.show();
                    break;

            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String printTxtPath =  getApplicationContext().getFilesDir().getAbsolutePath();
              super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_opencv_photo);

        dbHelper = new MyDataBaseHelper(this,"temp1111.db",null,3);

        this.imgView = (ImageView) findViewById(R.id.imgView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        mMenuTv = (TextView)findViewById(R.id.menu);
        mMenuTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });




    }
    /**
     * 选择相机
     *
     * @param
     */



    private void showPopupWindow() {


        View contentView = LayoutInflater.from(OpencvPhotoActivity.this).inflate(R.layout.popuplayout, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = contentView.getMeasuredWidth();
        int popupHeight = contentView.getMeasuredHeight();
        int[] location = new int[2];

        mPopWindow = new PopupWindow(contentView);
        int width = getWindowManager().getDefaultDisplay().getWidth();
       int height = getWindowManager().getDefaultDisplay().getHeight();
        mPopWindow.setWidth(height * 1 / 5);
        mPopWindow.setHeight(height * 1 / 9+10);

        TextView tv1 = (TextView)contentView.findViewById(R.id.pop_computer);
        TextView tv2 = (TextView)contentView.findViewById(R.id.pop_financial);

        tv1.setOnClickListener((View.OnClickListener) this);
        tv2.setOnClickListener((View.OnClickListener) this);

        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow.setAnimationStyle(R.style.PopupAnimation);

       // mPopWindow.showAsDropDown(mMenuTv);
        mMenuTv.getLocationOnScreen(location);
        mPopWindow.showAtLocation(mMenuTv, Gravity.NO_GRAVITY, location[0] , location[1] +mMenuTv.getHeight()+10 );


    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.pop_computer) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE2);

            } else {
                choosePhoto();
            }
            mPopWindow.dismiss();

        } else if (id == R.id.pop_financial) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE2);
            } else {
                takePhoto();
            }
            mPopWindow.dismiss();

        }




    }








    public void onSelectPhoto(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);

        } else {
            choosePhoto();
        }
    }

    /**
     * 打开相机
     *
     * @param view
     */
    public void onOpenCamera(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);
        } else {
            takePhoto();
        }
    }

    /**
     * 从相册选取图片
     */
    void choosePhoto() {
        /**
         * 打开选择图片的界面
         */
        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //设定结果返回
        startActivityForResult(i, REQUEST_CODE_PICK_IMAGE);
    }


    /**
     * 拍照
     */
    void takePhoto() {
        /**
         * 最后一个参数是文件夹的名称，可以随便起
         */
        File file = new File(Environment.getExternalStorageDirectory(), "拍照");
        if (!file.exists()) {
            file.mkdir();
        }
        /**
         * 这里将时间作为不同照片的名称
         */
        output = new File(file, System.currentTimeMillis() + ".jpg");

        /**
         * 如果该文件夹已经存在，则删除它，否则创建一个
         */
        try {
            if (output.exists()) {
                output.delete();
            }
            output.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 隐式打开拍照的Activity，并且传入CROP_PHOTO常量作为拍照结束后回调的标志
         */
        imageUri = Uri.fromFile(output);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CROP_PHOTO);

    }

    /**
     * 使用相机
     */
    private void useCamera() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/test/" + System.currentTimeMillis() + ".jpg");
//        file.getParentFile().mkdirs();
//
//        //改变Uri  com.xykj.customview.fileprovider注意和xml中的一致
//        Uri uri = FileProvider.getUriForFile(this, "com.xykj.customview.fileprovider", file);
//        //添加权限
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        startActivityForResult(intent, CROP_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            /**
             * 拍照的请求标志
             */
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        /**
                         * 该uri就是照片文件夹对应的uri
                         */
                        mBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        msrcMat = new Mat();
                        Utils.bitmapToMat(mBitmap, msrcMat);
                        Log.d(TAG, msrcMat.cols() + "");
                        imgView.setImageBitmap(mBitmap);

                    } catch (Exception e) {
                        Toast.makeText(this, "程序崩溃", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("tag", "失败");
                }

                break;
            /**
             * 从相册中选取图片的请求标志
             */

            case REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {

                    try {
                        msrcMat = new Mat();
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        //获取选择照片的数据视图
                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        //从数据视图中获取已选择图片的路径
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);

                        cursor.close();
                        //将图片显示到界面上
                        mBitmap1 = BitmapFactory.decodeFile(picturePath);
                        Utils.bitmapToMat(mBitmap1, msrcMat);
                        FLAG = 1;


                        imgView.setImageBitmap(mBitmap1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.i("liang", "失败");
                }

                break;

            default:
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "内部OpenCV库没有找到。使用OpenCV管理器进行初始化");

        } else {
            Log.d(TAG, "OpenCV库在包中找到。使用它!");


        }
    }


    public void onDetect(View view) {

        Bitmap mBitmap;

        Mat gray = new Mat();
        Mat erzhi = new Mat();
        Mat fushi = new Mat();
        Mat erzhi1 = new Mat();
        Mat pengzhang = new Mat();
        Mat pengzhang1 = new Mat();

        Mat dongshen = new Mat();
        Mat dongshen1 = new Mat();
        Mat dongshen2 = new Mat();

        Mat fushi1 = new Mat();
        Mat fushi2 = new Mat();
        Mat xia = new Mat();
        Mat shang = new Mat();
        Mat bing = new Mat();
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8));
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 15));
        //检测
        Imgproc.cvtColor(msrcMat, gray, Imgproc.COLOR_BGR2RGB);





        inRange(gray, new Scalar(50,110, 100), new Scalar(255, 255, 255), fushi);





        int height1, height2 = 0;
        height2 = fushi.rows();
        height1 = fushi.rows() / 3;

        int width = fushi.cols();
        xia = fushi.rowRange(height1, height2);




        Imgproc.erode(xia, pengzhang, element);
        Imgproc.erode(pengzhang, pengzhang1, element);
        Imgproc.dilate(pengzhang1, fushi1, element);
        Imgproc.dilate(fushi1, fushi2, element);
        Imgproc.filter2D(fushi2, dongshen1, -1, element1);
        Imgproc.filter2D(dongshen1, dongshen, -1, element1);


        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(dongshen, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

        MatOfPoint temp_contour = contours.get(0);
        Imgproc.contourArea(temp_contour);

        List<Double> list = new ArrayList<Double>();
        List<MatOfPoint> contours_pig = new ArrayList<MatOfPoint>();
        for (int i = 0; i < contours.size(); i++) {
            list.add(Imgproc.contourArea(contours.get(i)));
        }

        //对连通域面积进行排序
        Collections.sort(list);

        double maxArea_pig = -1;
        MatOfPoint2f approxCurve_pig = new MatOfPoint2f();
        for (int idx = 0; idx < contours.size(); idx++) {
            temp_contour = contours.get(idx);
            double contourarea = Imgproc.contourArea(temp_contour);
            if (contourarea > maxArea_pig) {
                MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour.toArray());
                int contourSize = (int) temp_contour.total();
                Imgproc.approxPolyDP(new_mat, approxCurve_pig, contourSize * 0.05, true);
                if (contourarea == list.get(contours.size() - 1)) {
                    maxArea_pig = contourarea;
                    contours_pig.add(temp_contour);
                }
            }
        }

        Imgproc.drawContours(dongshen, contours_pig, -1, new Scalar(255, 0, 255), -1);
        double b = 0;
        double le = 0;
        double[] a = new double[20000];
        double[] c = new double[20000];
        double[] lee = new double[20000];
        double[] t = new double[20000];


        //int chu=0;
        int xiachang = 0;
        double po=0;
        double pe=0;
        double range=255;
        double de =20;
        Mat kuan = new Mat();
        erzhi=dongshen;
        kuan = dongshen.rowRange(height2 / 6 -10, height2 / 6 + 10);

        for (int p =0; p <= 19 ; p++) {

            for (int q = 0; q <= width-1; q++) {
                lee = kuan.get(p, q);

                lee[0]=lee[0]/255;

                po = po + lee[0] ;


            }



        } le=po/20;

        int z=height2-height1;

        for (int p =0; p <=z-1  ; p++) {

            for (int q = 0; q <= width-1; q++) {
                t = erzhi.get(p, q);

                b = b + t[0] / 255;


            }




            a[p] = b;
            b=0;

            if (a[p] >= le / 3 + 5) {
                xiachang = xiachang + 1;

                for (int l = 0; l <= width-1; l++) {

                    erzhi.put(p, l, 255);


                }

            }


        }
/////////////////////////////////////////////



        Mat element3 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 20));

        //检测




        int height = 0;
        height = fushi.rows() / 3;


        shang= fushi.rowRange(0, height);



//        imgView.setDrawingCacheEnabled(false);

//        Log.d(TAG, xia.cols() + "");
//        for (int p = 1; p <= height/3; p++) {
//                for (int q = 1; q <= width; q++) {
//                    double[] t = xia.get(p, q);
//                    if (t[q] == 1) {
//                        Toast toast = Toast.makeText(getApplicationContext(), "222", Toast.LENGTH_LONG);
//                        toast.setGravity(Gravity.CENTER, 0, 500);
//                        toast.show();
//                        break;
//                    }
//
//
//                }
//        }

        Imgproc.erode(shang, pengzhang, element3);
        Imgproc.erode(pengzhang, pengzhang1, element3);
        Imgproc.dilate(pengzhang1, fushi1, element3);
        Imgproc.dilate(fushi1, fushi2, element3);
        Imgproc.filter2D(fushi2, dongshen1, -1, element3);
        Imgproc.filter2D(dongshen1, dongshen, -1, element3);


        List<MatOfPoint> contours1 = new ArrayList<MatOfPoint>();
        Mat hierarchy1 = new Mat();

        Imgproc.findContours(dongshen, contours1, hierarchy1, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

        MatOfPoint temp_contour1 = contours1.get(0);
        Imgproc.contourArea(temp_contour1);

        List<Double> list1 = new ArrayList<Double>();
        List<MatOfPoint> contours_pig1 = new ArrayList<MatOfPoint>();
        for (int i = 0; i < contours1.size(); i++) {
            list1.add(Imgproc.contourArea(contours1.get(i)));
        }

        //对连通域面积进行排序
        Collections.sort(list1);

        double maxArea_pig1 = -1;
        MatOfPoint2f approxCurve_pig1 = new MatOfPoint2f();
        for (int idx = 0; idx < contours1.size(); idx++) {
            temp_contour1 = contours1.get(idx);
            double contourarea1 = Imgproc.contourArea(temp_contour1);
            if (contourarea1 > maxArea_pig1) {
                MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour1.toArray());
                int contourSize = (int) temp_contour1.total();
                Imgproc.approxPolyDP(new_mat, approxCurve_pig1, contourSize * 0.05, true);
                if (contourarea1 == list1.get(contours1.size() - 1)) {
                    maxArea_pig1 = contourarea1;
                    contours_pig1.add(temp_contour1);
                }
            }
        }

        Imgproc.drawContours(dongshen, contours_pig1, -1, new Scalar(255, 0, 255), -1);



        //int chu=0;
        int shangchang = 0;
        for (int p = 0; p <= height - 1; p++) {
            for (int q = 0; q <= width - 1; q++) {
                t = dongshen.get(p, q);

                b = b + t[0] / 255;


            }

            a[p] = b;

            if (a[p] >= le/2+10) {
                shangchang = shangchang + 1;
                for (int l = 1; l <= width; l++) {

                    dongshen.put(p, l, 255);

//              if(l==1) {
//                  chu=p;
//              }


                }
//                   int shang=height-chu;

            }


        }

////////////////////////////////////////////////////////


        Mat element6 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Mat element5 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4, 4));
        Mat element7= Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(14, 15));
        //检测


        inRange(gray, new Scalar(120, 145, 205), new Scalar(255, 255, 255), fushi);


        height = fushi.rows() / 3;
        String B = xiachang + "";
        Toast toast = Toast.makeText(getApplicationContext(), B, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 500);
        toast.show();

        bing = fushi.rowRange(height-shangchang, height+xiachang);


//        imgView.setDrawingCacheEnabled(false);

//        Log.d(TAG, xia.cols() + "");
//        for (int p = 1; p <= height/3; p++) {
//                for (int q = 1; q <= width; q++) {
//                    double[] t = xia.get(p, q);
//                    if (t[q] == 1) {
//                        Toast toast = Toast.makeText(getApplicationContext(), "222", Toast.LENGTH_LONG);
//                        toast.setGravity(Gravity.CENTER, 0, 500);
//                        toast.show();
//                        break;
//                    }
//
//
//                }
//        }

        Imgproc.erode(bing, pengzhang, element6);
        Imgproc.erode(pengzhang, pengzhang1, element6);
        Imgproc.dilate(pengzhang1, fushi1, element5);
        Imgproc.dilate(fushi1, fushi2, element5);
        Imgproc.filter2D(fushi2, dongshen1, -1, element7);
        Imgproc.filter2D(dongshen1, dongshen, -1, element7);


        List<MatOfPoint> contours2 = new ArrayList<MatOfPoint>();
        Mat hierarchy2 = new Mat();

        Imgproc.findContours(dongshen, contours2, hierarchy2, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

        MatOfPoint temp_contour2 = contours2.get(0);
        Imgproc.contourArea(temp_contour2);

        List<Double> list2 = new ArrayList<Double>();
        List<MatOfPoint> contours_pig2 = new ArrayList<MatOfPoint>();
        for (int i = 0; i < contours2.size(); i++) {
            list2.add(Imgproc.contourArea(contours2.get(i)));
        }

        //对连通域面积进行排序
        Collections.sort(list2);

        double maxArea_pig2 = -1;
        MatOfPoint2f approxCurve_pig2 = new MatOfPoint2f();
        for (int idx = 0; idx < contours2.size(); idx++) {
            temp_contour2 = contours2.get(idx);
            double contourarea2 = Imgproc.contourArea(temp_contour2);
            if (contourarea2 > maxArea_pig2) {
                MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour2.toArray());
                int contourSize = (int) temp_contour2.total();
                Imgproc.approxPolyDP(new_mat, approxCurve_pig2, contourSize * 0.05, true);
                if (contourarea2 == list2.get(contours2.size() - 1)) {
                    maxArea_pig2 = contourarea2;
                    contours_pig2.add(temp_contour2);
                }
            }
        }

        Imgproc.drawContours(dongshen, contours_pig2, -1, new Scalar(255, 0, 255), -1);
        imgView.setDrawingCacheEnabled(true);
        mBitmap2 = Bitmap.createBitmap(width,shangchang+xiachang, Bitmap.Config.ARGB_8888);
        if (FLAG == 0) {
            try {
                Utils.matToBitmap(dongshen, mBitmap2);//opencv 转bmp  将Mat转换为位图

                //输出

                imgView.setImageBitmap(mBitmap2);


                imgView.destroyDrawingCache();
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            try {
                Utils.matToBitmap(dongshen, mBitmap2);//opencv 转bmp  将Mat转换为位图

                //输出

                imgView.setImageBitmap(mBitmap2);


                imgView.destroyDrawingCache();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

    /**
     * 中值滤波
     *
     * @param view
     */
    public void onMeans(View view) {
        try {
            Mat gray = new Mat();
            Mat hsv_v = new Mat();
            Imgproc.cvtColor(msrcMat, gray, Imgproc.COLOR_BGR2HSV);
            Core.extractChannel(gray, hsv_v, 2);

            int height, width = 0;

            height = hsv_v.rows();
            width = hsv_v.cols();
            double[] lee = new double[20000];
            double[] laa = new double[20000];
            double po = 0;
            double pe = 0;
            double pa = 0;
            double pc = 0;
            for (int p = height * 3 / 4; p <= height - 1; p++) {

                for (int q = 0; q <= (width / 4) - 1; q++) {
                    lee = hsv_v.get(p, q);


                    po = po + lee[0];


                }
            }
            pa = po / ((height / 4) * (width / 4) * 255);

            for (int p = height * 3 / 4; p <= height - 1; p++) {

                for (int q = width * 3 / 4; q <= width - 1; q++) {
                    laa = hsv_v.get(p, q);


                    pe = pe + laa[0];


                }
            }
            pc = pe / ((height / 4) * (width / 4) * 255);

            double pd = (pa + pc) / 2;


            if (pd > 0.25) {
                Y_MIN = 100;
                Cb_MIN = 110;
                Cr_MIN = 50;
            } else if (pd > 0.17)

            {
                Y_MIN = 70;
                Cb_MIN = 80;
                Cr_MIN = 30;
            } else {
                Y_MIN = 45;
                Cb_MIN = 55;
                Cr_MIN = 20;
            }


            if (pd > 0.335) {
                Y = 215;
                Cb = 185;
                Cr = 155;
            } else if (pd > 0.265) {
                Y = 215;
                Cb = 185;
                Cr = 155;
            } else if (pd > 0.24) {
                Y = 215;
                Cb = 160;
                Cr = 145;
            } else if (pd > 0.20) {
                Y = 205;
                Cb = 145;
                Cr = 120;
            } else if (((0.16 < pd) && (pd < 0.1673)) || ((0.1676 < pd) && (pd < 0.1999))) {
                Y = 200;
                Cb = 140;
                Cr = 110;
            } else if ((0.1673 < pd) && (pd < 0.1675)) {
                Y = 155;
                Cb = 125;
                Cr = 55;
            } else if (pd > 0.16) {
                Y = 185;
                Cb = 130;
                Cr = 105;
            } else if (pd > 0.1540) {
                Y = 180;
                Cb = 130;
                Cr = 90;
            } else if (pd > 0.149) {
                Y = 185;
                Cb = 130;
                Cr = 105;
            } else if (pd > 1) {
                Y = 115;
                Cb = 85;
                Cr = 35;
            }
            String B = Y + "," + Cb + "," + Cr + ",";


            handler.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OpencvPhotoActivity.this);
                    builder.setTitle("结果");
//                    builder.setMessage("R为" + Y + " G为" + Cb + "B" + Cr);
                    builder.setMessage("提取光强成功");
                    builder.setPositiveButton("是", null);
                    builder.show();

                }
            });

        }
        catch (Exception e){

        }
    }



    /**
     * 灰度化
     *
     * @param view
     */
    public void onGray(View view) {
       // this.imgView = (ImageView) findViewById(R.id.imgView);



        if(imgView.getDrawable()==null) {
           System.out.println("zenmehuishi");
       return ;


       }else {

           LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(OpencvPhotoActivity.this)
                   .setMessage("加载中...")
                   .setCancelable(false).setCancelOutside(false);
           dialog = loadBuilder.create();
           if (!dialog.isShowing()) {

               dialog.show();
           }


           // 开启一个子线程
           new Thread(new Runnable() {
               @Override
               public void run() {

                   Bitmap mBitmap;

                   Mat gray = new Mat();
                   Mat erzhi = new Mat();
                   Mat fushi = new Mat();
                   Mat erzhi1 = new Mat();
                   Mat pengzhang = new Mat();
                   Mat pengzhang1 = new Mat();

                   Mat dongshen = new Mat();
                   Mat dongshen1 = new Mat();


                   Mat fushi1 = new Mat();
                   Mat fushi2 = new Mat();
                   Mat xia = new Mat();
                   Mat shang = new Mat();
                   Mat bing = new Mat();
                   Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8));
                   Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 15));
                   //检测
                   Imgproc.cvtColor(msrcMat, gray, Imgproc.COLOR_BGR2RGB);


                   inRange(gray, new Scalar(Cr_MIN, Cb_MIN, Y_MIN), new Scalar(255, 255, 255), fushi);

                   int height1, height2 = 0;
                   height2 = fushi.rows();
                   height1 = fushi.rows() / 3;

                   int width = fushi.cols();
                   xia = fushi.rowRange(height1, height2);


                   Imgproc.erode(xia, pengzhang, element);
                   Imgproc.erode(pengzhang, pengzhang1, element);
                   Imgproc.dilate(pengzhang1, fushi1, element);
                   Imgproc.dilate(fushi1, fushi2, element);
                   Imgproc.filter2D(fushi2, dongshen1, -1, element1);
                   Imgproc.filter2D(dongshen1, dongshen, -1, element1);


                   List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
                   Mat hierarchy = new Mat();

                   Imgproc.findContours(dongshen, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

                   MatOfPoint temp_contour = contours.get(0);
                   Imgproc.contourArea(temp_contour);

                   List<Double> list = new ArrayList<Double>();
                   List<MatOfPoint> contours_pig = new ArrayList<MatOfPoint>();
                   for (int i = 0; i < contours.size(); i++) {
                       list.add(Imgproc.contourArea(contours.get(i)));
                   }

                   //对连通域面积进行排序
                   Collections.sort(list);

                   double maxArea_pig = -1;
                   MatOfPoint2f approxCurve_pig = new MatOfPoint2f();
                   for (int idx = 0; idx < contours.size(); idx++) {
                       temp_contour = contours.get(idx);
                       double contourarea = Imgproc.contourArea(temp_contour);
                       if (contourarea > maxArea_pig) {
                           MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour.toArray());
                           int contourSize = (int) temp_contour.total();
                           Imgproc.approxPolyDP(new_mat, approxCurve_pig, contourSize * 0.05, true);
                           if (contourarea == list.get(contours.size() - 1)) {
                               maxArea_pig = contourarea;
                               contours_pig.add(temp_contour);
                           }
                       }
                   }

                   Imgproc.drawContours(dongshen, contours_pig, -1, new Scalar(255, 0, 255), -1);
                   double b = 0;
                   double le = 0;
                   double[] a = new double[20000];
                   double[] c = new double[20000];
                   double[] lee = new double[20000];
                   double[] t = new double[20000];


                   //int chu=0;
                   int xiachang = 0;
                   double po = 0;
                   double pe = 0;
                   double range = 255;
                   double de = 20;
                   Mat kuan = new Mat();
                   erzhi = dongshen;
                   kuan = dongshen.rowRange(height2 / 6 - 10, height2 / 6 + 10);

                   for (int p = 0; p <= 19; p++) {

                       for (int q = 0; q <= width - 1; q++) {
                           lee = kuan.get(p, q);

                           lee[0] = lee[0] / 255;

                           po = po + lee[0];


                       }


                   }
                   le = po / 20;

                   int z = height2 - height1;

                   for (int p = 0; p <= z - 1; p++) {

                       for (int q = 0; q <= width - 1; q++) {
                           t = erzhi.get(p, q);

                           b = b + t[0] / 255;


                       }


                       a[p] = b;
                       b = 0;

                       if (a[p] >= le / 3 + 5) {
                           xiachang = xiachang + 1;

//                        for (int l = 0; l <= width-1; l++) {
//
//                            erzhi.put(p, l, 255);
//
//
//                        }

                       }


                   }
/////////////////////////////////////////////


                   Mat element3 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 20));

                   //检测


                   int height = 0;
                   height = fushi.rows() / 3;


                   shang = fushi.rowRange(0, height);


//        imgView.setDrawingCacheEnabled(false);

//        Log.d(TAG, xia.cols() + "");
//        for (int p = 1; p <= height/3; p++) {
//                for (int q = 1; q <= width; q++) {
//                    double[] t = xia.get(p, q);
//                    if (t[q] == 1) {
//                        Toast toast = Toast.makeText(getApplicationContext(), "222", Toast.LENGTH_LONG);
//                        toast.setGravity(Gravity.CENTER, 0, 500);
//                        toast.show();
//                        break;
//                    }
//
//
//                }
//        }

                   Imgproc.erode(shang, pengzhang, element3);
                   Imgproc.erode(pengzhang, pengzhang1, element3);
                   Imgproc.dilate(pengzhang1, fushi1, element3);
                   Imgproc.dilate(fushi1, fushi2, element3);
                   Imgproc.filter2D(fushi2, dongshen1, -1, element3);
                   Imgproc.filter2D(dongshen1, dongshen, -1, element3);


                   List<MatOfPoint> contours1 = new ArrayList<MatOfPoint>();
                   Mat hierarchy1 = new Mat();

                   Imgproc.findContours(dongshen, contours1, hierarchy1, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

                   MatOfPoint temp_contour1 = contours1.get(0);
                   Imgproc.contourArea(temp_contour1);

                   List<Double> list1 = new ArrayList<Double>();
                   List<MatOfPoint> contours_pig1 = new ArrayList<MatOfPoint>();
                   for (int i = 0; i < contours1.size(); i++) {
                       list1.add(Imgproc.contourArea(contours1.get(i)));
                   }

                   //对连通域面积进行排序
                   Collections.sort(list1);

                   double maxArea_pig1 = -1;
                   MatOfPoint2f approxCurve_pig1 = new MatOfPoint2f();
                   for (int idx = 0; idx < contours1.size(); idx++) {
                       temp_contour1 = contours1.get(idx);
                       double contourarea1 = Imgproc.contourArea(temp_contour1);
                       if (contourarea1 > maxArea_pig1) {
                           MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour1.toArray());
                           int contourSize = (int) temp_contour1.total();
                           Imgproc.approxPolyDP(new_mat, approxCurve_pig1, contourSize * 0.05, true);
                           if (contourarea1 == list1.get(contours1.size() - 1)) {
                               maxArea_pig1 = contourarea1;
                               contours_pig1.add(temp_contour1);
                           }
                       }
                   }

                   Imgproc.drawContours(dongshen, contours_pig1, -1, new Scalar(255, 0, 255), -1);


                   double[] chu = new double[20000];
                   //int chu=0;
                   int shangchang = 0;
                   for (int p = 0; p <= height - 1; p++) {
                       for (int q = 0; q <= width - 1; q++) {
                           t = dongshen.get(p, q);

                           b = b + t[0] / 255;


                       }

                       a[p] = b;

                       if (a[p] >= le / 2 + 10) {
                           shangchang = shangchang + 1;
//                        for (int l = 1; l <= width; l++) {
//
//                            dongshen.put(p, l, 255);
//
////              if(l==1) {
////                  chu=p;
////              }
//
//
//                        }
//                   int shang=height-chu;

                       }


                   }

////////////////////////////////////////////////////////


                   Mat element6 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
                   Mat element5 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4, 4));
                   Mat element7 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 14));
                   //检测


                   inRange(gray, new Scalar(Cr, Cb, Y), new Scalar(255, 255, 255), fushi);


                   height = fushi.rows() / 3;


                   bing = fushi.rowRange(height - shangchang, height + xiachang);


//       imgView.setDrawingCacheEnabled(false);

//        Log.d(TAG, xia.cols() + "");
//        for (int p = 1; p <= height/3; p++) {
//                for (int q = 1; q <= width; q++) {
//                    double[] t = xia.get(p, q);
//                    if (t[q] == 1) {
//                        Toast toast = Toast.makeText(getApplicationContext(), "222", Toast.LENGTH_LONG);
//                        toast.setGravity(Gravity.CENTER, 0, 500);
//                        toast.show();
//                        break;
//                    }
//
//
//                }
//        }

                   Imgproc.erode(bing, pengzhang, element6);

                   Imgproc.dilate(pengzhang, fushi2, element5);
                   Imgproc.filter2D(fushi2, dongshen1, -1, element7);
                   Imgproc.filter2D(dongshen1, dongshen, -1, element7);


                   List<MatOfPoint> contours2 = new ArrayList<MatOfPoint>();
                   Mat hierarchy2 = new Mat();

                   Imgproc.findContours(dongshen, contours2, hierarchy2, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

                   MatOfPoint temp_contour2 = contours2.get(0);
                   Imgproc.contourArea(temp_contour2);

                   List<Double> list2 = new ArrayList<Double>();
                   List<MatOfPoint> contours_pig2 = new ArrayList<MatOfPoint>();
                   for (int i = 0; i < contours2.size(); i++) {
                       list2.add(Imgproc.contourArea(contours2.get(i)));
                   }

                   //对连通域面积进行排序
                   Collections.sort(list2);

                   double maxArea_pig2 = -1;
                   MatOfPoint2f approxCurve_pig2 = new MatOfPoint2f();
                   for (int idx = 0; idx < contours2.size(); idx++) {
                       temp_contour2 = contours2.get(idx);
                       double contourarea2 = Imgproc.contourArea(temp_contour2);
                       if (contourarea2 > maxArea_pig2) {
                           MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour2.toArray());
                           int contourSize = (int) temp_contour2.total();
                           Imgproc.approxPolyDP(new_mat, approxCurve_pig2, contourSize * 0.05, true);
                           if (contourarea2 == list2.get(contours2.size() - 1)) {
                               maxArea_pig2 = contourarea2;
                               contours_pig2.add(temp_contour2);
                           }
                       }
                   }

                   Imgproc.drawContours(dongshen, contours_pig2, -1, new Scalar(255, 0, 255), -1);


                   int suichang = xiachang + shangchang;

                   int bingchang = 0;
                   for (int p = 0; p <= suichang - 1; p++) {
                       for (int q = 0; q <= width - 1; q++) {
                           t = dongshen.get(p, q);

                           b = b + t[0] / 255;


                       }

                       a[p] = b;
                       b = 0;
                       if (a[p] >= le / 4 - 6) {
                           bingchang = bingchang + 1;
                           chu[p] = p;
                           Arrays.sort(chu);


//                        for (int l = 0; l <= width-1; l++) {
//
//                            dongshen.put(p, l, 255);
//
//
//                        }


                       }


                   }
                   imgView.setDrawingCacheEnabled(true);
                   mBitmap2 = Bitmap.createBitmap(width, shangchang + xiachang, Bitmap.Config.ARGB_8888);
                   erzhi1 = gray.rowRange(height - shangchang, height + xiachang);
                   Point p1 = new Point(10, chu[0]);
                   Point p2 = new Point(width - 10, chu[0] + bingchang);

                   p3 = txfloat(bingchang, shangchang + xiachang);

                   rectangle(erzhi1, p2, p1, new Scalar(0, 255, 0), 2, 8, 0);


                   Message message = new Message();
                   message.what = 1;

                   myHandler.sendMessage(message);


                   Looper.prepare();

                   if (FLAG == 0) {
                       try {
                           Utils.matToBitmap(erzhi1, mBitmap2);//opencv 转bmp  将Mat转换为位图

                           //输出
                           handler.post(new Runnable() {
                               @Override
                               public void run() {


                                   imgView.setImageBitmap(mBitmap2);

                               }
                           });
                           imgView.destroyDrawingCache();
                       } catch (Exception e) {
                           e.printStackTrace();
                       }


                   } else {
                       try {
                           Utils.matToBitmap(erzhi1, mBitmap2);//opencv 转bmp  将Mat转换为位图

                           //输出
                           handler.post(new Runnable() {
                               @Override
                               public void run() {

                                   imgView.setImageBitmap(mBitmap2);

                               }
                           });

                           imgView.destroyDrawingCache();
                       } catch (Exception e) {
                           e.printStackTrace();
                       }


                   }


                   handler.post(new Runnable() {
                                    @Override
                                    public void run() {
//                        AlertDialog.Builder builder  = new AlertDialog.Builder(OpencvPhotoActivity.this);
//                        builder.setTitle("确认" ) ;
//                        builder.setMessage("发病率为"+p4+"    等级为"+3 ) ;
//                        builder.setPositiveButton("是" ,  null );
//                        builder.show();
                                        dbHelper.getWritableDatabase();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(OpencvPhotoActivity.this);
                                        builder.setTitle("存入数据");
                                        builder.setIcon(android.R.drawable.ic_dialog_alert);
                                        System.out.println(p3);

                                        builder.setMessage("发病率为百分之 " + p3 + "\n" + "等级为  " + dengji);
                                        final View v = getLayoutInflater().inflate(R.layout.dialoglayout, null);
                                        builder.setView(v);

                                        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                EditText editText_username = v.findViewById(R.id.username);
                                                String t = String.valueOf(editText_username.getText());
//////////////////////////////////
                                                File file = new File(Environment.getExternalStorageDirectory(), t + ".jpg");
                               /*for(int j=0;j<=100;j++){
                                    System.out.print( j+":"+t);
                                };*/
                                                FileOutputStream out = null;
                                                try {
                                                    out = new FileOutputStream(file);
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                }
                                                mBitmap2.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                                System.out.println("___________保存的__sd___下_______________________");


                                                try {
                                                    out.flush();
                                                    out.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }


                                                Toast.makeText(OpencvPhotoActivity.this, "保存已经至" + Environment.getExternalStorageDirectory() + "下", Toast.LENGTH_SHORT).show();


                                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                                ContentValues values = new ContentValues();
                                                //开始添加第一条数
                                                values.put("name", t);
                                                values.put("pages", p3);
                                                values.put("salary", file.toString());
                                                values.put("dengjia", dengji);
                                                db.insert("person", null, values);//插入第一条数据


/////////////////////////////////
                                                Toast.makeText(OpencvPhotoActivity.this, "username:" + editText_username.getText(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });

                                        builder.show();


/////////////////////////////////
                                    }


                                }
                   );


               }
           }).start();

       }

    }



    public void sqling(View view) {

//        Button backColse1;
//        Button backColse2;

//                        builder.setTitle("确认" ) ;
//                        builder.setMessage("发病率为"+p4+"    等级为"+3 ) ;
//                        builder.setPositiveButton("是" ,  null );
//                        builder.show();

        AlertDialog.Builder builder2 = new AlertDialog.Builder(OpencvPhotoActivity.this);

        builder2.setIcon(android.R.drawable.presence_away);
      //  builder2.setIcon(android.R.drawable.presence_busy);
        builder2.setTitle("读取数据库");

        final View v =  getLayoutInflater().inflate(R.layout.dialoglayout1,null);
//        backColse1 = (Button)v .findViewById(R.id.bvack_colse1);
//        backColse2= (Button) v.findViewById(R.id.bvack_colse2);
        builder2.setView(v);



        builder2.setPositiveButton("读取", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                EditText editText_username = v.findViewById(R.id.username1);


                dbHelper.getWritableDatabase();
//////////////////////////////////
                System.out.println("tag" + editText_username);
                Log.d("tag",String.valueOf(editText_username));
                Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                        "select *  from person where name =?", new String[]{String.valueOf(editText_username.getText())});

                if(cursor.moveToFirst()) {
                    Log.d("tag","cursor");
                    do {

                        name = cursor.getString(cursor.getColumnIndex("name"));
                        pages = cursor.getString(cursor.getColumnIndex("pages"));
                        salary = cursor.getString(cursor.getColumnIndex("salary"));

                        Log.d("tag","name"+name);
                        Log.d("tag","pages"+pages);
                        Log.d("tag","salary"+salary);
                        Log.d("tag","dengjia"+dengjia);
                    }while (cursor.moveToNext());
//                    for(int j=0;j<=100;j++){
//                        System.out.println("SALARY" + salary);
//                    }
                }

                cursor.close();
                //////
//                ImageButton Button1 = (ImageButton) v.findViewById(R.id.dialog_pre_entry_close);
//                Button1.setOnClickListener(new View.OnClickListener() {
//                    @Override			public void onClick(View v) {
//                        Log.e("AlertDialog","select 1");
//                    }		});

                //////

                Toast.makeText(OpencvPhotoActivity.this, "username:"+ name,Toast.LENGTH_SHORT).show();

                mBitmap1 = BitmapFactory.decodeFile(salary);

                imgView.setImageBitmap(mBitmap1);
//                mBitmap1 = BitmapFactory.decodeFile(salary);
//                Utils.bitmapToMat(mBitmap1, msrcMat);
//
//
//
//                imgView.setImageBitmap(mBitmap1);
/////////////////////////////////
                p4=Double.valueOf(p3);
                if(p4>=75)
                {
                    dengji=4;

                }
                else if(p4>=50)
                {
                    dengji=3;
                }
                else if(p4>=25){
                    dengji=2;
                }
                else {
                    dengji=1;
                }
            Message message = new Message();
            message.what =2;

            myHandler1.sendMessageDelayed(message,500);



//                builder1.setPositiveButton("保存", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//
//
//                    }
//                });
//                builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//
/////////////////////////////
//                    }
//                });

//
//                        AlertDialog.Builder builder  = new AlertDialog.Builder(OpencvPhotoActivity.this);
//                        builder.setTitle("确认" ) ;
//                        builder.setMessage("发病率为"+"    等级为"+3 ) ;
//                        builder.setPositiveButton("是" ,  null );
//                        builder.show();
//

            }
        });
        builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

/////////////////////////////
            }
        });
        builder2.show();


////////////////////////////////////////////////////////////




    }

//    private boolean hasData(String tempName) {
//        //从Record这个表里找到name=tempName的id
//        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
//                "select id as _id,name from records where name =?", new String[]{tempName});
//        //判断是否有下一个
//        return cursor.moveToNext();
//    }
//    private void insertData(String tempName) {
//        db = dbHelper.getWritableDatabase();
//        db.execSQL("insert into records(name) values('" + tempName + "')");
//        db.close();
//    }
public static String txfloat(int a,int b) {
    // TODO 自动生成的方法存根

    DecimalFormat df=new DecimalFormat("0.00");//设置保留位数

    return df.format((float)a*100/b);

}


}










 /*   public void ojbk(View view) {


        gameView = new GameView(this);

        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = 0;
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;



        addContentView(gameView,params);
    }

*/





