package com.jxit.jxitprinter1_6.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jxit.jxitbluetoothprintersdk1_4.Jxit_esc;
import com.jxit.jxitprinter1_6.R;
import com.jxit.jxitprinter1_6.util.testSketch;

import java.io.File;

public class PictureActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PHOTO_REQUEST_CAREMA = 1111;
    private static final int PHOTO_REQUEST_GALLERY = 1112;
    private static final int PHOTO_REQUEST_CUT = 1113;
    private static final int CAMERA_OK = 1114;

    private Toolbar toolbar;
    private LinearLayout mLlCamera,mLlPicture;
    private ImageView iv_image;

    private String address;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";

    private File tempFile;

    private Jxit_esc mJxit_esc;

    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        address = bundle.getString("btDeviceAddress");

        init();
    }

    /**
     * init
     */
    private void init(){
        initToolbar();

        iv_image = (ImageView) findViewById(R.id.iV);
        mLlCamera = (LinearLayout) findViewById(R.id.ll_camera) ;
        mLlPicture = (LinearLayout) findViewById(R.id.ll_picture) ;
        mLlCamera.setOnClickListener(this);
        mLlPicture.setOnClickListener(this);
    }

    /**
     * initToolbar
     */
    private void initToolbar(){
        toolbar = (Toolbar)findViewById(R.id.picture_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * onRequestPermissionsResult
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode){
            case CAMERA_OK:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    if (hasSdcard()) {
                        tempFile = new File(Environment.getExternalStorageDirectory(),PHOTO_FILE_NAME);
                        Uri uri = Uri.fromFile(tempFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    }
                    startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
                }else {
                    Toast.makeText(PictureActivity.this,"请手动打开相机权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * hasSdcard
     */
    private boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * crop
     */
    private void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1.5);
        intent.putExtra("outputX", 180);
        intent.putExtra("outputY", 320);
        intent.putExtra("scale", true);
        intent.putExtra("circleCrop",false);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * onActivityResult
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                crop(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_CAREMA && resultCode == RESULT_OK) {
            if (hasSdcard()) {
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(PictureActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PHOTO_REQUEST_CUT && resultCode == RESULT_OK) {
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                final Bitmap mBitmap = testSketch.testGaussBlur(bitmap, 5, 5 / 3);

                iv_image.setImageBitmap(mBitmap);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mJxit_esc = Jxit_esc.getInstance();
                        if(!mJxit_esc.connectDevice(address)) {
                            Toast.makeText(PictureActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (!mJxit_esc.esc_bitmap_mode(0, mBitmap))
                                Toast.makeText(PictureActivity.this, "打印失败", Toast.LENGTH_SHORT).show();
                            else {
                                if (!mJxit_esc.esc_print_text("\n\n"))
                                    Toast.makeText(PictureActivity.this, "打印失败", Toast.LENGTH_SHORT).show();
                            }
                        }

                        mJxit_esc.close();
                    }
                }).start();
            }
            try {
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * onClick
     */
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.ll_camera){
            if (Build.VERSION.SDK_INT > 22){
                if (ContextCompat.checkSelfPermission(PictureActivity.this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PictureActivity.this,new String[]{android.Manifest.permission.CAMERA},CAMERA_OK);
                }else {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    if (hasSdcard()) {
                        tempFile = new File(Environment.getExternalStorageDirectory(),PHOTO_FILE_NAME);
                        Uri uri = Uri.fromFile(tempFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    }
                    startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
                }
            }else {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                if (hasSdcard()) {
                    tempFile = new File(Environment.getExternalStorageDirectory(),PHOTO_FILE_NAME);
                    Uri uri = Uri.fromFile(tempFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                }
                startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
            }
        }else if(view.getId() == R.id.ll_picture){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
        }
    }



}
