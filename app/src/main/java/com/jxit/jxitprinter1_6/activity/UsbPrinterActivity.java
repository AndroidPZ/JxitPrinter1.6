package com.jxit.jxitprinter1_6.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jxit.jxitprinter1_6.R;
import com.jxit.jxitprinter1_6.util.Config;
import com.jxit.jxitprinter1_6.util.ConfigMy;
import com.jxit.jxitprinter1_6.util.JxitUSBPrinter;
import com.jxit.jxitprinter1_6.util.NewInfo;
import com.jxit.jxitprinter1_6.util.ToBG18030;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UsbPrinterActivity extends AppCompatActivity {
    /**
     * 小票打印菜品的名称，上限调到8个字
     */
    public static final int MEAL_NAME_MAX_LENGTH = 8;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final int PRINT_OK = 1;
    private static final int PRINT_ERROR = 2;
    private static final int PRINT_STATUS = 3;
    /**
     * 打印纸一行最大的字节
     */
    private static final int LINE_BYTE_SIZE = 48;
    private static final int LEFT_LENGTH = 28;
    private static final int RIGHT_LENGTH = 20;
    /**
     * 左侧汉字最多显示几个文字
     */
    private static final int LEFT_TEXT_MAX_LENGTH = 8;
    public ListView mListView;
    int[] resIdsf = {R.drawable.text_24, R.drawable.settings_24, R.drawable.lottery24, R.drawable.settings_24,
            R.drawable.barcode_24, R.drawable.barcode_2d_24, R.drawable.curve_24, R.drawable.photo_24};
    int[] resIdse = {R.drawable.bullet_grey_1, R.drawable.bullet_grey_1, R.drawable.bullet_grey_1, R.drawable.bullet_grey_1,
            R.drawable.bullet_grey_1, R.drawable.bullet_grey_1, R.drawable.bullet_grey_1, R.drawable.bullet_grey_1};
    private Toolbar mUsbTb;
    private TextView mTbTitleTV;
    private ImageButton mTbIBtn;
    private TextView mLogTV;
    private String[] titles = {"打印文本", "测试", "测试打印", "打印二维码测试", "打印彩票单据", "查询打印机状态", "打印一维条码", "控制命令"};
    private boolean isBackCliecked = false;
    private PendingIntent mPendingIntent;
    private JxitUSBPrinter mJxitUSBPrinter;
    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                showToastMsg("USB设备已拔出！");
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                mJxitUSBPrinter.close();
            }
        }
    };
    private UsbManager usbManager = null;
    private UsbDevice myUsbDevice = null;
    /**
     * Handler
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PRINT_OK:
                    mListView.setEnabled(true);
                    showToastMsg("打印成功！");
                    break;
                case PRINT_ERROR:
                    mListView.setEnabled(true);
                    showToastMsg("打印异常！");
                case PRINT_STATUS:
                    mListView.setEnabled(true);
                    break;
                default:
                    mListView.setEnabled(true);
                    Bundle b = msg.getData();
                    String status = b.getString("status");
                    Log.i("mHandler:status", status);
                    showToastMsg(status);
                    break;
            }
        }
    };
    private ArrayList<NewInfo> newInfoList;
    private NewInfo newInfo;
    private ArrayList<String> strings;
    /**
     * mCmdClickListener
     */
    private AdapterView.OnItemClickListener mCmdClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            if (myUsbDevice != null) {
                if (mJxitUSBPrinter.openDevice(myUsbDevice, mPendingIntent)) {
                    Log.i("cmd", titles[arg2]);
                    printCmd(titles[arg2]);// 点击条目执行相应操作
                } else {
                    mHandler.sendEmptyMessage(PRINT_ERROR);
                }
            } else {
                showToastMsg("请点击右上角USB按钮查找已连接的设备！");
            }

        }
    };

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
        // 左边最多显示 LEFT_TEXT_MAX_LENGTH 个汉字 + 两个点 , 这里LEFT_TEXT_MAX_LENGTH 预设为8
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_printer);

        initViews();

        initUsbMagager();

        registReceiver();
        getUsbDeviceInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUsbReceiver != null) {
            unregisterReceiver(mUsbReceiver);
            mUsbReceiver = null;
        }
        mJxitUSBPrinter.close();
    }

    private void initViews() {
        initToolbar();
        initListView();
        initTextView();
    }

    private void initUsbMagager() {
        Intent intent = new Intent();
        mPendingIntent = (PendingIntent) intent.getParcelableExtra(Intent.EXTRA_INTENT);
        mJxitUSBPrinter = new JxitUSBPrinter(this.getApplicationContext());
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

    }

    private void registReceiver() {
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
    }

    /**
     * initToolbar
     */
    private void initToolbar() {
        mUsbTb = (Toolbar) findViewById(R.id.usb_tb);
        mTbTitleTV = (TextView) mUsbTb.findViewById(R.id.title_tb_tv);
        mTbIBtn = (ImageButton) mUsbTb.findViewById(R.id.right_tb_imgbtn);
        mTbIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsbDeviceInfo();
            }
        });

        setSupportActionBar(mUsbTb);
        if (getSupportActionBar() == null) {
            showToastMsg("不支持ActionBar");
            return;
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mUsbTb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.demo_lv);
        mListView.setAdapter(new ListViewAdapter(resIdsf, titles, resIdse));
        mListView.setOnItemClickListener(mCmdClickListener);
    }

    private void initTextView() {
        mLogTV = (TextView) findViewById(R.id.log_tv);
        mLogTV.setMovementMethod(new ScrollingMovementMethod());
    }

    private void getUsbDeviceInfo() {
        mLogTV.setText("");

        enumeraterDevices();
    }

    /**
     * 枚举设备
     */
    public void enumeraterDevices() {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        StringBuilder sb = new StringBuilder();
        Map<String, UsbDevice> map = new HashMap<>();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if ((device.getVendorId() == 1155) && (device.getProductId() == 30016)) {//0x0483  0x7540
                showToastMsg("vid:" + device.getVendorId() + "  pid:" + device.getProductId());
                myUsbDevice = device;
            } else if ((device.getVendorId() == 2245) && (device.getProductId() == 774)) {
                showToastMsg("vid:" + device.getVendorId() + "  pid:" + device.getProductId());
                myUsbDevice = device;
            }
            sb.append(devicesString(device));
        }

        mLogTV.append(sb.toString());
    }

    /**
     * usb设备的信息
     * device
     */
    public String devicesString(UsbDevice device) {
        StringBuilder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder = new StringBuilder("UsbDevice" +
                    "\nName=" + device.getDeviceName() +
                    "\nVendorId=" + device.getVendorId() +
                    "\nProductId=" + device.getProductId() +
                    "\nmClass=" + device.getClass() +
                    "\nmSubclass=" + device.getDeviceSubclass() +
                    "\nmProtocol=" + device.getDeviceProtocol() +
                    "\nmManufacturerName=" + device.getManufacturerName() +
                    "\nmSerialNumber=" + device.getSerialNumber() +
                    "\n\n");
        } else {
            builder = new StringBuilder("UsbDevice" +
                    "\nName=" + device.getDeviceName() +
                    "\nVendorId=" + device.getVendorId() +
                    "\nProductId=" + device.getProductId() +
                    "\nmClass=" + device.getClass() +
                    "\nmSubclass=" + device.getDeviceSubclass() +
                    "\nmProtocol=" + device.getDeviceProtocol() +
                    "\nmManufacturerName=" +
                    "\nmSerialNumber=" +
                    "\n\n");
        }
        return builder.toString();
    }

    private void printCmd(final String cmd) {
        mListView.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doPrint(cmd);
            }
        }).start();

    }

    private void doPrint(String cmd) {
        switch (cmd) {
            case "打印文本":
                pullParser();
                if (printText()) {
                    mHandler.sendEmptyMessageDelayed(PRINT_OK, 1000);
                } else {
                    mHandler.sendEmptyMessage(PRINT_ERROR);
                }
                break;
            case "测试":
                if (printPatrolResult()) {
                    mHandler.sendEmptyMessageDelayed(PRINT_OK, 1000);
                } else {
                    mHandler.sendEmptyMessage(PRINT_ERROR);
                }
                break;
            case "测试打印":
                if (printPicture()) {
                    mHandler.sendEmptyMessageDelayed(PRINT_OK, 1000);
                } else {
                    mHandler.sendEmptyMessage(PRINT_ERROR);
                }
                break;
            case "打印二维码测试":
                if (printBarcode2d()) {
                    mHandler.sendEmptyMessageDelayed(PRINT_OK, 1000);
                } else {
                    mHandler.sendEmptyMessage(PRINT_ERROR);
                }
                break;
            case "打印彩票单据":
                if (printLottery()) {
                    mHandler.sendEmptyMessageDelayed(PRINT_OK, 1000);
                } else {
                    mHandler.sendEmptyMessage(PRINT_ERROR);
                }
                break;
            case "查询打印机状态":
                String status = getPrinterStatus();
                Log.i("status", status);
                Message message = new Message();
                Bundle b = new Bundle();
                b.putString("status", status);
                message.setData(b);
                mHandler.sendMessage(message);
                break;

            case "打印一维条码":
                if (printBarcode1d()) {
                    mHandler.sendEmptyMessageDelayed(PRINT_OK, 1000);
                } else {
                    mHandler.sendEmptyMessage(PRINT_ERROR);
                }
                break;
            case "控制命令":
                if (printControlCommand()) {
                    mHandler.sendEmptyMessageDelayed(PRINT_OK, 1000);
                } else {
                    mHandler.sendEmptyMessage(PRINT_ERROR);
                }
                break;
            default:
                showToastMsg("啥也没有！");
                break;
        }
    }

    /**
     * 测试打印
     *
     * @return
     */
    private boolean printPicture() {
        //转换过的
//        String str = "1b 40 1b 61 01 cd e6 b7 a8 3a cb ab c9 ab c7 f2 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 c1 f7 cb ae ba c5 3a 31 31 0a bb fa ba c5 3a 31 38 30 30 30 30 30 31 20 20 20 20 20 20 20 20 20 31 39 32 41 2d 42 34 33 30 2d 37 36 43 33 2d 36 35 39 37 2d 32 44 46 32 0a cf fa ca db c6 da 3a 32 30 31 38 30 35 35 20 20 20 20 20 20 20 38 34 36 37 38 20 20 20 20 20 20 20 d3 d0 d0 a7 c6 da 3a 32 30 31 38 30 35 35 0a cf fa ca db ca b1 bc e4 3a 32 30 31 38 2d 30 35 2d 31 35 20 31 34 3a 34 38 3a 30 33 20 20 20 20 20 20 20 20 20 20 bd f0 b6 ee 3a 31 30 d4 aa 0a 1b 40 1b 61 01 1b 45 01 0a 30 36 20 20 20 30 38 20 20 20 31 31 20 20 20 32 30 20 20 20 32 34 20 20 20 32 36 20 2d 20 30 31 20 20 20 20 28 31 29 0a 30 37 20 20 20 30 39 20 20 20 31 32 20 20 20 32 31 20 20 20 32 35 20 20 20 32 38 20 2d 20 30 31 20 20 20 20 28 31 29 0a 30 34 20 20 20 30 36 20 20 20 30 39 20 20 20 31 38 20 20 20 32 32 20 20 20 32 34 20 2d 20 30 31 20 20 20 20 28 31 29 0a 30 38 20 20 20 31 30 20 20 20 31 33 20 20 20 32 33 20 20 20 32 36 20 20 20 32 39 20 2d 20 30 31 20 20 20 20 28 31 29 0a 30 31 20 20 20 30 33 20 20 20 30 37 20 20 20 31 35 20 20 20 31 38 20 20 20 32 37 20 2d 20 30 31 20 20 20 20 28 31 29 0a 0a 1b 40 1b 61 01 bf aa bd b1 ca b1 bc e4 3a 32 30 31 38 2d 30 35 2d 31 35 20 20 20 20 20 20 20 20 20 20 20 20 20 20 b8 bd bc d3 c2 eb 3a 34 34 38 38 33 31 0a b5 d8 d6 b7 3a b1 b1 be a9 ca d0 ba a3 b5 ed c7 f8 b8 b7 b3 c9 c2 b7 31 30 30 31 ba c5 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 0a 1b 40 1b 33 00 1b 74 01 1d 21 00 1c 2e 1b 61 01 1b 24 19 00 83 1b 24 32 00 83 1b 24 64 00 83 1b 24 7d 00 83 1b 24 96 00 83 1b 24 c8 00 83 1b 24 19 01 83 1b 24 4d 01 83 1b 24 7d 01 83 1b 24 c8 01 83 1b 24 e1 01 83 1b 24 fa 01 83 0a 1b 24 00 00 83 1b 24 19 00 83 1b 24 4d 00 83 1b 24 64 00 83 1b 24 af 00 83 1b 24 c8 00 83 1b 24 fa 00 83 1b 24 19 01 83 1b 24 4d 01 83 1b 24 7d 01 83 1b 24 96 01 83 1b 24 af 01 83 1b 24 fa 01 83 0a 1b 24 19 00 83 1b 24 32 00 83 1b 24 64 00 83 1b 24 96 00 83 1b 24 af 00 83 1b 24 e1 00 83 1b 24 fa 00 83 1b 24 19 01 83 1b 24 64 01 83 1b 24 fa 01 83 0a 1b 24 19 00 83 1b 24 32 00 83 1b 24 7d 00 83 1b 24 e1 00 83 1b 24 7d 01 83 1b 24 96 01 83 1b 24 c8 01 83 1b 24 fa 01 83 0a 1b 24 00 00 83 1b 24 19 00 83 1b 24 32 00 83 1b 24 4d 00 83 1b 24 64 00 83 1b 24 7d 00 83 1b 24 96 00 83 1b 24 af 00 83 1b 24 c8 00 83 1b 24 e1 00 83 1b 24 fa 00 83 1b 24 19 01 83 1b 24 32 01 83 1b 24 4d 01 83 1b 24 64 01 83 1b 24 7d 01 83 1b 24 96 01 83 1b 24 af 01 83 1b 24 c8 01 83 1b 24 e1 01 83 1b 24 fa 01 83 0a ";
//        String str = Config.SiChuanQR;
        String str = Config.NewCoed;

        String[] data = str.toString().split(" ");
        byte[] lotteryBytes = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            lotteryBytes[i] = (byte) Integer.parseInt(data[i], 16);
        }
        mJxitUSBPrinter.esc_write_bytes(lotteryBytes);
        return mJxitUSBPrinter.esc_write_bytes(new byte[]{0x1D, 0x56, 0x42, 0x00, 0x1B, 0x40});
    }

    /**
     * 解析xml数据
     */
    private void pullParser() {
        try {
            InputStream mAssets = getAssets().open("templet.xml");//得到assets目录下的文件
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();//
            XmlPullParser xmlPullParser = factory.newPullParser();//构建解析对象
            xmlPullParser.setInput(mAssets, "utf-8");//开始解析---传流
//            xmlPullParser.setInput(new StringReader(str));//传字符串
            int eventType = xmlPullParser.getEventType();//当前的解析事件--就是当前解析的节点
            //判断是否解析完毕
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();//获取节点名称
                switch (eventType) {
                    //开始解析文档
                    case XmlPullParser.START_DOCUMENT:
//                        persons = new ArrayList<Person>();
                        break;
                    //开始解析某个节点---
                    case XmlPullParser.START_TAG: {
                        /**
                         * 解释下这里为什么要判断。点进去nextText方法可以看到
                         XmlPullParser.START_DOCUMENT=0（开始解析文档）,
                         XmlPullParser.EDN_DOCUMENT=1（结束解析文档）,
                         XmlPullParser.START_TAG=2(开始解析标签),
                         XmlPullParser.END_TAG=3(结束解析标签)；
                         XmlPullParser.TEXT=4(解析文本时用的);
                         *  意思就是说：如果当前事件是starttag那么如果下一个元素是文本那么元素内容就会返回
                         *   如果下一个事件是endtag，则返回空字符串，否则将抛出异常。成功地调用这个函数之后，
                         *   解析器将被定位在endtag上。
                         *   不判断必定报异常，因为很多节点下面都是子元素，并不是文本内容。也不是endTag
                         */
                        //<news>
                        if ("lotterys".equals(nodeName)) {
                            newInfoList = new ArrayList<NewInfo>();
                        } else if ("lottery".equals(nodeName)) {
                            newInfoList = new ArrayList<NewInfo>();
                        } else if ("content".equals(nodeName)) {
                            newInfo = new NewInfo();
                        } else if ("styles".equals(nodeName)) {
                            strings = new ArrayList<>();
                        } else if ("style".equals(nodeName)) {
                            strings.add(xmlPullParser.nextText());
                        } else if ("text".equals(nodeName)) {
                            newInfo.setText(xmlPullParser.nextText());//讲detaile的内容放进去
                        }

                    }
                    break;
                    //完成某个节点的解析
                    case XmlPullParser.END_TAG: {
                        if ("content".equals(nodeName)) {
                            newInfoList.add(newInfo);
                            Log.i("XPZ", "文件的" + newInfo.toString());
                        } else if ("lottery".equals(nodeName)) {
                            Log.i("XPZ", "文件的集合" + newInfoList.toString());
                        } else if ("styles".equals(nodeName)) {
                            newInfo.setStyle(strings);
                        }
                    }
                    break;
                    default:
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * printText
     */
    private boolean printText() {


        Log.i("printText", 1 + " true");
        mJxitUSBPrinter.esc_reset();
        for (NewInfo info : newInfoList) {
            for (String style : info.getStyle()) {
                String[] data = style.split(" ");
                byte[] lotteryBytes = new byte[data.length];
                for (int i = 0; i < data.length; i++) {
                    lotteryBytes[i] = (byte) Integer.parseInt(data[i], 16);
                }
                mJxitUSBPrinter.esc_write_bytes(lotteryBytes);
            }
            String text = info.getText();
            String code = text.replaceAll("/n", "\n");
            mJxitUSBPrinter.esc_print_text(code);
            mJxitUSBPrinter.esc_reset();
        }

        Log.i("printText", 1008611 + " true");
        // 打印点状矩阵
        String[] data = Config.my.split(" ");
        byte[] lotteryBytes = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            lotteryBytes[i] = (byte) Integer.parseInt(data[i], 16);
        }

        return mJxitUSBPrinter.esc_write_bytes(lotteryBytes);
    }

    /**
     * 测试
     * printPatrolResult
     */
    private boolean printPatrolResult() {
        if (!mJxitUSBPrinter.esc_reset()) {
            return false;
        }
        /*
         * 使用指令和BG18030 码打印票面
         * */

        StringBuffer stringBuffer = new StringBuffer(ConfigMy.END);
        for (int i = 0; i < Config.CODE_ARR_STR.length; i++) {

            if (i >= 4 && i <= 8) {
                if (i == 4) {
                    stringBuffer.append(ConfigMy.END).append(ConfigMy.CENTET_BOLD).append(ToBG18030.strToGB18030(ConfigMy.CODE_ARR_STR[i])).append(ConfigMy.END);
                } else {
                    stringBuffer.append(ConfigMy.CENTET_BOLD).append(ToBG18030.strToGB18030(ConfigMy.CODE_ARR_STR[i])).append(ConfigMy.END);
                }
            } else {
                if (i == 9) {
                    stringBuffer.append(ConfigMy.END).append(ConfigMy.UNBOLD).append(ToBG18030.strToGB18030(ConfigMy.CODE_ARR_STR[i])).append(ConfigMy.END);
                } else {
                    stringBuffer.append(ConfigMy.CENTER).append(ToBG18030.strToGB18030(ConfigMy.CODE_ARR_STR[i])).append(ConfigMy.END);
                }
            }
        }
        stringBuffer.append(ConfigMy.END)
                .append(ConfigMy.PRINT_DIAN)
                .append(ConfigMy.CENTER)
                .append(ToBG18030.hexStrToBG18030("2934-B427-3147-0C50-E7C9"))
                .append(ConfigMy.PRINT_ENTER)
                .append(ConfigMy.RESET);

        String[] data = stringBuffer.toString().split(" ");
        byte[] lotteryBytes = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            lotteryBytes[i] = (byte) Integer.parseInt(data[i], 16);
        }
        return mJxitUSBPrinter.esc_write_bytes(lotteryBytes);
    }

    /**
     * printLottery
     */
    private boolean printLottery() {
        String pictureStr = "0D 0A 20 cd e6 b7 a8 a3 ba c6 df c0 d6 b2 ca 2d b5 a5 ca bd 20 20 20 20 20 20 20 20 20 20 bb fa ba c5 a3 ba 32 32 30 31 30 30 31 30 0d 0a 20 33 39 " +
                "44 39 2d 38 32 45 36 2d 37 32 35 33 2d 46 41 35 43 2d 45 36 31 39 2f 35 38 31 30 32 33 39 34 2f 43 34 38 39 38 0d 0a 20 20 20 20 20 20 20 20 20 20 20 20 20 20 " +
                "20 20 20 b2 e2 20 20 ca d4 20 20 c6 b1 20 1b 33 32 1b 21 10 1b 45 01 1b 20 00 0d 0a 20 41 2e 1b 24 2e 00 30 32 1b 24 5e 00 30 35 1b 24 8e 00 31 32 1b 24 be " +
                "00 31 35 1b 24 ee 00 32 30 1b 24 1e 01 32 35 1b 24 4e 01 32 38 1b 24 7e 01 28 31 29 0d 0a 20 42 2e 1b 24 2e 00 30 35 1b 24 5e 00 31 35 1b 24 8e 00 32 30 1b " +
                "24 be 00 32 35 1b 24 ee 00 32 36 1b 24 1e 01 32 38 1b 24 4e 01 33 30 1b 24 7e 01 28 31 29 0d 0a 20 43 2e 1b 24 2e 00 30 33 1b 24 5e 00 30 35 1b 24 8e 00 30 " +
                "36 1b 24 be 00 30 37 1b 24 ee 00 31 35 1b 24 1e 01 31 37 1b 24 4e 01 32 35 1b 24 7e 01 28 31 29 0d 0a 20 44 2e 1b 24 2e 00 30 35 1b 24 5e 00 31 30 1b 24 8e " +
                "00 31 35 1b 24 be 00 31 39 1b 24 ee 00 32 31 1b 24 1e 01 32 35 1b 24 4e 01 32 36 1b 24 7e 01 28 31 29 0d 0a 20 45 2e 1b 24 2e 00 30 35 1b 24 5e 00 31 32 1b " +
                "24 8e 00 31 33 1b 24 be 00 31 35 1b 24 ee 00 32 32 1b 24 1e 01 32 35 1b 24 4e 01 32 38 1b 24 7e 01 28 31 29 1b 21 00 1b 45 00 1b 33 00 0d 0a 20 bf aa bd b1 " +
                "c6 da a3 ba 32 30 31 36 31 37 34 20 31 36 2d 30 36 2d 32 32 20 20 20 20 20 20 20 20 20 20 20 20 20 20 a3 a4 1b 21 30 1b 45 01 a3 ba 31 30 1b 21 00 1b 45 00 " +
                "0d 0a 20 cf fa ca db c6 da a3 ba 32 30 31 36 31 37 34 2d 31 20 20 20 20 20 20 20 20 20 20 20 31 36 2d 30 36 2d 32 32 20 31 30 a3 ba 30 39 a3 ba 32 32 0d 0a " +
                "20 b5 d8 d6 b7 a3 ba C9 EE DB DA CA D0 BB A5 B2 CA CD A8 BF C6 BC BC D3 D0 CF DE B9 AB CB BE " +
                "0d " +//打印并回车
                "0a " +//打印并换行
                "1b 33 00 " +
                "1b 74 01 1d 21 00 " +//设置字符编码,字符大小
                "1c 2e 1b 61 01" +//取消文字模式
                "1b 24 00 00 83 1b 24 20 " +
                "00 83 1b 24 40 00 83 1b 24 60 00 83 1b 24 80 00 83 1b 24 a0 00 83 1b 24 00 01 83 1b 24 20 01 83 1b 24 40 01 83 1b 24 60 01 83 1b 24 80 01 83 1b 24 a0 01 83 " +
                "1b 24 c0 01 83 1b 24 00 02 83 1b 24 20 02 83 0a 1b 24 00 00 83 1b 24 20 00 83 1b 24 60 00 83 1b 24 80 00 83 1b 24 a0 00 83 1b 24 c0 00 83 1b 24 e0 00 83 1b " +
                "24 00 01 83 1b 24 40 01 83 1b 24 60 01 83 1b 24 a0 01 83 1b 24 c0 01 83 1b 24 e0 01 83 1b 24 00 02 83 1b 24 20 02 83 0a 1b 24 00 00 83 1b 24 40 00 83 1b 24 " +
                "60 00 83 1b 24 80 00 83 1b 24 a0 00 83 1b 24 c0 00 83 1b 24 e0 00 83 1b 24 20 01 83 1b 24 a0 01 83 1b 24 c0 01 83 1b 24 e0 01 83 1b 24 00 02 83 1b 24 20 02 " +
                "83 0a 1b 24 00 00 83 1b 24 20 00 83 1b 24 40 00 83 1b 24 60 00 83 1b 24 80 00 83 1b 24 a0 00 83 1b 24 c0 00 83 1b 24 e0 00 83 1b 24 00 01 83 1b 24 20 01 83 " +
                "1b 24 40 01 83 1b 24 60 01 83 1b 24 80 01 83 1b 24 a0 01 83 1b 24 c0 01 83 1b 24 e0 01 83 1b 24 00 02 83 1b 24 20 02 83 0a 1b 24 00 00 83 1b 24 20 00 83 1b " +
                "24 40 00 83 1b 24 60 00 83 1b 24 80 00 83 1b 24 a0 00 83 1b 24 c0 00 83 1b 24 e0 00 83 1b 24 00 01 83 1b 24 20 01 83 1b 24 c0 01 83 1b 24 e0 01 83 1b 24 00 " +
                "02 83 1b 24 20 02 83 " +
                "0a 0a " +//换行
                "1c 26 1d 21 00 1b 74 00 1b 33 1e 0a";//设置了汉子模式,汉子大小,选择字符代码表,设置行间距,打印并换行


        String[] data = pictureStr.split(" ");
        byte[] lotteryBytes = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            lotteryBytes[i] = (byte) Integer.parseInt(data[i], 16);
        }
        if (!mJxitUSBPrinter.esc_reset()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_align(1)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_write_bytes(lotteryBytes)) {
            return false;
        }
//        if(!mJxitUSBPrinter.esc_reset()) {return false;}
        return mJxitUSBPrinter.esc_write_bytes(new byte[]{0x1D, 0x56, 0x42, 0x00});
    }


    /**
     * printBarcode2d
     */
    private boolean printBarcode2d() {
        if (!mJxitUSBPrinter.esc_reset()) {
            return false;
        }
        /*
         * 使用指令和BG18030 码打印票面
         * */

        StringBuffer stringBuffer = new StringBuffer(ConfigMy.END);
        for (int i = 0; i < Config.CODE_ARR_STR.length; i++) {

            if (i >= 4 && i <= 8) {
                if (i == 4) {
                    stringBuffer.append(ConfigMy.END).append(ConfigMy.CENTET_BOLD).append(ToBG18030.strToGB18030(ConfigMy.CODE_ARR_STR[i])).append(ConfigMy.END);
                } else {
                    stringBuffer.append(ConfigMy.CENTET_BOLD).append(ToBG18030.strToGB18030(ConfigMy.CODE_ARR_STR[i])).append(ConfigMy.END);
                }
            } else {
                if (i == 9) {
                    stringBuffer.append(ConfigMy.END).append(ConfigMy.UNBOLD).append(ToBG18030.strToGB18030(ConfigMy.CODE_ARR_STR[i])).append(ConfigMy.END);
                } else {
                    if (i == 10) {
                        stringBuffer.append(ConfigMy.END)
                                .append("1f 07 48 01 07 ")//在开启页模式之前添加,定制版本几x几大小(不够自动填充)
                                .append("1b 4c ") //选择页模式
                                .append("1b 57 00 00 00 00 60 01 c0 00 ") //在页模式下设置打印区域
                                .append("1b 54 00 ")////在页模式下选择打印方向
                                .append("1d 24 0a 00 ")
                                .append("1b 24 18 00 ")
                                .append(ConfigMy.END)
                                .append(ToBG18030.strToGB18030(ConfigMy.CODE_ARR_STR[i]));
                    } else {
                        stringBuffer.append(ConfigMy.CENTER).append(ToBG18030.strToGB18030(ConfigMy.CODE_ARR_STR[i])).append(ConfigMy.END);
                    }

                }
            }
        }
        stringBuffer.append(ConfigMy.END)
                .append("1b 57 68 01 00 00 3c 02 c0 00 " + //68 -> 104 准备打印二维码
                        "1d 24 0a 00 " +                //页模式下设置绝对垂直打印位置
                        "1d 28 6b 03 00 31 43 04 " +    //6b之后的 四位
                        "1d 28 6b 03 00 31 45 01 " +    //6b之后的 四位
                        "1d 28 6b 17 00 31 50 30 " +    //17 00 这个是二维码字符的长度 额外要加上 3纠错的大小
                        "70 6a 72 77 75 6c 71 6d 77 71 6e 75 76 6f 77 71 77 68 6c 6e " +
                        "1d 28 6b 03 00 31 51 30 ")
                .append("0c ");

        stringBuffer.append(ConfigMy.PRINT_ENTER)
                .append(ConfigMy.RESET);
        String[] data = stringBuffer.toString().split(" ");
        byte[] lotteryBytes = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            lotteryBytes[i] = (byte) Integer.parseInt(data[i], 16);
        }
        return mJxitUSBPrinter.esc_write_bytes(lotteryBytes);
    }


    /**
     * printBarcode1d
     */
    private boolean printBarcode1d() {
        if (!mJxitUSBPrinter.esc_print_enter()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_formfeed()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_barcode_1d(2, 1, 2, 60, 69, "RSF0-A0IV-BFFE-3QIB-6XU7")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_enter()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_formfeed()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_formfeed_row(2)) {
            return false;
        }
        // 数组中是 切纸的编码
//        if (!mJxitUSBPrinter.esc_write_bytes(new byte[]{0x1D, 0x56, 0x42, 0x00})) {
//            return false;
//        }
        return mJxitUSBPrinter.esc_select_cutting_mode(0);
    }


    /**
     * printControlCommand
     */
    private boolean printControlCommand() {
        if (!mJxitUSBPrinter.esc_reset()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_enter()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_formfeed()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("控制命令效果展示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("打印并回车效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_enter()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("打印并走纸一行效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_formfeed()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("打印并走纸100个纵向移动单位效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_formfeed(100)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("打印并走纸10个行高效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_formfeed_row(10)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("横向跳格效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_next_horizontal_tab()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("1")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_next_horizontal_tab()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("2")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_next_horizontal_tab()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("3")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_next_horizontal_tab()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("4\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_absolute_print_position(0, 0)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("绝对位置0、0效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_absolute_print_position(50, 50)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("绝对位置50、50效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_absolute_print_position(150, 150)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("绝对位置150、150效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_reset()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_default_line_height()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("设置默认行高效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_line_height(0)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("设置行高为0效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_line_height(50)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("设置行高为50效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_line_height(150)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("设置行高为150效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_reset()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_right_spacing(0)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("设置右边距为0效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_right_spacing(50)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("设置右边距为50效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_right_spacing(100)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("设置右边距为100效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_right_spacing(150)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("设置右边距为150效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_reset()) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_left_margin(50, 0)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("设置左边距为50、0效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_left_margin(50, 1)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("设置左边距为50、1效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_left_margin(100, 1)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("设置左边距为100、1效果演示：\n")) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_left_margin(0, 0)) {
            return false;
        }
        if (!mJxitUSBPrinter.esc_print_text("设置左边距为0效果演示：\n\n\n\n\n")) {
            return false;
        }
//        if(!mJxitUSBPrinter.esc_reset()) {return false;}
        return mJxitUSBPrinter.esc_select_cutting_mode(0);
    }

    private void showToastMsg(String msg) {
        Toast.makeText(UsbPrinterActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * getPrinterStatus
     */
    private String getPrinterStatus() {
        int status = mJxitUSBPrinter.getPrinterStatus();
        Log.i("status", String.valueOf(status));
        String result;
        switch (status) {
            case 0:
                result = "打印机正常！";
                break;
            case 1:
                result = "打印机未连接或未上电！";
                break;
            case 2:
                result = "打印机和调用库不匹配！";
                break;
            case 3:
                result = "打印头打开！";
                break;
            case 4:
                result = "切刀未复位！";
                break;
            case 5:
                result = "打印头过热！";
                break;
            case 6:
                result = "黑标错误！";
                break;
            case 7:
                result = "纸尽！";
                break;
            case 8:
                result = "纸将尽！";
                break;
            case -1:
                result = "打印机未连接或未上电！";
                break;
            default:
                result = "打印机未连接或未上电！";
                break;
        }
        return result;
    }

    /**
     * ListViewAdapter
     */
    public class ListViewAdapter extends BaseAdapter {
        View[] itemViews;

        ListViewAdapter(int[] itemImageResf, String[] itemTexts, int[] itemImageRese) {
            itemViews = new View[itemImageResf.length];
            for (int i = 0; i < itemViews.length; ++i) {
                itemViews[i] = makeItemView(itemImageResf[i], itemTexts[i], itemImageRese[i]);
            }
        }

        private View makeItemView(int resIdf, String strText, int resIde) {
            final String tit = strText;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.listview_item, null);
            ImageView imagef = (ImageView) itemView.findViewById(R.id.lvitem_image_first);
            imagef.setImageResource(resIdf);
            TextView title = (TextView) itemView.findViewById(R.id.lvitem_textview);
            title.setText(strText);
            final ImageView imagee = (ImageView) itemView.findViewById(R.id.lvitem_image_end);
            imagee.setImageResource(resIde);
            return itemView;
        }

        @Override
        public int getCount() {
            return itemViews.length;
        }

        @Override
        public View getItem(int position) {
            return itemViews[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if (convertView == null) {
                return itemViews[position];
            }
            return convertView;
        }
    }
}
