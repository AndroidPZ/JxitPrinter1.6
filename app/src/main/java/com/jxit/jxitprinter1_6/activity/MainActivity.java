package com.jxit.jxitprinter1_6.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jxit.jxitbluetoothprintersdk1_4.Jxit_esc;
import com.jxit.jxitprinter1_6.R;
import com.jxit.jxitprinter1_6.fragment.FragmentBluetooth;
import com.jxit.jxitprinter1_6.fragment.FragmentDemo;

import java.io.BufferedInputStream;
import java.nio.charset.Charset;

import static java.lang.Math.sin;


public class MainActivity extends AppCompatActivity implements FragmentBluetooth.FragmentInteractionBluetooth,FragmentDemo.FragmentInteractionDemo{
    private static final int REQUEST_CAMERA = 0;
    private static final int CONNECTED = 1;
    private static final int CONNECT_FAILD = 2;
    private static final int PRINT_FINISHED = 3;
    private static final int PRINT_FAILD = 4;

    private Toolbar toolBarMain;
    private TextView tvTitle;
    private ImageButton btnRight;

    private FragmentDemo mFragmentDemo;
    private FragmentBluetooth mFragmentBluetooth;

    private String btDeviceName;
    public static String btDeviceAddress = null;
    private boolean clickFragmentBluetooth = false;

    private BluetoothAdapter mBtAdapter;
    private Jxit_esc mJxit_esc;


    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        clickDemmoBtn();
    }

    /**
     * onResume
     */
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT>22) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }
        }
    }

    /**
     * init
     */
    private void init() {
        initView();
        initOthers();
    }

    /**
     * initView
     */
    private void initView() {
        initToolbar();
    }


    /**
     * initToolbar
     */
    private void initToolbar(){
        toolBarMain = (Toolbar)findViewById(R.id.tb_main);

        tvTitle = (TextView) toolBarMain.findViewById(R.id.tv_tb_title);
        btnRight = (ImageButton) toolBarMain.findViewById(R.id.btn_tb_right);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!clickFragmentBluetooth) {
                    clickFragmentBluetooth = true;
                    clickBluetoothBtn();
                }
            }
        });

        setSupportActionBar(toolBarMain);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolBarMain.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickFragmentBluetooth){
                    clickFragmentBluetooth = false;

//                    toolBarMain.setNavigationIcon(R.drawable.bullet_grey_1);
                    toolBarMain.setNavigationIcon(R.drawable.usb1_32);

                    FragmentTransaction fragmentTransaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.remove(mFragmentBluetooth);
                    fragmentTransaction.commit();
                }else {
                    Intent intent = new Intent(MainActivity.this, UsbPrinterActivity.class);
                    MainActivity.this.startActivity(intent);
                }


            }
        });
    }

    /**
     * initOthers
     */
    private void initOthers() {

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        mJxit_esc = Jxit_esc.getInstance();

        registerBroadcastReceiver();
    }

    /**
     * registerBroadcastReceiver
     */
    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        this.registerReceiver(mActivityReceiver, filter);
    }

    /**
     * clickDemmoBtn
     */
    private void clickDemmoBtn(){
        mFragmentDemo = new FragmentDemo();
        clickFragmentBluetooth = false;
        tvTitle.setText(R.string.demo_title);
        btnRight.setVisibility(Button.VISIBLE);


        FragmentTransaction fragmentTransaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_content, mFragmentDemo);
        fragmentTransaction.commit();
    }

    /**
     * clickBluetoothBtn
     */
    private void clickBluetoothBtn(){
        mFragmentBluetooth = new FragmentBluetooth();
        toolBarMain.setNavigationIcon(R.drawable.arrow_left);
        tvTitle.setText(R.string.bluetooth_title);


        FragmentTransaction fragmentTransaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_content,mFragmentBluetooth,"mFragmentBluetooth");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    /**
     * Handler
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONNECTED:
                    btnRight.setBackgroundResource(R.drawable.printer20);
                    break;
                case CONNECT_FAILD:
                    Toast.makeText(MainActivity.this,btDeviceName+"连接失败",Toast.LENGTH_SHORT).show();

                    mJxit_esc.close();

                    mFragmentDemo.mListView.setEnabled(true);
                    break;
                case PRINT_FAILD:
                    Toast.makeText(MainActivity.this,btDeviceName+"发送数据失败",Toast.LENGTH_SHORT).show();

                    mJxit_esc.close();

                    mFragmentDemo.mListView.setEnabled(true);
                    break;
                case PRINT_FINISHED:
                    mJxit_esc.close();

                    mFragmentDemo.mListView.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * processBluetooth
     **/
    @Override
    public void processBluetooth(String str) {
        switch (str) {
            case "@关闭蓝牙":
                btnRight.setBackgroundResource(R.drawable.bluetooth_24);
//                btnRight.setImageResource(R.drawable.icon_plus_32);
                break;
            case "@退出蓝牙界面":
                Log.i("processBluetooth","退出蓝牙界面");
                FragmentBluetoothExit();
                break;
            case "@没有相机权限":
                Toast.makeText(this, "请在应用管理中打开“相机”访问权限！", Toast.LENGTH_SHORT).show();
                break;
            case "@MAC地址错误":
                Toast.makeText(this, "获取蓝牙MAC地址错误！", Toast.LENGTH_SHORT).show();
                break;
            default:
                FragmentBluetoothExit(str);
                break;
        }
    }

    public void FragmentBluetoothExit(){

        toolBarMain.setNavigationIcon(R.drawable.usb1_32);

        FragmentTransaction fragmentTransaction1 = MainActivity.this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction1.remove(mFragmentBluetooth);
        fragmentTransaction1.commit();

        clickFragmentBluetooth = false;

    }

    public void FragmentBluetoothExit(String str){
        btnRight.setBackgroundResource(R.drawable.printer20);

        btDeviceName = str.substring(0, str.length() - 17);
        btDeviceAddress = str.substring(str.length() - 17);

        FragmentBluetoothExit();
    }


    /**
     * processDemo
     **/
    @Override
    public void processDemo(String str) {
        switch (str){
            case "@打印文本":
                if(!mBtAdapter.isEnabled()){
                    Toast.makeText(MainActivity.this, "请打开蓝牙！", Toast.LENGTH_SHORT).show();
                }else{
                    if (BluetoothAdapter.checkBluetoothAddress(btDeviceAddress) && btDeviceAddress != null) {
                        mFragmentDemo.mListView.setEnabled(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!mJxit_esc.connectDevice(btDeviceAddress)) {
                                    mHandler.sendEmptyMessage(CONNECT_FAILD);
                                } else {
                                    if (!printText()) {
                                        mHandler.sendEmptyMessage(PRINT_FAILD);
                                    } else {
                                        mHandler.sendEmptyMessageDelayed(PRINT_FINISHED, 6000);
                                    }
                                }
                            }
                        }).start();
                    }else Toast.makeText(MainActivity.this, "请选择打印机！", Toast.LENGTH_SHORT).show();
                }
                break;
            case "@打印图片":
                if(!mBtAdapter.isEnabled()){
                    Toast.makeText(MainActivity.this, "请打开蓝牙！", Toast.LENGTH_SHORT).show();
                }else {
                    if (BluetoothAdapter.checkBluetoothAddress(btDeviceAddress) && btDeviceAddress != null) {
                        mFragmentDemo.mListView.setEnabled(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!mJxit_esc.connectDevice(btDeviceAddress)) {
                                    mHandler.sendEmptyMessage(CONNECT_FAILD);
                                } else {
                                    if (!printPicture()) {
                                        mHandler.sendEmptyMessage(PRINT_FAILD);
                                    } else {
                                        mHandler.sendEmptyMessageDelayed(PRINT_FINISHED, 1500);
                                    }
                                }
                            }
                        }).start();
                    } else Toast.makeText(MainActivity.this, "请选择打印机！", Toast.LENGTH_SHORT).show();
                }
                break;
            case "@打印一维条码":
                if(!mBtAdapter.isEnabled()){
                    Toast.makeText(MainActivity.this, "请打开蓝牙！", Toast.LENGTH_SHORT).show();
                }else {
                    if (BluetoothAdapter.checkBluetoothAddress(btDeviceAddress) && btDeviceAddress != null) {
                        mFragmentDemo.mListView.setEnabled(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!mJxit_esc.connectDevice(btDeviceAddress)) {
                                    mHandler.sendEmptyMessage(CONNECT_FAILD);
                                } else {
                                    if (!printBarcode1d()) {
                                        mHandler.sendEmptyMessage(PRINT_FAILD);
                                    } else {
                                        mHandler.sendEmptyMessageDelayed(PRINT_FINISHED, 1500);
                                    }
                                }
                            }
                        }).start();
                    } else Toast.makeText(MainActivity.this, "请选择打印机！", Toast.LENGTH_SHORT).show();
                }
                break;
            case "@打印二维条码":
                if(!mBtAdapter.isEnabled()){
                    Toast.makeText(MainActivity.this, "请打开蓝牙！", Toast.LENGTH_SHORT).show();
                }else {
                    if (BluetoothAdapter.checkBluetoothAddress(btDeviceAddress) && btDeviceAddress != null) {
                        mFragmentDemo.mListView.setEnabled(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!mJxit_esc.connectDevice(btDeviceAddress)) {
                                    mHandler.sendEmptyMessage(CONNECT_FAILD);
                                } else {
                                    if (!printBarcode2d()) {
                                        mHandler.sendEmptyMessage(PRINT_FAILD);
                                    } else {
                                        mHandler.sendEmptyMessageDelayed(PRINT_FINISHED, 1500);
                                    }
                                }
                            }
                        }).start();
                    } else Toast.makeText(MainActivity.this, "请选择打印机！", Toast.LENGTH_SHORT).show();
                }
                break;
            case "@打印曲线":
                if(!mBtAdapter.isEnabled()){
                    Toast.makeText(MainActivity.this, "请打开蓝牙！", Toast.LENGTH_SHORT).show();
                }else {
                    if (BluetoothAdapter.checkBluetoothAddress(btDeviceAddress) && btDeviceAddress != null) {
                        mFragmentDemo.mListView.setEnabled(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!mJxit_esc.connectDevice(btDeviceAddress)) {
                                    mHandler.sendEmptyMessage(CONNECT_FAILD);
                                } else {
                                    if (!printCurve()) {
                                        mHandler.sendEmptyMessage(PRINT_FAILD);
                                    } else {
                                        mHandler.sendEmptyMessageDelayed(PRINT_FINISHED, 1500);
                                    }
                                }
                            }
                        }).start();
                    } else Toast.makeText(MainActivity.this, "请选择打印机！", Toast.LENGTH_SHORT).show();
                }
                break;
            case "@打印表格":
                if(!mBtAdapter.isEnabled()){
                    Toast.makeText(MainActivity.this, "请打开蓝牙！", Toast.LENGTH_SHORT).show();
                }else {
                    if (BluetoothAdapter.checkBluetoothAddress(btDeviceAddress) && btDeviceAddress != null) {
                        mFragmentDemo.mListView.setEnabled(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!mJxit_esc.connectDevice(btDeviceAddress)) {
                                    mHandler.sendEmptyMessage(CONNECT_FAILD);
                                } else {
                                    if (!printTable()) {
                                        mHandler.sendEmptyMessage(PRINT_FAILD);
                                    } else {
                                        mHandler.sendEmptyMessageDelayed(PRINT_FINISHED, 1500);
                                    }
                                }
                            }
                        }).start();
                    } else Toast.makeText(MainActivity.this, "请选择打印机", Toast.LENGTH_SHORT).show();
                }
                break;
            case "@控制命令":
                if(!mBtAdapter.isEnabled()){
                    Toast.makeText(MainActivity.this, "请打开蓝牙！", Toast.LENGTH_SHORT).show();
                }else {
                    if (BluetoothAdapter.checkBluetoothAddress(btDeviceAddress) && btDeviceAddress != null) {
                        mFragmentDemo.mListView.setEnabled(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!mJxit_esc.connectDevice(btDeviceAddress)) {
                                    mHandler.sendEmptyMessage(CONNECT_FAILD);
                                } else {
                                    if (!printControlCommand()) {
                                        mHandler.sendEmptyMessage(PRINT_FAILD);
                                    } else {
                                        mHandler.sendEmptyMessageDelayed(PRINT_FINISHED, 7000);
                                    }
                                }
                            }
                        }).start();
                    } else Toast.makeText(MainActivity.this, "请选择打印机！", Toast.LENGTH_SHORT).show();
                }
                break;
            case "@打印餐饮账单":
                if(!mBtAdapter.isEnabled()){
                    Toast.makeText(MainActivity.this, "请打开蓝牙！", Toast.LENGTH_SHORT).show();
                }else {
                    if (BluetoothAdapter.checkBluetoothAddress(btDeviceAddress) && btDeviceAddress != null) {
                        mFragmentDemo.mListView.setEnabled(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!mJxit_esc.connectDevice(btDeviceAddress)) {
                                    mHandler.sendEmptyMessage(CONNECT_FAILD);
                                } else {
                                    if (!printCateringBills()) {
                                        mHandler.sendEmptyMessage(PRINT_FAILD);
                                    } else {
                                        mHandler.sendEmptyMessageDelayed(PRINT_FINISHED, 3000);
                                    }
                                }
                            }
                        }).start();
                    } else Toast.makeText(MainActivity.this, "请选择打印机！", Toast.LENGTH_SHORT).show();
                }
                break;
            case "@打印巡查结果":
                if(!mBtAdapter.isEnabled()){
                    Toast.makeText(MainActivity.this, "请打开蓝牙！", Toast.LENGTH_SHORT).show();
                }else {
                    if (BluetoothAdapter.checkBluetoothAddress(btDeviceAddress) && btDeviceAddress != null) {
                        mFragmentDemo.mListView.setEnabled(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!mJxit_esc.connectDevice(btDeviceAddress)) {
                                    mHandler.sendEmptyMessage(CONNECT_FAILD);
                                } else {
                                    if (!printPatrolResult()) {
                                        mHandler.sendEmptyMessage(PRINT_FAILD);
                                    } else {
                                        mHandler.sendEmptyMessageDelayed(PRINT_FINISHED, 5000);
                                    }
                                }
                            }
                        }).start();
                    } else Toast.makeText(MainActivity.this, "请选择打印机", Toast.LENGTH_SHORT).show();
                }
                break;
            case "@打印货品清单":
                if(!mBtAdapter.isEnabled()){
                    Toast.makeText(MainActivity.this, "请打开蓝牙！", Toast.LENGTH_SHORT).show();
                }else {
                    if (BluetoothAdapter.checkBluetoothAddress(btDeviceAddress) && btDeviceAddress != null) {
                        mFragmentDemo.mListView.setEnabled(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!mJxit_esc.connectDevice(btDeviceAddress)) {
                                    mHandler.sendEmptyMessage(CONNECT_FAILD);
                                } else {
                                    if (!printGoodsList()) {
                                        mHandler.sendEmptyMessage(PRINT_FAILD);
                                    } else {
                                        mHandler.sendEmptyMessageDelayed(PRINT_FINISHED, 4000);
                                    }
                                }
                            }
                        }).start();
                    } else Toast.makeText(MainActivity.this, "请选择打印机！", Toast.LENGTH_SHORT).show();
                }
                break;
            case "@打印彩票单据":
                if(!mBtAdapter.isEnabled()){
                    Toast.makeText(MainActivity.this, "请打开蓝牙！", Toast.LENGTH_SHORT).show();
                }else {
                    if (BluetoothAdapter.checkBluetoothAddress(btDeviceAddress) && btDeviceAddress != null) {
                        mFragmentDemo.mListView.setEnabled(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!mJxit_esc.connectDevice(btDeviceAddress)) {
                                    mHandler.sendEmptyMessage(CONNECT_FAILD);
                                } else {
                                    if (!printLottery()) {
                                        mHandler.sendEmptyMessage(PRINT_FAILD);
                                    } else {
                                        mHandler.sendEmptyMessageDelayed(PRINT_FINISHED, 3000);
                                    }
                                }
                            }
                        }).start();
                    } else Toast.makeText(MainActivity.this, "请选择打印机！", Toast.LENGTH_SHORT).show();
                }
                break;
            case "@选择文字打印":
                if(btDeviceAddress == null) {
                    Toast.makeText(MainActivity.this,"请选择打印机",Toast.LENGTH_SHORT).show();
                    clickBluetoothBtn();
                }else {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, TextActivity.class);
                    intent.putExtra("btDeviceAddress", btDeviceAddress);
                    startActivity(intent);
                }
                break;
            case "@选择图片打印":
                if(btDeviceAddress == null) {
                    Toast.makeText(MainActivity.this,"请选择打印机",Toast.LENGTH_SHORT).show();
                    clickBluetoothBtn();
                }else {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, PictureActivity.class);
                    intent.putExtra("btDeviceAddress", btDeviceAddress);
                    startActivity(intent);
                }
                break;
            case "@选择一维码打印":
                if(btDeviceAddress == null) {
                    Toast.makeText(MainActivity.this,"请选择打印机",Toast.LENGTH_SHORT).show();
                    clickBluetoothBtn();
                }else {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, Barcode1dActivity.class);
                    intent.putExtra("btDeviceAddress", btDeviceAddress);
                    startActivity(intent);
                }
                break;
            case "@选择二维码打印":
                if(btDeviceAddress == null) {
                    Toast.makeText(MainActivity.this,"请选择打印机",Toast.LENGTH_SHORT).show();
                    clickBluetoothBtn();
                }else {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, Barcode2dActivity.class);
                    intent.putExtra("btDeviceAddress", btDeviceAddress);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    /**
     * printText
     */
    private boolean printText(){
        try {
            mJxit_esc.esc_reset();
            mJxit_esc.esc_print_text("打印文本效果展示：\n");
            mJxit_esc.esc_align(0);
            mJxit_esc.esc_print_text("左对齐效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_align(1);
            mJxit_esc.esc_print_text("居中对齐效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_align(2);
            mJxit_esc.esc_print_text("右对齐效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_bold(true);
            mJxit_esc.esc_print_text("加粗效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_underline(2);
            mJxit_esc.esc_print_text("下划线效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_print_text("同行不同高效果演示：1倍");
            mJxit_esc.esc_character_size(22);
            mJxit_esc.esc_print_text("2倍");
            mJxit_esc.esc_character_size(33);
            mJxit_esc.esc_print_text("3倍");
            mJxit_esc.esc_character_size(44);
            mJxit_esc.esc_print_text("4倍\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_print_text("放大1倍效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_character_size(22);
            mJxit_esc.esc_print_text("放大2倍效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_character_size(33);
            mJxit_esc.esc_print_text("放大3倍效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_character_size(44);
            mJxit_esc.esc_print_text("放大4倍效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_font(0);
            mJxit_esc.esc_print_text("字体A效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_font(1);
            mJxit_esc.esc_print_text("字体B效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_font(2);
            mJxit_esc.esc_print_text("字体C效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_font(3);
            mJxit_esc.esc_print_text("字体D效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_rotate(1);
            mJxit_esc.esc_print_text("顺时针旋转90°效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_rotate(2);
            mJxit_esc.esc_print_text("顺时针旋转180°效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_rotate(3);
            mJxit_esc.esc_print_text("顺时针旋转270°效果演示abc123：\n");
            mJxit_esc.esc_reset();
            mJxit_esc.esc_black_white_reverse(true);
            mJxit_esc.esc_print_text("黑白反显效果演示abc123：\n\n\n\n\n");
            mJxit_esc.esc_reset();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * printPicture
     */
    private boolean printPicture(){
        byte[] topBytes = new byte[]{0x1B, 0x40, 0x1B, 0x64, 0x01};
        if(!mJxit_esc.esc_write_bytes(topBytes)){
            return false;
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(getAssets().open("jxit_logo360_77.bmp"));
            Bitmap bitmap = BitmapFactory.decodeStream(bis);
            if(!printBitmap(bitmap)){
                return false;
            }
            byte[] endBytes = new byte[]{0x1B, 0x40, 0x1B, 0x64, 0x04};
            if(!mJxit_esc.esc_write_bytes(endBytes)){
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * printBitmap
     */
    private boolean printBitmap(Bitmap bitmap) {
        bitmap = Bitmap.createBitmap(bitmap);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int heightbytes = (height - 1) / 8 + 1;
        int bufsize = width * heightbytes;
        byte[] maparray = new byte[bufsize];
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        /**解析图片 获取位图数据**/
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int pixel = pixels[width * j + i];
                if (pixel == Color.BLACK) {
                    maparray[i + (j / 8) * width] |= (byte) (0x80 >> (j % 8));
                }
            }
        }
        byte[] Cmd = new byte[5];
        byte[] pictureTop = new byte[]{0x1B,0x33,0x00};
        if(!mJxit_esc.esc_write_bytes(pictureTop)){
            return false;
        }
        /**对位图数据进行处理**/
        for (int i = 0; i < heightbytes; i++) {
            Cmd[0] = 0x1B;
            Cmd[1] = 0x2A;
            Cmd[2] = 0x01;
            Cmd[3] = (byte) (width % 256);
            Cmd[4] = (byte) (width / 256);
            if(!mJxit_esc.esc_write_bytes(Cmd)){
                return false;
            }
            if(!mJxit_esc.esc_write_subbytes(maparray, i * width, width)){
                return false;
            }
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x0D,0x0A})){
                return false;
            }

        }
        return true;
    }

    /**
     * printBarcode1d
     */
    private boolean printBarcode1d(){
        if(!mJxit_esc.esc_write_bytes(new byte[]{0x0D,0x0A})) return false;
        if(!mJxit_esc.esc_barcode_1d(2,1,3,80,0,"123456789012")) return false;
        if(!mJxit_esc.esc_write_bytes(new byte[]{0x0D,0x0A,0x1B,0x64,0x02})) return false;
        return true;
    }

    /**
     * printBarcode2d
     */
    private boolean printBarcode2d(){
        if(!mJxit_esc.esc_write_bytes(new byte[]{0x0D,0x0A})) return false;
        if(!mJxit_esc.esc_write_bytes(new byte[]{0x1D,0x77,0x04})) return false;
        if(!mJxit_esc.esc_write_bytes(new byte[]{0x1D,0x68,0x64})) return false;
        byte[] qrcodeBytes = new byte[]{0x1D,0x6B,0x20,0x03,0x01,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x30,0x31,0x32,0x00};
        if(!mJxit_esc.esc_write_bytes(qrcodeBytes)) return false;
        if(!mJxit_esc.esc_write_bytes(new byte[]{0x0D,0x0A,0x1B,0x64,0x04})) return false;

        return true;
    }

    /**
     * printCurve
     */
    private boolean printCurve() {
        if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x40,0x0D,0x0A})) return false;

        byte curveBytes[] = new byte[7];
        byte y1,y1s = 0;
        byte[] sinBytes = new byte[4];
        curveBytes[0] = 0x1D;
        curveBytes[1] = 0x27;
        curveBytes[2] = 1;
        curveBytes[3] = 30;
        curveBytes[4] = 0;
        curveBytes[5] = (byte) 230;
        curveBytes[6] = 0;
        if(!mJxit_esc.esc_write_bytes(curveBytes)) return false;
        curveBytes[0] = 0x1D;
        curveBytes[1] = 0x27;
        curveBytes[2] = 2;
        curveBytes[3] = (byte) 130;
        curveBytes[4] = 0;
        curveBytes[5] = (byte) 130;
        curveBytes[6] = 0;

        for(int i=1;i<=512;i++){
            y1 = (byte) (sin(i*3.1415926/128)*100+130);

            if(i==1) { y1s = y1;}
            if(!mJxit_esc.esc_write_bytes(curveBytes)) return false;

            sinBytes[0] = y1s;
            sinBytes[1] = 0;
            sinBytes[2] = y1;
            sinBytes[3] = 0;
            if(!mJxit_esc.esc_write_bytes(sinBytes)) return false;
            y1s = y1;

        }
        if(!mJxit_esc.esc_write_bytes(new byte[]{0x0D,0x0A,0x1B,0x4A,0x40})) return false;
        return true;
    }

    /**
     * printTable
     */
    private boolean printTable(){
        String s1 = "┏━━┳━━━┳━━━┳━━━┓\n";
        String s2 = "┃序号┃姓名  ┃性别  ┃年龄  ┃\n┣━━╋━━━╋━━━╋━━━┫\n";
        String s3 = "┃ 1  ┃张三  ┃ 男   ┃  18  ┃\n┣━━╋━━━╋━━━╋━━━┫\n";
        String s4 = "┃ 2  ┃李四  ┃ 女   ┃  17  ┃\n┣━━╋━━━╋━━━╋━━━┫\n";
        String s5 = "┃ 3  ┃王五  ┃ 男   ┃  16  ┃\n┗━━┻━━━┻━━━┻━━━┛\n\n\n\n\n";
        if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x40,0x1B,0x33,0x00,0x0D,0x0A})) return false;
        if(!mJxit_esc.esc_print_text(s1)) return false;
        if(!mJxit_esc.esc_print_text(s2)) return false;
        if(!mJxit_esc.esc_print_text(s3)) return false;
        if(!mJxit_esc.esc_print_text(s4)) return false;
        if(!mJxit_esc.esc_print_text(s5)) return false;
        if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x40})) return false;

        return true;
    }


    /**
     * printControlCommand
     */
    private boolean printControlCommand(){
        try {
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x40,0x0D,0x0A})) return false;
            if(!mJxit_esc.esc_print_text("控制命令效果展示：\n")) return false;
            if(!mJxit_esc.esc_print_text("打印并回车效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x0D})) return false;
            if(!mJxit_esc.esc_print_text("打印并走纸一行效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x0A})) return false;
            if(!mJxit_esc.esc_print_text("打印并走纸100个纵向移动单位效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x4A,0x64})) return false;
            if(!mJxit_esc.esc_print_text("打印并走纸10个行高效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x64,0x0A})) return false;
            if(!mJxit_esc.esc_print_text("横向跳格效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x09})) return false;
            if(!mJxit_esc.esc_print_text("1")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x09})) return false;
            if(!mJxit_esc.esc_print_text("2")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x09})) return false;
            if(!mJxit_esc.esc_print_text("3")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x09})) return false;
            if(!mJxit_esc.esc_print_text("4\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x24,0x00,0x00})) return false;
            if(!mJxit_esc.esc_print_text("绝对位置0、0效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x24,0x32,0x32})) return false;
            if(!mJxit_esc.esc_print_text("绝对位置50、50效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x24, (byte) 0x96, (byte) 0x96})) return false;
            if(!mJxit_esc.esc_print_text("绝对位置150、150效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x40})) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x32})) return false;
            if(!mJxit_esc.esc_print_text("设置默认行高效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x33,0x00})) return false;
            if(!mJxit_esc.esc_print_text("设置行高为0效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x33,0x32})) return false;
            if(!mJxit_esc.esc_print_text("设置行高为50效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x33,(byte) 0x96})) return false;
            if(!mJxit_esc.esc_print_text("设置行高为150效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x40})) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x20,0x00})) return false;
            if(!mJxit_esc.esc_print_text("设置右边距为0效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x20,0x32})) return false;
            if(!mJxit_esc.esc_print_text("设置右边距为50效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x20,0x64})) return false;
            if(!mJxit_esc.esc_print_text("设置右边距为100效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x20, (byte) 0x96})) return false;
            if(!mJxit_esc.esc_print_text("设置右边距为150效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x40})) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1D,0x4C,0x32,0x00})) return false;
            if(!mJxit_esc.esc_print_text("设置左边距为50、0效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1D,0x4C,0x32,0x01})) return false;
            if(!mJxit_esc.esc_print_text("设置左边距为50、1效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1D,0x4C,0x64,0x01})) return false;
            if(!mJxit_esc.esc_print_text("设置左边距为100、1效果演示：\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1D,0x4C,0x00,0x00})) return false;
            if(!mJxit_esc.esc_print_text("设置左边距为0效果演示：\n\n\n\n\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x40})) return false;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * printCateringBills
     */
    private boolean printCateringBills(){
        try {
            if(!mJxit_esc.esc_reset()) return false;
            if(!mJxit_esc.esc_default_line_height()) return false;
            if(!mJxit_esc.esc_align(1)) return false;
            if(!mJxit_esc.esc_print_text("红星餐厅\n\n")) return false;
            if(!mJxit_esc.esc_character_size(22)) return false;
            if(!mJxit_esc.esc_print_text("桌号：1号桌\n\n")) return false;
            if(!mJxit_esc.esc_reset()) return false;
            if(!mJxit_esc.esc_align(0)) return false;
            if(!mJxit_esc.esc_print_text(printTwoData("订单编号", "201704161515\n"))) return false;
            if(!mJxit_esc.esc_print_text(printTwoData("点菜时间", "2017-04-16 10:46\n"))) return false;
            if(!mJxit_esc.esc_print_text(printTwoData("上菜时间", "2017-04-16 11:46\n"))) return false;
            if(!mJxit_esc.esc_print_text(printTwoData("人数：2人", "收银员：张三\n"))) return false;
            if(!mJxit_esc.esc_print_text("--------------------------------\n")) return false;
            if(!mJxit_esc.esc_bold(true)) return false;
            if(!mJxit_esc.esc_print_text(printThreeData("项目", "数量", "金额\n"))) return false;
            if(!mJxit_esc.esc_print_text("--------------------------------\n")) return false;
            if(!mJxit_esc.esc_bold(false)) return false;
            if(!mJxit_esc.esc_print_text(printThreeData("面", "1", "0.00\n"))) return false;
            if(!mJxit_esc.esc_print_text(printThreeData("米饭", "1", "6.00\n"))) return false;
            if(!mJxit_esc.esc_print_text(printThreeData("铁板烧", "1", "26.00\n"))) return false;
            if(!mJxit_esc.esc_print_text(printThreeData("红烧鲤鱼", "1", "226.00\n"))) return false;
            if(!mJxit_esc.esc_print_text(printThreeData("红烧牛肉面", "1", "2226.00\n"))) return false;
            if(!mJxit_esc.esc_print_text(printThreeData("红烧牛肉面红烧牛肉面红烧牛肉面", "888", "98886.00\n"))) return false;
            if(!mJxit_esc.esc_print_text("--------------------------------\n")) return false;
            if(!mJxit_esc.esc_print_text(printTwoData("合计", "53.50\n"))) return false;
            if(!mJxit_esc.esc_print_text(printTwoData("抹零", "3.50\n"))) return false;
            if(!mJxit_esc.esc_print_text("--------------------------------\n")) return false;
            if(!mJxit_esc.esc_print_text(printTwoData("应收", "50.00\n"))) return false;
            if(!mJxit_esc.esc_print_text("--------------------------------\n")) return false;
            if(!mJxit_esc.esc_align(0)) return false;
            if(!mJxit_esc.esc_print_text("备注：不要辣、不要香菜！\n\n\n\n\n")) return false;
            if(!mJxit_esc.esc_reset()) return false;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * printPatrolResult
     */
    private boolean printPatrolResult(){
        try {
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x40,0x1B,0x61,0x01,0x1C,0x21,0x08})) return false;
            if(!mJxit_esc.esc_print_text("---------------------------------------------------\n厦门市工商行政管理局\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x40,0x1B,0x61,0x01,0x1C,0x57,0x01})) return false;
            if(!mJxit_esc.esc_print_text("责令改正通知书\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1C,0x57,0x00})) return false;
            if(!mJxit_esc.esc_print_text("厦工商食责[2012]  14号\n（工商部门留存）\n")) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x40})) return false;
            if(!mJxit_esc.esc_print_text("厦门集正商贸有限公司：\n  经查，你（单位）从事批发业务的食品经营企业没有向购货者开具销售票据或者清单，进货" +
                    "时未查验许可证和相关证明文件。上诉行为违反了《流通环节食品安全监督管理办法》第十四条第二款、《食品安全法》第三十九条第一" +
                    "款的规定，构成了从事批发业务的食品经营企业没有向购货者开具销售票据或者清单、进货时未查验许可证和相关证明文件行为。根据" +
                    "《流通环节食品安全监督管理办法》第六十三条、《食品安全法》第八十七条的规定，现责令你（单位）立即改正。\n  如果对本责令改" +
                    "正通知不服，可以在收到本通知之日起六十日内向厦门市人民政府行政复议委员会申请复议；也可以在三个月内依法向厦门市思明区人民" +
                    "法院提起诉讼。\n")) return false;

            String pictureStr = "0D 0A 1B 45 00 1B 61 02 0D 0A 1B 45 00 1B 4A 46 1D 76 30 00 16 00 C9 00 00 00 00 00 00 00 00 00 00 01 FF FF FC 00 " +
                    "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF F8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0F FF FF FF " +
                    "FF FF 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF F8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 FF FF " +
                    "FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF 07 FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 FF " +
                    "FF F8 00 00 00 FF FF F8 00 00 00 00 00 00 00 00 00 00 00 00 07 FF FF 00 00 00 00 07 FF FF 00 00 00 00 00 00 00 00 00 00 00 00 " +
                    "1F FF F0 00 00 00 00 00 7F FF C0 00 00 00 00 00 00 00 00 00 00 00 7F FF 80 00 00 00 00 00 0F FF F0 00 00 00 00 00 00 00 00 00 " +
                    "00 01 FF FC 00 00 00 00 00 20 01 FF FC 00 00 00 00 00 00 00 00 00 00 03 FF E0 00 30 00 00 02 30 00 3F FE 00 00 00 00 00 00 00 " +
                    "00 00 00 0F FF 80 00 38 00 00 03 39 80 0F FF 80 00 00 00 00 00 00 00 00 00 3F FE 01 00 FC 00 00 07 F9 C0 03 FF E0 00 00 00 00 " +
                    "00 00 00 00 00 7F F8 01 EF F8 00 00 07 FB C0 00 FF F0 00 00 00 00 00 00 00 00 01 FF E0 01 FF F8 00 00 07 73 C0 00 3F FC 00 00 " +
                    "00 00 00 00 00 00 03 FF 80 01 EE 70 00 00 07 77 80 00 0F FE 00 00 00 00 00 00 00 00 0F FE 00 01 E0 70 00 00 0F F7 98 00 03 FF " +
                    "80 00 00 00 00 00 00 00 1F F8 00 00 E0 F0 00 00 0E EF F8 00 00 FF C0 00 00 00 00 00 00 00 3F F0 00 00 E0 E0 00 00 0E EF FC 00 " +
                    "00 7F E0 00 00 00 00 00 00 00 FF C0 00 00 FD E0 80 00 0E FF FC 00 00 1F F8 00 00 00 00 00 00 01 FF 80 00 00 FF E1 C0 00 1F FF " +
                    "8C 00 00 0F FC 00 00 00 00 00 00 03 FF 00 00 00 7F C3 E0 00 1D FF 80 00 00 07 FE 00 00 00 00 00 00 07 FC 00 00 03 FF FF E0 00 " +
                    "1D F9 C0 00 00 01 FF 00 00 00 00 00 00 0F F8 00 00 03 FB FF C0 00 1D F1 C0 00 00 00 FF 80 00 00 00 00 00 1F F0 00 00 01 FF FF " +
                    "E0 00 3F E1 C0 00 00 00 7F C0 00 00 00 00 00 3F E0 00 00 00 3B FF E0 00 33 E1 C0 00 00 00 3F E0 00 00 00 00 00 7F C0 00 00 00 " +
                    "39 7F E0 00 3B 81 C0 00 00 06 1F F0 00 00 00 00 00 FF 80 38 00 00 3C 7F E0 00 3F 81 C0 00 00 0E 0F F8 00 00 00 00 01 FE 00 3F " +
                    "00 00 3C FF F0 00 3F F1 80 00 04 1E 03 FC 00 00 00 00 03 FC 00 3F 00 00 1D FF F0 00 7F FF 80 00 06 3F 01 FE 00 00 00 00 07 F8 " +
                    "00 3E 00 00 1F FF 70 00 77 FF 80 00 07 7E 00 FF 00 00 00 00 0F F8 7E 78 00 00 1F FE 70 00 77 7F 80 00 07 FF 00 FF 80 00 00 00 " +
                    "0F F0 7E F0 00 00 1F EE 70 00 77 77 00 00 0F FF 80 7F 80 00 00 00 1F E0 3F E7 80 00 0F EE 70 00 F7 77 00 00 7F FF C0 3F C0 00 " +
                    "00 00 3F C0 0F C7 80 00 1E FE 70 00 EF F7 00 00 FF FF E0 1F E0 00 00 00 7F 80 07 C7 C0 00 3D DE 78 07 EE FF 00 00 FF FF F0 0F " +
                    "F0 00 00 00 7F 00 0F EF E0 00 7D 9F F8 03 FE EE 00 00 0F DF FE 07 F0 00 00 00 FE 00 1F FF F0 00 79 BB F0 03 FF EF 80 00 7F EF " +
                    "3E 03 F8 00 00 01 FE 00 3C FC F8 00 38 79 F0 00 1F FF 80 00 7F E7 FC 03 FC 00 00 01 FC 00 78 FC 7C 00 00 70 60 00 01 FF 80 00 " +
                    "FD EF FC 01 FC 00 00 03 F8 00 F1 FE 3E 00 00 E0 00 00 00 1F 80 01 FF FF F0 00 FE 00 00 07 F0 01 EF DF 1F 00 00 C0 00 00 00 01 " +
                    "C0 03 FF FF C0 00 7F 00 00 07 F0 00 4F 9F 8F 80 00 00 00 00 00 00 00 07 FF FE 00 00 7F 00 00 0F E0 00 0F CF C7 80 00 00 00 00 " +
                    "00 00 00 0F FF 1E 00 00 3F 80 00 0F C0 00 07 E7 C3 C0 00 00 00 00 00 00 00 1F FF 9E 00 00 1F 80 00 1F C0 00 03 E3 EF C0 00 00 " +
                    "00 00 00 00 00 3F 7F FE 00 00 1F C0 00 3F 80 00 01 F1 FF C0 00 00 00 00 00 00 00 7F BF FE 00 00 0F E0 00 3F 80 00 00 F8 FF C0 " +
                    "00 00 00 00 00 00 00 FB DF FE 00 00 0F E0 00 7F 00 00 00 7C 7C 00 00 00 00 00 00 00 00 FD EF FE 00 00 07 F0 00 7F 00 00 00 3E " +
                    "3E 00 00 00 00 00 00 00 00 7E FF 80 00 00 07 F0 00 FE 00 00 00 1F 1F 00 00 00 00 00 00 00 00 0F FF 00 00 00 03 F8 00 FE 00 00 " +
                    "00 0F 8F 80 00 00 00 00 00 00 00 07 FE 00 00 00 03 F8 01 FC 00 00 00 07 C7 00 00 00 00 70 00 00 00 03 FC 00 00 00 01 FC 01 FC " +
                    "00 00 00 03 C3 00 00 00 00 70 00 00 00 00 F8 00 00 00 01 FC 01 F8 00 00 00 01 C0 00 00 00 00 70 00 00 00 00 F8 00 00 00 00 FC " +
                    "03 F8 00 00 00 00 80 00 00 00 00 70 00 00 00 00 F0 00 00 00 00 FE 03 F0 00 00 00 00 00 00 00 00 00 78 00 00 00 00 20 00 00 00 " +
                    "00 7E 07 F0 03 00 00 00 00 00 00 00 00 F8 00 00 00 00 00 00 00 18 00 7F 07 E0 3F 00 00 00 00 00 00 00 00 F8 00 00 00 00 00 00 " +
                    "00 1F FE 3F 07 E0 1E 00 00 00 00 00 00 00 00 FC 00 00 00 00 00 00 03 FF FC 3F 0F E0 1E 00 00 00 00 00 00 00 01 FC 00 00 00 00 " +
                    "00 00 03 FB FC 3F 0F C0 0E 70 00 00 00 00 00 00 01 FE 00 00 00 00 00 00 03 F9 F8 1F 0F C0 0C FC 00 00 00 00 00 00 03 FE 00 00 " +
                    "00 00 00 00 01 F7 E0 1F 1F C0 1C FF 80 00 00 00 00 00 03 FE 00 00 00 00 00 00 07 FF F8 1F 1F 81 DC 7F F0 00 00 00 00 00 03 FF " +
                    "00 00 00 00 00 00 3F FF F8 0F 1F 87 F8 EF FE 00 00 00 00 00 07 FF 00 00 00 00 00 01 FF DB F0 0F 1F 8F FE E1 FF 00 00 00 00 00 " +
                    "07 FF 80 00 00 00 00 0F FF DF FF 8F 3F 0F FF E0 3F 80 00 00 00 00 0F FF 80 00 00 00 00 7F FC DF FF 07 3F 00 3F FC 07 80 00 00 " +
                    "00 00 0F FF 80 00 00 00 00 FF DC EF 3F 07 3F 00 73 FF 8F C0 00 00 00 00 0F FF 80 00 00 00 00 FD CC EE 7E 07 3F 00 71 FF FE 00 " +
                    "00 00 00 00 0F FF 80 00 00 00 00 79 CE 6E FC 07 7E 00 63 8F FE 00 00 00 00 00 0F FF C0 00 00 00 00 1C EE FF FF 03 7E 00 E3 81 " +
                    "FF C0 00 00 00 00 1F FF C0 00 00 00 00 1C E7 FF EF 03 7E 00 EF E0 3F E0 00 00 00 00 1F FF C0 00 00 00 00 1C EF FF 8F 03 7E 00 " +
                    "EF FC 07 E0 00 00 00 00 1F FF E0 00 00 00 00 0E 7F FF CE 03 7E 01 E7 FF 80 E0 00 00 00 00 3F FF E0 00 00 00 00 0F FE 1F C0 03 " +
                    "7C 00 00 7F F0 00 00 00 00 00 3F FF F0 00 00 00 00 0F F8 07 80 01 7C 00 00 0F FC 00 00 00 00 00 3F FF F0 00 00 00 00 1F F0 03 " +
                    "00 01 FC 00 00 01 FC 00 00 00 00 00 7F FF F0 00 00 00 00 0F 00 00 00 01 FC 00 00 00 3C 00 07 FF FF FF FF FF FF FF FF FF 00 00 " +
                    "00 00 00 01 FC 00 00 00 04 00 07 FF FF FF FF FF FF FF FF FF 00 00 00 00 00 01 FC 00 00 00 00 00 01 FF FF FF FF FF FF FF FF FC " +
                    "00 00 00 00 00 01 FC 00 00 00 00 00 00 7F FF FF FF FF FF FF FF F0 00 00 00 00 00 01 FC 00 00 00 00 00 00 3F FF FF FF FF FF FF " +
                    "FF E0 00 00 00 00 00 01 FC 00 00 00 00 00 00 1F FF FF FF FF FF FF FF E0 00 00 00 00 00 01 FC 00 00 00 00 00 00 0F FF FF FF FF " +
                    "FF FF FF 80 00 00 00 00 00 01 FC 00 00 00 00 00 00 03 FF FF FF FF FF FF FF 00 00 00 00 00 00 01 F8 00 00 00 00 00 00 01 FF FF " +
                    "FF FF FF FF FC 00 00 00 00 00 00 00 F8 00 00 00 00 00 00 00 7F FF FF FF FF FF F8 00 00 00 00 00 00 00 F8 00 00 00 00 00 00 00 " +
                    "3F FF FF FF FF FF E0 00 00 00 00 00 00 00 F8 00 00 00 00 00 00 00 1F FF FF FF FF FF C0 00 00 00 78 00 00 00 F8 00 00 00 00 00 " +
                    "00 00 07 FF FF FF FF FF 00 00 00 00 78 00 00 00 FC 00 00 01 FE 00 00 00 03 FF FF FF FF FE 00 00 00 00 38 07 00 01 FC 00 00 7F " +
                    "FF 00 00 00 01 FF FF FF FF FC 00 00 00 00 3E 06 01 01 FC 00 0F FF FF 00 00 00 00 7F FF FF FF F8 00 00 00 03 9F FE 07 01 FC 05 " +
                    "FF FF 9E 00 00 00 00 3F FF FF FF F0 00 00 00 03 1F FF C7 01 FC 0F FF F0 1C 00 00 00 00 1F FF FF FF C0 00 00 00 03 0F FF FF 01 " +
                    "FC 1F FE 00 1C 00 00 00 00 1F FF FF FF C0 00 00 00 07 0E 1F FE 01 FC 1F C0 00 00 00 00 00 00 1F FF FF FF C0 00 00 00 07 1E 0F " +
                    "FE 01 FC 07 00 00 00 00 00 00 00 1F FF FF FF E0 00 00 00 07 1E FF 0F 01 FC 07 00 00 00 00 00 00 00 3F FF FF FF E0 00 00 00 06 " +
                    "1C FF CF 81 7C 07 00 00 00 00 00 00 00 3F FF FF FF F0 00 00 00 07 D8 FF FF 01 7C 03 00 00 00 00 00 00 00 7F FF FF FF F0 00 00 " +
                    "00 0F FE 7F FF 81 7E 03 80 00 00 00 00 00 00 7F FF FF FF F0 00 00 00 0F FF F1 FF 83 7E 03 80 00 01 80 00 00 00 7F FF FF FF F8 " +
                    "00 00 00 0E 7F FF C7 03 7E 03 00 00 3F C0 00 00 00 FF FF FF FF F8 00 00 00 0C 3B FF FE 03 7E 03 E0 07 FF E0 00 00 00 FF FF FF " +
                    "FF FC 00 00 00 0E 38 FF FE 03 7E 07 E0 FF FF C0 00 00 01 FF FF FF FF FC 00 00 00 0F 3D F3 FE 03 3F 0F FF FF F8 00 00 00 01 FF " +
                    "FF CF FF FC 00 00 00 1F 7D FF 8E 07 3F 0F BF FF 00 00 00 00 01 FF FF 07 FF FC 00 00 00 18 21 FF FE 07 3F 1E 7F E0 00 00 00 00 " +
                    "01 FF FC 01 FF FC 00 00 00 00 00 1F FF 07 3F 0C 7C 00 00 00 00 00 01 FF F8 00 FF FE 00 00 00 00 00 00 FF 07 1F 80 00 00 00 00 " +
                    "00 00 03 FF F0 00 3F FE 00 00 00 00 00 00 0C 0F 1F 80 00 00 00 00 00 00 03 FF C0 00 1F FE 00 00 01 00 00 00 00 0F 1F 80 00 00 " +
                    "00 00 00 00 03 FF 80 00 07 FF 00 00 03 80 00 00 00 0F 1F C0 00 00 00 3E 00 00 07 FF 00 00 03 FF 00 00 01 C0 00 00 00 1F 0F C0 " +
                    "00 00 00 3E 00 00 07 FC 00 00 01 FF 80 00 01 C0 00 00 00 1F 0F C0 00 00 00 1F 00 00 0F F8 00 00 00 FF 80 00 01 E0 00 00 00 1F " +
                    "0F E0 00 00 00 0F 00 00 0F E0 00 00 00 3F 80 00 00 E0 00 00 00 3F 07 E0 00 00 00 0F 00 00 0F C0 00 00 00 1F C0 00 00 F0 00 00 " +
                    "00 3F 07 E0 00 00 00 07 00 00 1F 80 00 00 00 0F C0 00 00 78 00 00 00 3F 07 F0 00 00 00 07 00 00 1F 00 00 00 00 03 C0 00 03 3C " +
                    "00 00 00 7F 03 F0 00 00 01 FF 00 00 1C 00 00 00 00 00 C0 00 03 BE 00 00 00 7E 03 F8 00 00 01 FF F0 00 10 00 00 00 00 00 40 00 " +
                    "03 DF 00 00 00 FE 01 F8 00 00 0F FF FC 00 00 00 00 00 00 00 00 00 03 FF 80 00 00 FC 01 FC 00 00 1F FF FE 00 00 00 00 00 00 00 " +
                    "00 07 E3 FF C0 00 01 FC 01 FC 00 00 3F 7E 1F 80 00 00 00 00 00 00 00 07 FF FF E0 00 01 FC 00 FE 00 00 7F BF C3 80 00 00 00 00 " +
                    "00 00 00 07 DF 7F F0 00 03 F8 00 FE 00 00 FF FF FB C0 00 00 00 00 00 00 00 07 9F 3F F8 00 03 F8 00 7F 00 01 FF FF FF C0 00 00 " +
                    "00 00 00 00 00 07 DF F9 FE 00 07 F0 00 7F 00 37 FF FC 3F 80 00 00 00 00 00 00 00 03 EF F3 FF 00 07 F0 00 3F 80 BF FF FC FE 00 " +
                    "00 00 00 00 00 00 00 01 F3 E7 9F 80 0F E0 00 3F 80 FF FB F9 F8 00 00 00 00 00 00 00 00 00 FD EF 1F C0 0F E0 00 1F C1 FF FF F7 " +
                    "E0 00 00 00 00 00 00 00 00 00 7F FE 3F F0 1F C0 00 0F C1 FF FF EF C0 00 00 00 00 00 00 00 00 00 3F 3C 79 F8 1F 80 00 0F E1 FF " +
                    "FF FF 00 00 00 00 00 00 00 00 00 00 1F B8 F1 F8 3F 80 00 07 F1 BF FF 7E 00 00 00 00 00 00 00 00 00 00 07 F1 E3 C0 7F 00 00 07 " +
                    "F0 1F FE FC 00 00 00 00 00 00 00 00 00 00 03 F5 C7 80 7F 00 00 03 F8 0F FD F8 00 00 00 00 00 00 00 00 00 00 01 EF 8F 00 FE 00 " +
                    "00 01 FC 0F FB E0 00 00 00 00 00 00 00 00 00 00 01 FF 9E 01 FC 00 00 01 FE 07 FF C0 00 00 00 00 00 00 00 00 00 00 00 0F 9C 03 " +
                    "FC 00 00 00 FE 03 FF 80 00 00 00 00 00 00 00 00 00 00 00 07 F8 03 F8 00 00 00 7F 01 FF 00 00 00 00 00 00 00 00 00 00 00 00 03 " +
                    "F8 07 F0 00 00 00 7F 80 FE 00 00 00 00 00 00 00 00 00 00 00 00 01 F0 0F F0 00 00 00 3F C0 78 00 00 00 00 00 00 00 00 00 00 00 " +
                    "00 00 F0 1F E0 00 00 00 1F E0 70 00 00 00 00 00 00 00 00 00 00 00 00 00 30 3F C0 00 00 00 0F F0 60 00 00 00 00 00 00 00 00 00 " +
                    "00 00 00 00 00 7F 80 00 00 00 0F F8 00 07 80 00 00 00 00 00 00 00 00 00 07 00 00 FF 80 00 00 00 07 F8 00 07 C0 00 00 00 00 00 " +
                    "00 00 00 00 1F 80 00 FF 00 00 00 00 03 FC 00 06 C0 00 00 00 00 00 00 00 00 00 1F C0 01 FE 00 00 00 00 01 FE 00 07 C8 00 00 00 " +
                    "00 00 00 00 00 00 19 C0 03 FC 00 00 00 00 00 FF 80 3F CE 00 00 00 00 00 00 00 00 03 9D C0 0F F8 00 00 00 00 00 7F C0 3B 1F 80 " +
                    "00 00 00 00 00 00 00 07 9E E0 1F F0 00 00 00 00 00 3F E0 3F 3B 80 00 00 00 00 00 00 00 0F EF C0 3F E0 00 00 00 00 00 1F F0 1F " +
                    "3E 80 00 00 00 00 00 00 00 0F FF C0 7F C0 00 00 00 00 00 0F F8 00 66 3C 00 00 00 00 00 00 03 EF 73 00 FF 80 00 00 00 00 00 07 " +
                    "FC 00 67 7E 00 00 00 00 00 00 07 E7 30 01 FF 00 00 00 00 00 00 03 FF 00 6E 77 00 00 00 00 00 00 77 67 F0 07 FE 00 00 00 00 00 " +
                    "00 01 FF 80 3E E7 7C 00 00 00 00 03 F7 F3 E0 0F FC 00 00 00 00 00 00 00 FF C0 1C E7 EE 30 00 00 00 C3 F3 F8 00 1F F8 00 00 00 " +
                    "00 00 00 00 3F F0 00 EE 6E F8 00 00 01 E3 63 B8 00 7F E0 00 00 00 00 00 00 00 1F F8 00 FE 3C FC F8 78 E1 E3 63 F8 00 FF C0 00 " +
                    "00 00 00 00 00 00 0F FE 00 7C FD CC F8 F9 E0 E0 63 F0 03 FF 80 00 00 00 00 00 00 00 03 FF 80 01 E9 DD DD D8 60 60 60 00 0F FE " +
                    "00 00 00 00 00 00 00 00 01 FF E0 01 FD DD DD F8 70 60 70 00 3F FC 00 00 00 00 00 00 00 00 00 7F F8 00 79 DD 9D FC 70 70 70 00 " +
                    "FF F0 00 00 00 00 00 00 00 00 00 3F FE 00 00 F9 DD DC 70 F8 00 03 FF E0 00 00 00 00 00 00 00 00 00 0F FF 80 00 71 F9 DC 78 F0 " +
                    "00 0F FF 80 00 00 00 00 00 00 00 00 00 03 FF E0 00 00 F9 F8 F8 00 00 3F FE 00 00 00 00 00 00 00 00 00 00 01 FF FC 00 00 00 70 " +
                    "80 00 01 FF FC 00 00 00 00 00 00 00 00 00 00 00 7F FF 80 00 00 00 00 00 0F FF F0 00 00 00 00 00 00 00 00 00 00 00 1F FF F0 00 " +
                    "00 00 00 00 7F FF C0 00 00 00 00 00 00 00 00 00 00 00 07 FF FF 00 00 00 00 07 FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 FF " +
                    "FF F8 00 00 00 FF FF F8 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF 07 FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 00 " +
                    "00 07 FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF F8 00 00 00 00 00 00 00 00 00 00 00 " +
                    "00 00 00 00 0F FF FF FF FF FF 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF F8 00 00 00 00 00 00 00 00 00 00 " +
                    "00 00 00 00 00 00 00 01 FF FF FC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " +
                    "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " +
                    "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " +
                    "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " +
                    "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " +
                    "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " +
                    "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " +
                    "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " +
                    "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " +
                    "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " +
                    "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0D 0A";
            String[] data = pictureStr.split(" ");
            byte[] pictureBytes = new byte[data.length];
            for (int i = 0; i < data.length; i++){
                pictureBytes[i] = (byte) Integer.parseInt(data[i],16);
            }
            if(!mJxit_esc.esc_write_bytes(pictureBytes)) return false;
            if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x64,0x04})) return false;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * printGoodsList
     */
    private boolean printGoodsList(){
        String s1 =  "┏━━━━━━━┳━━┳━━━┓\n";
        String s2 =  "┃   商品名称   ┃单位┃ 单价 ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s3 =  "┃玻璃纸        ┃ 张 ┃9.00  ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s4 =  "┃磨砂玻璃纸    ┃ 张 ┃11.00 ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s5 =  "┃签字笔芯      ┃ 支 ┃4.50  ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s6 =  "┃修正液/胶水   ┃ 瓶 ┃4.00  ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s7 =  "┃复印纸        ┃ 盒 ┃22    ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s8 =  "┃双面胶/透明胶 ┃ 卷 ┃11.20 ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s9 =  "┃回形针        ┃ 盒 ┃2.00  ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s10 = "┃订书机        ┃ 台 ┃16.6  ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s11 = "┃直尺          ┃ 把 ┃3.00  ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s12 = "┃订书针        ┃ 盒 ┃9.80  ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s13 = "┃胶水          ┃ 瓶 ┃9.60  ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s14 = "┃三格文件架    ┃ 个 ┃19.00 ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s15 = "┃三层活动文件架┃ 个 ┃36.00 ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s16 = "┃单格文件架    ┃ 个 ┃8.00  ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s17 = "┃文件柜        ┃ 个 ┃122   ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s18 = "┃介刀          ┃ 把 ┃17.5  ┃\n┣━━━━━━━╋━━╋━━━┫\n";
        String s19 = "┃笔记本        ┃ 本 ┃4.5   ┃\n┗━━━━━━━┻━━┻━━━┛\n\n\n\n\n";

        if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x40,0x1B,0x33,0x00,0x0D,0x0A})) return false;
        if(!mJxit_esc.esc_print_text(s1)) return false;
        if(!mJxit_esc.esc_print_text(s2)) return false;
        if(!mJxit_esc.esc_print_text(s3)) return false;
        if(!mJxit_esc.esc_print_text(s4)) return false;
        if(!mJxit_esc.esc_print_text(s5)) return false;
        if(!mJxit_esc.esc_print_text(s6)) return false;
        if(!mJxit_esc.esc_print_text(s7)) return false;
        if(!mJxit_esc.esc_print_text(s8)) return false;
        if(!mJxit_esc.esc_print_text(s9)) return false;
        if(!mJxit_esc.esc_print_text(s10)) return false;
        if(!mJxit_esc.esc_print_text(s11)) return false;
        if(!mJxit_esc.esc_print_text(s12)) return false;
        if(!mJxit_esc.esc_print_text(s13)) return false;
        if(!mJxit_esc.esc_print_text(s14)) return false;
        if(!mJxit_esc.esc_print_text(s15)) return false;
        if(!mJxit_esc.esc_print_text(s16)) return false;
        if(!mJxit_esc.esc_print_text(s17)) return false;
        if(!mJxit_esc.esc_print_text(s18)) return false;
        if(!mJxit_esc.esc_print_text(s19)) return false;
        if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x40})) return false;
        return true;
    }

    /**
     * printLottery
     */
    private boolean printLottery(){
        /*String pictureStr = "1d 0c 20 cd e6 b7 a8 a3 ba c6 df c0 d6 b2 ca 2d b5 a5 ca bd 20 20 20 20 20 20 20 20 20 20 bb fa ba c5 a3 ba 32 32 30 31 30 30 31 30 0d 0a 20 33 39 " +
                "44 39 2d 38 32 45 36 2d 37 32 35 33 2d 46 41 35 43 2d 45 36 31 39 2f 35 38 31 30 32 33 39 34 2f 43 34 38 39 38 0d 0a 20 20 20 20 20 20 20 20 20 20 20 20 20 20 " +
                "20 20 20 b2 e2 20 20 ca d4 20 20 c6 b1 20 1b 33 32 1b 21 10 1b 45 01 1b 20 00 0d 0a 20 41 2e 1b 24 2e 00 30 32 1b 24 5e 00 30 35 1b 24 8e 00 31 32 1b 24 be " +
                "00 31 35 1b 24 ee 00 32 30 1b 24 1e 01 32 35 1b 24 4e 01 32 38 1b 24 7e 01 28 31 29 0d 0a 20 42 2e 1b 24 2e 00 30 35 1b 24 5e 00 31 35 1b 24 8e 00 32 30 1b " +
                "24 be 00 32 35 1b 24 ee 00 32 36 1b 24 1e 01 32 38 1b 24 4e 01 33 30 1b 24 7e 01 28 31 29 0d 0a 20 43 2e 1b 24 2e 00 30 33 1b 24 5e 00 30 35 1b 24 8e 00 30 " +
                "36 1b 24 be 00 30 37 1b 24 ee 00 31 35 1b 24 1e 01 31 37 1b 24 4e 01 32 35 1b 24 7e 01 28 31 29 0d 0a 20 44 2e 1b 24 2e 00 30 35 1b 24 5e 00 31 30 1b 24 8e " +
                "00 31 35 1b 24 be 00 31 39 1b 24 ee 00 32 31 1b 24 1e 01 32 35 1b 24 4e 01 32 36 1b 24 7e 01 28 31 29 0d 0a 20 45 2e 1b 24 2e 00 30 35 1b 24 5e 00 31 32 1b " +
                "24 8e 00 31 33 1b 24 be 00 31 35 1b 24 ee 00 32 32 1b 24 1e 01 32 35 1b 24 4e 01 32 38 1b 24 7e 01 28 31 29 1b 21 00 1b 45 00 1b 33 00 0d 0a 20 bf aa bd b1 " +
                "c6 da a3 ba 32 30 31 36 31 37 34 20 31 36 2d 30 36 2d 32 32 20 20 20 20 20 20 20 20 20 20 20 20 20 20 a3 a4 1b 21 30 1b 45 01 a3 ba 31 30 1b 21 00 1b 45 00 " +
                "0d 0a 20 cf fa ca db c6 da a3 ba 32 30 31 36 31 37 34 2d 31 20 20 20 20 20 20 20 20 20 20 20 31 36 2d 30 36 2d 32 32 20 31 30 a3 ba 30 39 a3 ba 32 32 0d 0a " +
                "20 b5 d8 d6 b7 a3 ba C9 EE DB DA CA D0 BB A5 B2 CA CD A8 BF C6 BC BC D3 D0 CF DE B9 AB CB BE 0d 0a 1b 33 00 1b 74 01 1d 21 00 1c 2e 1b 24 00 00 83 1b 24 20 " +
                "00 83 1b 24 40 00 83 1b 24 60 00 83 1b 24 80 00 83 1b 24 a0 00 83 1b 24 00 01 83 1b 24 20 01 83 1b 24 40 01 83 1b 24 60 01 83 1b 24 80 01 83 1b 24 a0 01 83 " +
                "1b 24 c0 01 83 1b 24 00 02 83 1b 24 20 02 83 0a 1b 24 00 00 83 1b 24 20 00 83 1b 24 60 00 83 1b 24 80 00 83 1b 24 a0 00 83 1b 24 c0 00 83 1b 24 e0 00 83 1b " +
                "24 00 01 83 1b 24 40 01 83 1b 24 60 01 83 1b 24 a0 01 83 1b 24 c0 01 83 1b 24 e0 01 83 1b 24 00 02 83 1b 24 20 02 83 0a 1b 24 00 00 83 1b 24 40 00 83 1b 24 " +
                "60 00 83 1b 24 80 00 83 1b 24 a0 00 83 1b 24 c0 00 83 1b 24 e0 00 83 1b 24 20 01 83 1b 24 a0 01 83 1b 24 c0 01 83 1b 24 e0 01 83 1b 24 00 02 83 1b 24 20 02 " +
                "83 0a 1b 24 00 00 83 1b 24 20 00 83 1b 24 40 00 83 1b 24 60 00 83 1b 24 80 00 83 1b 24 a0 00 83 1b 24 c0 00 83 1b 24 e0 00 83 1b 24 00 01 83 1b 24 20 01 83 " +
                "1b 24 40 01 83 1b 24 60 01 83 1b 24 80 01 83 1b 24 a0 01 83 1b 24 c0 01 83 1b 24 e0 01 83 1b 24 00 02 83 1b 24 20 02 83 0a 1b 24 00 00 83 1b 24 20 00 83 1b " +
                "24 40 00 83 1b 24 60 00 83 1b 24 80 00 83 1b 24 a0 00 83 1b 24 c0 00 83 1b 24 e0 00 83 1b 24 00 01 83 1b 24 20 01 83 1b 24 c0 01 83 1b 24 e0 01 83 1b 24 00 " +
                "02 83 1b 24 20 02 83 0a 0a 1c 26 1d 21 00 1b 74 00 1b 33 1e 0a 0a 1d 56 42 00";
        */
        String pictureStr = "1B 40 0D 0A cd e6 b7 a8 a3 ba c6 df c0 d6 b2 ca 2d b5 a5 ca bd 0D 0A " +
                "bb fa ba c5 a3 ba 32 32 30 31 30 30 31 30 0d 0a 33 39 " +
                "44 39 2d 38 32 45 36 2d 37 32 35 33 2d 46 41 35 43 2d 45 36 31 39 2f 35 38 31 30 32 33 39 34 2f 43 34 38 39 38 0d 0a " +
                "20 20 20 20 20 20 20 20 20 20 b2 e2 20 20 ca d4 20 20 c6 b1 20 1b 21 10 1b 45 01 1b 20 00 0d 0a " +
                "20 41 2e 20 30 32 20 30 35 20 31 32 20 31 35 20 32 30 20 32 35 20 32 38 " +
                "20 28 31 29 0d 0a 20 42 2e 20 30 35 20 31 35 20 32 30 20 32 35 20 32 36 20 32 38 20 33 30 20 28 31 29 0d 0a 20 43 2e 20 30 33 20 30 35 20 30 " +
                "36 20 30 37 20 31 35 20 31 37 20 32 35 20 28 31 29 0d 0a 20 44 2e 20 30 35 20 31 30 20 31 35 20 31 39 20 32 31 20 32 35 20 32 36 20 28 31 29 0d 0a 20 45 2e " +
                "20 31 33 20 31 35 20 32 32 20 32 35 20 32 38 20 28 31 29 1b 21 00 1b 45 00 1b 33 00 0d 0a bf aa bd b1 " +
                "c6 da a3 ba 32 30 31 36 31 37 34 20 31 36 2d 30 36 2d 32 32 0D 0A a3 a4 1b 21 30 1b 45 01 a3 ba 31 30 1b 21 00 1b 45 00 " +
                "0d 0a cf fa ca db c6 da a3 ba 32 30 31 36 31 37 34 2d 31 20 20 20 20 20 20 20 20 20 20 20 0D 0A 31 36 2d 30 36 2d 32 32 20 31 30 a3 ba 30 39 a3 ba 32 32 0d 0a " +
                "b5 d8 d6 b7 a3 ba C9 EE DB DA CA D0 BB A5 B2 CA CD A8 BF C6 BC BC D3 D0 CF DE B9 AB CB BE 0d 0a 0A 0A 0A 0A";
        String[] data = pictureStr.split(" ");
        byte[] lotteryBytes = new byte[data.length];
        for (int i = 0; i < data.length; i++){
            lotteryBytes[i] = (byte) Integer.parseInt(data[i],16);
        }
        if(!mJxit_esc.esc_reset()) return false;
        if(!mJxit_esc.esc_write_bytes(lotteryBytes)) return false;
        if(!mJxit_esc.esc_write_bytes(new byte[]{0x1B,0x64,0x04})) return false;
        return true;
    }


    /**
     * 打印纸一行最大的字节
     */
    private static final int LINE_BYTE_SIZE = 32;

    private static final int LEFT_LENGTH = 20;

    private static final int RIGHT_LENGTH = 12;

    /**
     * 左侧汉字最多显示几个文字
     */
    private static final int LEFT_TEXT_MAX_LENGTH = 8;

    /**
     * 小票打印菜品的名称，上限调到8个字
     */
    public static final int MEAL_NAME_MAX_LENGTH = 8;

    /**
     * 打印两列
     *
     * @param leftText  左侧文字
     * @param rightText 右侧文字
     * @return
     */
    public static String printTwoData(String leftText, String rightText) {
        StringBuilder sb = new StringBuilder();
        int leftTextLength = getBytesLength(leftText);
        int rightTextLength = getBytesLength(rightText);
        sb.append(leftText);

        // 计算两侧文字中间的空格
        int marginBetweenMiddleAndRight = LINE_BYTE_SIZE - leftTextLength - rightTextLength;

        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }
        sb.append(rightText);
        return sb.toString();
    }

    /**
     * 打印三列
     *
     * @param leftText   左侧文字
     * @param middleText 中间文字
     * @param rightText  右侧文字
     * @return
     */
    public static String printThreeData(String leftText, String middleText, String rightText) {
        StringBuilder sb = new StringBuilder();
        // 左边最多显示 LEFT_TEXT_MAX_LENGTH 个汉字 + 两个点
        if (leftText.length() > LEFT_TEXT_MAX_LENGTH) {
            leftText = leftText.substring(0, LEFT_TEXT_MAX_LENGTH) + "..";
        }
        int leftTextLength = getBytesLength(leftText);
        int middleTextLength = getBytesLength(middleText);
        int rightTextLength = getBytesLength(rightText);

        sb.append(leftText);
        // 计算左侧文字和中间文字的空格长度
        int marginBetweenLeftAndMiddle = LEFT_LENGTH - leftTextLength - middleTextLength / 2;

        for (int i = 0; i < marginBetweenLeftAndMiddle; i++) {
            sb.append(" ");
        }
        sb.append(middleText);

        // 计算右侧文字和中间文字的空格长度
        int marginBetweenMiddleAndRight = RIGHT_LENGTH - middleTextLength / 2 - rightTextLength;

        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }

        // 打印的时候发现，最右边的文字总是偏右一个字符，所以需要删除一个空格
        sb.delete(sb.length() - 1, sb.length()).append(rightText);
        return sb.toString();
    }

    /**
     * 获取数据长度
     *
     * @param msg
     * @return
     */
    private static int getBytesLength(String msg) {
        return msg.getBytes(Charset.forName("GB2312")).length;
    }

    /**
     * 格式化菜品名称，最多显示MEAL_NAME_MAX_LENGTH个数
     *
     * @param name
     * @return
     */
    public static String formatMealName(String name) {
        if (TextUtils.isEmpty(name)) {
            return name;
        }
        if (name.length() > MEAL_NAME_MAX_LENGTH) {
            return name.substring(0, 8) + "..";
        }
        return name;
    }

    /**
     * BroadcastReceiver
     */
    private BroadcastReceiver mActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    mHandler.sendEmptyMessage(CONNECTED);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * onDestroy
     */
   @Override
   public void onDestroy() {
       super.onDestroy();

       mJxit_esc.close();

       if(mActivityReceiver != null) {
           unregisterReceiver(mActivityReceiver);
           mActivityReceiver = null;
       }

       if(mBtAdapter.isEnabled()){
           mBtAdapter.disable();
       }

   }


}
