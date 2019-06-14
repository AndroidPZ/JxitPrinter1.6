package com.jxit.jxitprinter1_6.util;


import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by xu on 2018/01/09.
 */

public class JxitUSBPrinter {
    public UsbDeviceConnection myDeviceConnection = null;
    private Context mContext;
    private UsbManager myUsbManager = null;
    private UsbDevice myUsbDevice = null;
    private UsbInterface usbInterface = null;
    private UsbEndpoint epBulkOut = null;
    private UsbEndpoint epBulkIn = null;
    private UsbEndpoint epControl = null;
    private UsbEndpoint epIntEndpointOut = null;
    private UsbEndpoint epIntEndpointIn = null;

    private JxitUSBPrinter() {
    }

    public JxitUSBPrinter(Context context) {
        this.mContext = context;
        this.myUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ACTION_USB_PERMISSION);
//        mContext.registerReceiver(mUsbReceiver, filter);
    }


    /**
     * 1、连接设备
     */
    public boolean openDevice(UsbDevice usbDevice, PendingIntent mPendingIntent) {
        this.myUsbDevice = usbDevice;
        if (myDeviceConnection != null) {
            myDeviceConnection.close();
            myDeviceConnection = null;
        }

        getDeviceInterface();
        assignEndpoint();

        if (!myUsbManager.hasPermission(myUsbDevice)) {
            myUsbManager.requestPermission(myUsbDevice, mPendingIntent);
        } else {
            UsbDeviceConnection conn = null;
            conn = myUsbManager.openDevice(myUsbDevice);
            if (conn == null) {
                return false;
            }
            //打开设备
            if (!conn.claimInterface(usbInterface, true)) {
                conn.close();
                return false;
            }
            myDeviceConnection = conn;
            Log.i("connenting:", "connect OK!");
            return true;
        }
        return false;
    }

    /**
     * 获取设备的接口
     */
    private boolean getDeviceInterface() {
        if (myUsbDevice == null) {
            return false;
        }

        usbInterface = myUsbDevice.getInterface(0);
        return true;
    }

    /**
     * 分配端点，IN | OUT，即输入输出；可以通过判断
     */
    private boolean assignEndpoint() {
        if (usbInterface == null) {
            return false;
        }

        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint ep = usbInterface.getEndpoint(i);
            switch (ep.getType()) {
                case UsbConstants.USB_ENDPOINT_XFER_BULK://块
                    if (UsbConstants.USB_DIR_OUT == ep.getDirection()) {//输出
                        epBulkOut = ep;
                    } else if (UsbConstants.USB_DIR_IN == ep.getDirection()) {//输入
                        epBulkIn = ep;
                    }
                    break;
                case UsbConstants.USB_ENDPOINT_XFER_CONTROL://控制
                    epControl = ep;
                    break;
                case UsbConstants.USB_ENDPOINT_XFER_INT://中断
                    if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {//输出
                        epIntEndpointOut = ep;
                    }
                    if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
                        epIntEndpointIn = ep;
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    /**
     * 2、断开打印机。
     * 与当前连接的打印机断开连接。
     */
    public boolean close() {
        if (myDeviceConnection != null) {
            myDeviceConnection.close();
            myDeviceConnection = null;
        }
        return true;
    }

    /**
     * 3、打印文本。
     *
     * @param text text表示所要打印的文本内容。
     */
    public boolean esc_print_text(String text) {
        return sendMessageToPoint(text);
    }

    public boolean sendMessageToPoint(String text) {
        if (myDeviceConnection == null) {
            return false;
        }
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] buffer = null;
        try {
            buffer = text.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return myDeviceConnection.bulkTransfer(epBulkOut, buffer, buffer != null ? buffer.length : 0, 3000) >= 0;
    }

    /**
     * 4、发送字节流。
     *
     * @param bytes bytes表示所要发送的字节流。
     */
    public boolean esc_write_bytes(byte[] bytes) {
        return sendMessageToPoint(bytes);
    }

    public boolean sendMessageToPoint(byte[] buffer) {
        if (myDeviceConnection == null) {
            return false;
        }
        if (buffer.length > 1024) {
            int len = ((buffer.length - 1) / 1024 + 1);
            for (int i = 0; i < len; i++) {
                if (i < len - 1) {
                    byte[] resultBytes = new byte[1024];
                    System.arraycopy(buffer, i * 1024, resultBytes, 0, 1024);
                    int result = myDeviceConnection.bulkTransfer(epBulkOut, resultBytes, resultBytes.length, 1000);
                    if (result < 0) {
                        return false;
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    byte[] resultBytes = new byte[buffer.length % 1024];
                    System.arraycopy(buffer, i * 1024, resultBytes, 0, buffer.length % 1024);
                    int result = myDeviceConnection.bulkTransfer(epBulkOut, resultBytes, resultBytes.length, 1000);
                    if (result < 0) {
                        return false;
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            int result = myDeviceConnection.bulkTransfer(epBulkOut, buffer, buffer.length, 3000);
            return result >= 0;
        }
        return true;
    }

    /**
     * 5、发送byte[]的一部分到打印机打印
     */
    public boolean esc_write_bytes(byte[] bytes, int offset, int len) {
        return sendMessageToPoint(bytes, offset, len);
    }

    public boolean sendMessageToPoint(byte[] buffer, int offset, int len) {
        if (myDeviceConnection == null) {
            return false;
        }
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return myDeviceConnection.bulkTransfer(epBulkOut, buffer, offset, len, 3000) >= 0;
        } else {
            byte[] resultBytes = new byte[len];
            System.arraycopy(buffer, offset, resultBytes, 0, len);
            return myDeviceConnection.bulkTransfer(epBulkOut, resultBytes, len, 3000) >= 0;
        }
    }

    /**
     * my 设置字符编码,,字符大小,取消文字模式(为打印光感点状区做准备)
     */
    public boolean esc_my1() {//"1b 33 00"设置行间距"1B 74 01 1D 21 00 " //设置字符编码,字符大小"1C 2E " +//取消文字模式
        byte[] mode = {0x1B, 0x33, 0x00, 0x1B, 0x74, 0x01, 0x1D, 0x21, 0x00, 0x1C, 0x2E};
        return sendMessageToPoint(mode);
//        esc_character_code_page(1);
//        esc_character_size(0);
//        return esc_chinese_mode(false);

    }

    /**
     * my 打印黑点
     * 0 <= nL <=255
     * 0 <= nH <=255
     */
    public boolean esc_my2(int nl, int nh) {//1b  24 00 00 83 打印黑点
//        byte b = (byte) Integer.parseInt(Integer.toHexString(nl), 16);
        byte b = (byte) Integer.parseInt("00", 16);
        byte b2 = (byte) Integer.parseInt("00", 16);
        byte b3 = (byte) Integer.parseInt("83", 16);
        byte[] mode = {0x1B, 0x24, b, b2, b3};
//        if (nl < 0) mode[2] = 0x00;//位置 = 0;
//        else if (nl>255) mode[2] = (byte) 255;//位置 = 0;
//        else
//        mode[2] = b;//位置

//        if (nh < 0) mode[3] = 0x00;//位置 = 0;
//        else if (nh>255) mode[3] = (byte) 255;//位置 = 0
//        else
//        mode[3] = b2;//位置

//        mode[4] = b3;
        return sendMessageToPoint(mode);
    }

    /**
     * 35、设定汉字模式。
     *
     * @param b 当b为true时选择汉字模式，当b为false时取消汉字模式。
     */
    public boolean esc_chinese_mode(boolean b) {
        byte[] esc_chinese_mode = new byte[2];
        esc_chinese_mode[0] = 0x1C;
        if (!b) esc_chinese_mode[1] = 0x2E;
        else esc_chinese_mode[1] = 0x26;
        return sendMessageToPoint(esc_chinese_mode);
    }

    /**
     * 28、选择字符代码页。
     *
     * @param n 当n=1时选择Page 1 Katakana，当n=2时选择Page 2 Multilingual(Latin-1) [CP850]，当n=3时选择Page 3 Portuguese [CP860]，
     *          当n=4时选择Page 4 Canadian-French [CP863]，当n=5时选择Page 5 Nordic [CP865]，当n=6时选择Page 6 Slavic(Latin-2) [CP852]，
     *          当n=7时选择Page 7 Turkish [CP857]，当n=8时选择Page 8 Greek [CP737]，当n=9时选择Page 9 Russian(Cyrillic) [CP866]，
     *          当n=10时选择Page 10 Hebrew [CP862]，当n=11时选择Page 11 Baltic [CP775]，当n=12时选择Page 12 Polish，
     *          当n=13时选择Page 13 Latin-9 [ISO8859-15]，当n=14时选择Page 14 Latin1[Win1252]，当n=15时选择Page 15 Multilingual Latin I + Euro[CP858]，
     *          当n=16时选择Page 16 Russian(Cyrillic)[CP855]，当n=17时选择Page 17 Russian(Cyrillic)[Win1251]，当n=18时选择Page 18 Central Europe[Win1250]，
     *          当n=19时选择Page 19 Greek[Win1253]，当n=20时选择Page 20 Turkish[Win1254]，当n=21时选择Page 21 Hebrew[Win1255]，
     *          当n=22时选择Page 22 Vietnam[Win1258]，当n=23时选择Page 23 Baltic[Win1257]，当n=24时选择Page 24 Azerbaijani，
     *          当n=30时选择Thai[CP874]Thai[CP874]，当n=40时选择Page 25 Arabic [CP720]，当n=41时选择Page 26 Arabic [Win 1256]，
     *          当n=42时选择Page 27 Arabic (Farsi)，当n=43时选择Page 28 Arabic presentation forms B，当n=50时选择Page 29 Page 25 Hindi_Devanagari，
     *          当n=252时选择Page 30 Japanese[CP932]，当n=253时选择Page 31 Korean[CP949]，当n=254时选择Page 32 Traditional Chinese[CP950]，
     *          当n=255时选择Page 33 Simplified Chinese[CP936]。
     *          当n取其他值时选择else if(n == 252) esc_character_code_page[2] = 0x01。
     */
    public boolean esc_character_code_page(int n) {
        byte[] esc_character_code_page = new byte[3];
        esc_character_code_page[0] = 0x1B;
        esc_character_code_page[1] = 0x74;
        if (n == 1) esc_character_code_page[2] = 1;
        else if (n == 2) esc_character_code_page[2] = 2;
        else if (n == 3) esc_character_code_page[2] = 3;
        else if (n == 4) esc_character_code_page[2] = 4;
        else if (n == 5) esc_character_code_page[2] = 5;
        else if (n == 6) esc_character_code_page[2] = 6;
        else if (n == 7) esc_character_code_page[2] = 7;
        else if (n == 8) esc_character_code_page[2] = 8;
        else if (n == 9) esc_character_code_page[2] = 9;
        else if (n == 10) esc_character_code_page[2] = 10;
        else if (n == 11) esc_character_code_page[2] = 11;
        else if (n == 12) esc_character_code_page[2] = 12;
        else if (n == 13) esc_character_code_page[2] = 13;
        else if (n == 14) esc_character_code_page[2] = 14;
        else if (n == 15) esc_character_code_page[2] = 15;
        else if (n == 16) esc_character_code_page[2] = 16;
        else if (n == 17) esc_character_code_page[2] = 17;
        else if (n == 18) esc_character_code_page[2] = 18;
        else if (n == 19) esc_character_code_page[2] = 19;
        else if (n == 20) esc_character_code_page[2] = 20;
        else if (n == 21) esc_character_code_page[2] = 21;
        else if (n == 22) esc_character_code_page[2] = 22;
        else if (n == 23) esc_character_code_page[2] = 23;
        else if (n == 24) esc_character_code_page[2] = 24;
        else if (n == 30) esc_character_code_page[2] = 30;
        else if (n == 40) esc_character_code_page[2] = 40;
        else if (n == 41) esc_character_code_page[2] = 41;
        else if (n == 42) esc_character_code_page[2] = 42;
        else if (n == 43) esc_character_code_page[2] = 43;
        else if (n == 50) esc_character_code_page[2] = 50;
        else if (n == 252) esc_character_code_page[2] = (byte) 252;
        else if (n == 253) esc_character_code_page[2] = (byte) 253;
        else if (n == 254) esc_character_code_page[2] = (byte) 254;
        else if (n == 255) esc_character_code_page[2] = (byte) 255;
        else esc_character_code_page[2] = 0x00;
        return sendMessageToPoint(esc_character_code_page);
    }

    /**
     * 29、选择字符大小。
     *
     * @param n 当n=2时2倍高，当n=3时3倍高，当n=4时4倍高，当n=20时2倍宽，当n=30时3倍宽，当n=40时4倍宽，当n=22时2倍宽高，当n=33时3倍宽高，
     *          当n=44时4倍宽高，当n取其他值时1倍宽高。
     */
    public boolean esc_character_size(int n) {
        // TODO: 2018/5/8 字符大小
        byte[] esc_character_size = new byte[3];
        esc_character_size[0] = 0x1D;
        esc_character_size[1] = 0x21;
        if (n == 2) esc_character_size[2] = 0x01;
        else if (n == 3) esc_character_size[2] = 0x02;
        else if (n == 4) esc_character_size[2] = 0x03;
        else if (n == 20) esc_character_size[2] = 0x10;
        else if (n == 30) esc_character_size[2] = 0x20;
        else if (n == 40) esc_character_size[2] = 0x30;
        else if (n == 22) esc_character_size[2] = 0x11;
        else if (n == 33) esc_character_size[2] = 0x22;
        else if (n == 44) esc_character_size[2] = 0x33;
        else esc_character_size[2] = 0x00;
        return sendMessageToPoint(esc_character_size);
    }

    /**
     * 7、初始化打印机。
     * 使所有设置恢复到打印机开机时的默认值模式。
     */
    public boolean esc_reset() {
        byte[] reset = {0x1B, 0x40};
        return sendMessageToPoint(reset);
    }

    /**
     * 8、选择加粗模式。
     *
     * @param b b为true时选择加粗模式，b为false时取消加粗模式。
     */
    public boolean esc_bold(boolean b) {
        byte[] esc_bold = new byte[3];
        esc_bold[0] = 0x1B;
        esc_bold[1] = 0x45;
        if (!b) esc_bold[2] = 0x00;
        else esc_bold[2] = 0x01;
        return sendMessageToPoint(esc_bold);
    }

    /**
     * 9、选择/取消下划线模式。
     *
     * @param n 当n=1或n=49时选择下划线模式且设置为1点宽，当n=2或n=50时选择下划线模式且设置为2点宽，当n取其他值时取消下划线模式。
     */
    public boolean esc_underline(int n) {
        byte[] esc_underline = new byte[3];
        esc_underline[0] = 0x1B;
        esc_underline[1] = 0x2D;
        if (n == 1 || n == 49) esc_underline[2] = 0x01;
        else if (n == 2 || n == 50) esc_underline[2] = 0x02;
        else esc_underline[2] = 0x00;
        return sendMessageToPoint(esc_underline);
    }

    /**
     * 10、打印和行进。
     * 基于当前的行间距，打印缓冲区内的数据并走纸一行。
     */
    public boolean esc_print_formfeed() {
        byte[] esc_print_formfeed = {0x0A};
        return sendMessageToPoint(esc_print_formfeed);
    }

    /**
     * 11、水平制表符。
     * 将打印位置移动至下一水平制表符位置。
     */
    public boolean esc_next_horizontal_tab() {
        byte[] esc_next_horizontal_tab = {0x09};
        return sendMessageToPoint(esc_next_horizontal_tab);
    }

    /**
     * 12、打印并走纸到左黑标处。
     * 将打印缓冲区中的数据全部打印出来并走纸到左黑标处。
     */
    public boolean esc_left_black_label() {
        byte[] esc_left_black_label = {0x0C};
        return sendMessageToPoint(esc_left_black_label);
    }

    /**
     * 13、打印并回车。
     * 该指令等同于LF指令，既打印缓冲区内的数据并走纸一字符行。
     */
    public boolean esc_print_enter() {
        byte[] esc_print_enter = {0x0D};
        return sendMessageToPoint(esc_print_enter);
    }

    /**
     * 14、设定右侧字符间距。
     *
     * @param n 当n＜0时设定右侧字符间距为0，当n＞255时设定右侧字符间距为【255×（水平或垂直移动单位）】,
     *          当0≤n≤255时设定右侧字符间距为【n×（水平或垂直移动单位）】。
     */
    public boolean esc_right_spacing(int n) {
        byte[] esc_right_space = new byte[3];
        esc_right_space[0] = 0x1B;
        esc_right_space[1] = 0x20;
        if (n < 0) esc_right_space[2] = 0x00;
        else if (0 <= n && n <= 255) esc_right_space[2] = (byte) n;
        else if (n > 255) esc_right_space[2] = (byte) 0xFF;
        return sendMessageToPoint(esc_right_space);
    }

    /**
     * 15、选择打印模式。
     *
     * @param n 当n=0时选择字符字体A，当n=1时选择字符字体B，当n=2时表示选择字符字体C，当n=3时表示选择字符字体D；
     *          当n=8时选择字符加粗模式，当n=16时选择字符倍高模式，当n=32时选择字符倍宽模式，当n=128时选择字符下划线模式。
     *          此命令字体、加粗模式、倍高模式、倍宽模式、下划线模式同时设置。若要多种效果叠加，只需将相应的值相加即可
     *          （例如若要B字体加粗，只需将n=1+8即n=9传入）。
     */
    public boolean esc_print_mode(int n) {
        byte[] esc_print_mode = new byte[3];
        esc_print_mode[0] = 0x1B;
        esc_print_mode[1] = 0x21;

        if (n <= 0) esc_print_mode[2] = 0x00;
        else if (n == 1) esc_print_mode[2] = 0x01;
        else if (n == 2) esc_print_mode[2] = 0x02;
        else if (n == 3) esc_print_mode[2] = 0x03;
        else if (n == 8) esc_print_mode[2] = 0x08;
        else if (n == 16) esc_print_mode[2] = 0x10;
        else if (n == 32) esc_print_mode[2] = 0x20;
        else if (n == 128) esc_print_mode[2] = (byte) 0x80;
        else if (n >= 255) esc_print_mode[2] = (byte) 0xFF;
        else esc_print_mode[2] = (byte) n;
        return sendMessageToPoint(esc_print_mode);
    }

    /**
     * 16、设置绝对打印位置。
     * 将当前位置设置到距离行首（nL+nH×256）×（横向或纵向移动单位）处。当nL＜0或nL＞255时将nL设置为0，当nH＜0或nH＞255时将nH设置为0。
     */
    public boolean esc_absolute_print_position(int nL, int nH) {
        // TODO: 2018/5/8 设置绝对打印位置
        byte[] esc_absolute_print_position = new byte[4];
        esc_absolute_print_position[0] = 0x1B;
        esc_absolute_print_position[1] = 0x24;

        if (nL < 0 || nL > 255) esc_absolute_print_position[2] = 0x00;
        else esc_absolute_print_position[2] = (byte) nL;

        if (nH < 0 || nH > 255) esc_absolute_print_position[3] = 0x00;
        else esc_absolute_print_position[3] = (byte) nH;
        return sendMessageToPoint(esc_absolute_print_position);
    }

    /**
     * 17、选择位图模式打印图片。
     *
     * @param m      m表示位图模式。当m=1时位图模式为8点双密度，当m=32时位图模式为24点单密度，当m=33时位图模式为24点双密度，
     *               除m=1,32,33之外位图模式都为8点单密度。
     * @param bitmap bitmap为要打印的位图。由于打印纸宽度有限，图片不可太大。
     */
    public boolean esc_bitmap_mode(int m, Bitmap bitmap) {
        if (m != 1 && m != 32 && m != 33) m = 0;
        bitmap = Bitmap.createBitmap(bitmap);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (m == 0 || m == 1) {
            int heightbytes = (height - 1) / 8 + 1;
            int bufsize = width * heightbytes;
            byte[] maparray = new byte[bufsize];
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            /**解析图片 获取位图数据**/
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    int pixel = pixels[width * j + i];
                    if (pixel != Color.WHITE) {//如果不是空白的话用黑色填充    这里如果童鞋要过滤颜色在这里处理
                        maparray[i + (j / 8) * width] |= (byte) (0x80 >> (j % 8));
                    }
                }
            }
            byte[] Cmd = new byte[5];
            byte[] pictureTop = new byte[]{0x1B, 0x33, 0x00};
            if (!sendMessageToPoint(pictureTop, 0, pictureTop.length)) {
                return false;
            }
            /**对位图数据进行处理**/
            for (int i = 0; i < heightbytes; i++) {
                Cmd[0] = 0x1B;
                Cmd[1] = 0x2A;
                Cmd[2] = (byte) m;
                Cmd[3] = (byte) (width % 256);
                Cmd[4] = (byte) (width / 256);
                if (!sendMessageToPoint(Cmd, 0, 5)) {
                    return false;
                }
                if (!sendMessageToPoint(maparray, i * width, width)) {
                    return false;
                }
                if (!sendMessageToPoint(new byte[]{0x0D, 0x0A}, 0, 2)) {
                    return false;
                }
            }
        } else {
            int heightbytes = (height - 1) / 8 + 1;
//            Log.i("heightbytes", String.valueOf(heightbytes));
            if ((heightbytes % 3) != 0) {
                heightbytes += 3 - heightbytes % 3;
//                Log.i("heightbytes", String.valueOf(heightbytes));
            }
            int bufsize = width * heightbytes;
            byte[] maparray = new byte[bufsize];
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            /**解析图片 获取位图数据**/
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
//                    Log.i(String.valueOf(bufsize),"j:"+j+" i:"+i+" index:"+(3*width*(j/24))+(i*3)+((j%24)/8));
                    int pixel = pixels[width * j + i];
                    if (pixel != Color.WHITE) {//如果不是空白的话用黑色填充    这里如果童鞋要过滤颜色在这里处理
                        int index = ((3 * width) * (j / 24)) + (i * 3) + ((j % 24) / 8);
//                        Log.i(String.valueOf(bufsize),"j:"+j+" i:"+i+" index:"+index);
                        if (index < bufsize) {
                            maparray[index] |= (byte) (0x80 >> (j % 8));
                        }
                    }
                }
            }
            byte[] Cmd = new byte[5];
            byte[] pictureTop = new byte[]{0x1B, 0x33, 0x00};
            if (!sendMessageToPoint(pictureTop, 0, pictureTop.length)) {
                return false;
            }
            /**对位图数据进行处理**/
//        Log.i("maxline", String.valueOf(((heightbytes-1)/3+1)));
            for (int j = 0; j < ((heightbytes - 1) / 3 + 1); j++) {
                Cmd[0] = 0x1B;
                Cmd[1] = 0x2A;
                Cmd[2] = (byte) m;
                Cmd[3] = (byte) (width % 256);
                Cmd[4] = (byte) (width / 256);
                if (!sendMessageToPoint(Cmd, 0, 5)) {
                    return false;
                }
//                for(int i = 0; i < width;i ++) {
//                    if(!sendMessageToPoint(maparray, ((3 * width) * (j / 24))+(i * 3)+((j % 24) / 8), 1)){return false;}
//                    if(!sendMessageToPoint(maparray, ((3 * width) * (j / 24))+(i * 3)+((j % 24) / 8), 1)){return false;}
//                    if(!sendMessageToPoint(maparray, ((3 * width) * (j / 24))+(i * 3)+((j % 24) / 8), 1)){return false;}
//                }
//                if(j < ((heightbytes-1)/3)){
//                    if(!sendMessageToPoint(maparray, j * width*3, width*3)){return false;}
//                }else {
//                    if(!sendMessageToPoint(maparray, j * width*3, bufsize - (j * width * 3))){return false;}
//                    byte[] end = new byte[(width*3)-(bufsize - (j * width * 3))];
//                    if(!sendMessageToPoint(end)){return false;}
//                }
                if (!sendMessageToPoint(maparray, j * width * 3, width * 3)) {
                    return false;
                }

                if (!sendMessageToPoint(new byte[]{0x0D, 0x0A}, 0, 2)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 18、设置默认行高。
     * 将行间距设为约 3.75mm{30/203"}。
     */
    public boolean esc_default_line_height() {
        byte[] esc_default_line_height = {0x1B, 0x32};
        return sendMessageToPoint(esc_default_line_height);
    }

    /**
     * 19、设置行高
     * 设置行高为[n×纵向或横向移动单位]英寸。
     *
     * @param n n表示行高值。当n＜0时设置行高为0，当n＞255时设置行高为255[n×纵向或横向移动单位]英寸，
     *          当0≤n≤255时设置行高为[n×纵向或横向移动单位]英寸。
     */
    public boolean esc_line_height(int n) {
        // TODO: 2018/5/8 设置行高
        byte[] esc_line_height = new byte[3];
        esc_line_height[0] = 0x1B;
        esc_line_height[1] = 0x33;
        if (n < 0) esc_line_height[2] = 0x00;
        else if (n > 255) esc_line_height[2] = (byte) 0xFF;
        else esc_line_height[2] = (byte) n;
        return sendMessageToPoint(esc_line_height);
    }

    /**
     * 20、设置水平制表符位置。
     *
     * @param n n的长度表示横向跳格数，n[k]表示第k个跳格位置的值。当n的长度大于32时，只取前32个值；当n[k]大于等于n[k-1]时忽略该命令。
     *          当n[k]≤0或n[k]≥255时，忽略该命令。
     */
    public boolean esc_horizontal_tab_position(int[] n) {
        byte[] esc_horizontal_tab_position_top = {0x1B, 0x44};
        byte[] targetB = new byte[32];
        if (n.length > 32) {
            int[] targetI = Arrays.copyOfRange(n, 0, 32);
            targetB[0] = (byte) targetI[0];
            for (int i = 1; i < targetI.length; i++) {
                if (targetI[i] <= targetI[i - 1]) return false;
                targetB[i] = (byte) targetI[i];
            }
            if (myDeviceConnection == null) return false;
            if (!sendMessageToPoint(esc_horizontal_tab_position_top, 0, esc_horizontal_tab_position_top.length))
                return false;
            if (!sendMessageToPoint(targetB, 0, targetB.length)) return false;
        } else {
            byte[] target = new byte[n.length];
            target[0] = (byte) n[0];
            for (int i = 1; i < n.length; i++) {
                if (n[i] <= n[i - 1]) return false;
                target[i] = (byte) n[i];
            }
            if (myDeviceConnection == null) return false;
            if (!sendMessageToPoint(esc_horizontal_tab_position_top, 0, esc_horizontal_tab_position_top.length))
                return false;
            if (!sendMessageToPoint(target, 0, target.length)) return false;
        }
        return true;
    }

    /**
     * 21、打印并进纸。
     *
     * @param n 当0≤n≤255时打印缓冲区数据并进纸【n×纵向或横向移动单位】英寸。当n＜0时进纸0，当n＞255时进纸【255×纵向或横向移动单位】英寸。
     */
    public boolean esc_print_formfeed(int n) {
        byte[] esc_print_formfeed = new byte[3];
        esc_print_formfeed[0] = 0x1B;
        esc_print_formfeed[1] = 0x4A;
        if (n < 0) esc_print_formfeed[2] = 0x00;
        else if (n > 255) esc_print_formfeed[2] = (byte) 0xFF;
        else esc_print_formfeed[2] = (byte) n;
        return sendMessageToPoint(esc_print_formfeed, 0, esc_print_formfeed.length);
    }

    /**
     * 22、选择字体。
     *
     * @param n 当n=1或n=49时选择字体B，当n=2或n=50时选择字体C，当n=3或n=51时选择字体D，当n为其他值时选择字体A。
     */
    public boolean esc_font(int n) {
        // TODO: 2018/5/8 选择字体
        byte[] esc_font = new byte[3];
        esc_font[0] = 0x1B;
        esc_font[1] = 0x4D;
        if (n == 1 || n == 49) esc_font[2] = 0x01;
        else if (n == 2 || n == 50) esc_font[2] = 0x02;
        else if (n == 3 || n == 51) esc_font[2] = 0x03;
        else esc_font[2] = 0x00;
        return sendMessageToPoint(esc_font);
    }

    /**
     * 23、选择国际字符集。
     *
     * @param n 当n≤0或n＞13时选择America字符集，当n=1时选择France字符集，当n=2时选择German字符集，当n=3时选择UK字符集，
     *          当n=4时选择Denmar字符集，当n=5时选择Sweden字符集，当n=6时选择Italy字符集，当n=7时选择Spain I字符集，当n=8时选择Japan字符集，
     *          当n=9时选择Norway字符集，当n=10时选择Denmar字符集，当n=11时选择Spain II字符集，当n=12时选择Latin字符集，当n=13时选择Korea字符集。
     */
    public boolean esc_national_character_set(int n) {
        byte[] esc_national_character_set = new byte[3];
        esc_national_character_set[0] = 0x1B;
        esc_national_character_set[1] = 0x52;
        if (n == 1) esc_national_character_set[2] = 0x01;
        else if (n == 2) esc_national_character_set[2] = 0x02;
        else if (n == 3) esc_national_character_set[2] = 0x03;
        else if (n == 4) esc_national_character_set[2] = 0x04;
        else if (n == 5) esc_national_character_set[2] = 0x05;
        else if (n == 6) esc_national_character_set[2] = 0x06;
        else if (n == 7) esc_national_character_set[2] = 0x07;
        else if (n == 8) esc_national_character_set[2] = 0x08;
        else if (n == 9) esc_national_character_set[2] = 0x09;
        else if (n == 10) esc_national_character_set[2] = 0x0A;
        else if (n == 11) esc_national_character_set[2] = 0x0B;
        else if (n == 12) esc_national_character_set[2] = 0x0C;
        else if (n == 13) esc_national_character_set[2] = 0x0D;
        else esc_national_character_set[2] = 0x00;
        return sendMessageToPoint(esc_national_character_set, 0, esc_national_character_set.length);
    }

    /**
     * 24、选择/取消顺时针旋转90°。
     *
     * @param n 当n=1或n=49时设置90°顺时针旋转模式，当n=2或n=50时设置180°顺时针旋转模式，当n=3或n=51时设置270°顺时针旋转模式，
     *          当n取其他值时取消旋转模式。
     */
    public boolean esc_rotate(int n) {
        byte[] esc_rotate = new byte[3];
        esc_rotate[0] = 0x1B;
        esc_rotate[1] = 0x56;
        if (n == 1 || n == 49) esc_rotate[2] = 0x01;
        else if (n == 2 || n == 50) esc_rotate[2] = 0x02;
        else if (n == 3 || n == 51) esc_rotate[2] = 0x03;
        else esc_rotate[2] = 0x00;
        return sendMessageToPoint(esc_rotate);
    }

    /**
     * 25、设定相对打印位置。
     * 将打印位置从当前位置移至（nL+nH×256）×（水平或垂直运动单位）。当nL＜0时设置nL=0，当nL＞255时设置nL=255。
     * 当nH＜0时设置nH=0，当nH＞255时设置nH=255。
     */
    public boolean esc_relative_print_position(int nL, int nH) {
        // TODO: 2018/5/8  设定相对打印位置
        byte[] esc_relative_print_position = new byte[4];
        esc_relative_print_position[0] = 0x1B;
        esc_relative_print_position[1] = 0x5C;

        if (nL < 0) esc_relative_print_position[2] = 0x00;
        else if (nL > 255) esc_relative_print_position[2] = (byte) 0xFF;
        else esc_relative_print_position[2] = (byte) nL;

        if (nH < 0) esc_relative_print_position[3] = 0x00;
        else if (nH > 255) esc_relative_print_position[3] = (byte) 0xFF;
        else esc_relative_print_position[3] = (byte) nH;
        return sendMessageToPoint(esc_relative_print_position);
    }

    /**
     * 26、选择对齐模式。
     *
     * @param n 当n=1或n=49时选择居中对齐，当n=2或n=50时选择右对齐，当n取其他值时选择左对齐。
     */
    public boolean esc_align(int n) {
        // TODO: 2018/5/8 对齐模式
        byte[] esc_align = new byte[3];
        esc_align[0] = 0x1B;
        esc_align[1] = 0x61;
        if (n == 1 || n == 49) esc_align[2] = 0x01;
        else if (n == 2 || n == 50) esc_align[2] = 0x02;
        else esc_align[2] = 0x00;
        return sendMessageToPoint(esc_align);
    }

    /**
     * 27、打印并向前走纸n行。
     *
     * @param n 当n＜0时进纸0行，当n＞255时进纸255行，当0≤n≤255时进纸n行。
     */
    public boolean esc_print_formfeed_row(int n) {
        byte[] esc_print_formfeed_row = new byte[3];
        esc_print_formfeed_row[0] = 0x1B;
        esc_print_formfeed_row[1] = 0x64;
        if (n < 0) esc_print_formfeed_row[2] = 0x00;
        else if (n > 255) esc_print_formfeed_row[2] = (byte) 0xFF;
        else esc_print_formfeed_row[2] = (byte) n;
        return sendMessageToPoint(esc_print_formfeed_row);
    }

    /**
     * 30、定义并打印下载位图。
     *
     * @param x    x表示位图的横向点数（1≤x≤255），
     * @param y    y表示位图的纵向点数（1≤y≤48）。
     * @param data data的长度等于x*y*8（1≤x*y≤1536），表示位图字节数，除以上取值外其他取值均忽略此命令。
     * @param m    m表示打印下载位图的模式，当m=1或m=49时设置倍宽模式，当m=2或m=50时设置倍高模式，当m=3或m=51时设置倍宽倍高模式，
     *             当m取其他值时设置普通模式打印所下载的位图。
     */
    public boolean esc_define_print_download_bitmap(int x, int y, byte[] data, int m) {
        byte[] esc_define_download_bitmap = new byte[4];
        esc_define_download_bitmap[0] = 0x1D;
        esc_define_download_bitmap[1] = 0x2A;
        if (x < 1 || x > 255 || y < 1 || y > 48 || (x * y) > 1536 || data.length != (x * y * 8))
            return false;
        esc_define_download_bitmap[2] = (byte) x;
        esc_define_download_bitmap[3] = (byte) y;
        byte[] target = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            target[i] = data[i];
        }
        if (myDeviceConnection == null) return false;
        if (!sendMessageToPoint(esc_define_download_bitmap, 0, esc_define_download_bitmap.length))
            return false;
        if (!sendMessageToPoint(target, 0, target.length)) return false;

        byte[] esc_print_download_bitmap = new byte[3];
        esc_print_download_bitmap[0] = 0x1D;
        esc_print_download_bitmap[1] = 0x2F;
        if (m == 1 || m == 49) esc_print_download_bitmap[2] = 0x01;
        else if (m == 2 || m == 50) esc_print_download_bitmap[2] = 0x02;
        else if (m == 3 || m == 51) esc_print_download_bitmap[2] = 0x03;
        else esc_print_download_bitmap[2] = 0x00;
        return sendMessageToPoint(esc_print_download_bitmap, 0, esc_print_download_bitmap.length);
    }

    /**
     * 31、选择/取消黑白反显打印模式。
     *
     * @param b 当b为true时选择黑白反显打印模式，当b为false时取消黑白反显打印模式。
     */
    public boolean esc_black_white_reverse(boolean b) {
        byte[] esc_black_white_reverse = new byte[3];
        esc_black_white_reverse[0] = 0x1D;
        esc_black_white_reverse[1] = 0x42;
        if (!b) esc_black_white_reverse[2] = 0x00;
        else esc_black_white_reverse[2] = 0x01;
        return sendMessageToPoint(esc_black_white_reverse);
    }

    /**
     * 32、设定左边距。
     * 当0≤nL≤255且0≤nH≤255时，将左边距设为【(nL+nH×256)×(水平移动单位)】。当nL和nH取其他值时将左边距设为0。
     */
    public boolean esc_left_margin(int nL, int nH) {
        byte[] esc_left_margin = new byte[4];
        esc_left_margin[0] = 0x1D;
        esc_left_margin[1] = 0x4C;
        if (0 <= nL && nL <= 255 && 0 <= nH && nH <= 255) {
            esc_left_margin[2] = (byte) nL;
            esc_left_margin[3] = (byte) nH;
        } else {
            esc_left_margin[2] = 0x00;
            esc_left_margin[3] = 0x00;
        }
        return sendMessageToPoint(esc_left_margin);
    }

    /**
     * 33、设定横向和纵向移动单位。
     * 当0≤x≤255且0≤y≤255时分别将水平和垂直移动单位设为25.4/x毫米和25.4/y毫米。当x和y取其他值时取x=0和Y=0。
     */
    public boolean esc_move_unit(int x, int y) {
        byte[] esc_move_unit = new byte[4];
        esc_move_unit[0] = 0x1D;
        esc_move_unit[1] = 0x50;
        if (0 <= x && x <= 255 && 0 <= y && y <= 255) {
            esc_move_unit[2] = (byte) x;
            esc_move_unit[3] = (byte) y;
        } else {
            esc_move_unit[2] = 0x00;
            esc_move_unit[3] = 0x00;
        }
        return sendMessageToPoint(esc_move_unit);
    }

    /**
     * 34、设定打印区域宽度。
     * 当0≤nL≤255且0≤nH≤255时,将打印区域宽度设为（nL+nH×256）×（水平移动单位）。当nL和nH取其他值时取nL=0和nH=0。
     */
    public boolean esc_print_area_width(int nL, int nH) {
        byte[] esc_print_area_width = new byte[4];
        esc_print_area_width[0] = 0x1D;
        esc_print_area_width[1] = 0x57;
        if (0 <= nL && nL <= 255 && 0 <= nH && nH <= 255) {
            esc_print_area_width[2] = (byte) nL;
            esc_print_area_width[3] = (byte) nH;
        } else {
            esc_print_area_width[2] = 0x00;
            esc_print_area_width[3] = 0x00;
        }
        return sendMessageToPoint(esc_print_area_width);
    }

    /**
     * 36、设置汉字字符模式。
     *
     * @param n 当n=4时选择倍宽，当n=8时选择倍高，当n=128时选择下划线，当n=12时选择倍高倍宽，当n=132时选择倍宽下划线，当n=136时选择倍高下划线，
     *          当n=140时选择倍宽倍高下划线，当n取其他值时不选择倍高倍宽下划线。
     *          倍高、倍宽、下划线模式同时设置。
     */
    public boolean esc_chinese_character_mode(int n) {
        byte[] esc_chinese_character_mode = new byte[3];
        esc_chinese_character_mode[0] = 0x1C;
        esc_chinese_character_mode[1] = 0x21;
        if (n == 4) esc_chinese_character_mode[2] = 0x04;
        else if (n == 8) esc_chinese_character_mode[2] = 0x08;
        else if (n == 128) esc_chinese_character_mode[2] = (byte) 128;
        else if (n == 12) esc_chinese_character_mode[2] = 12;
        else if (n == 132) esc_chinese_character_mode[2] = (byte) 132;
        else if (n == 136) esc_chinese_character_mode[2] = (byte) 136;
        else if (n == 140) esc_chinese_character_mode[2] = (byte) 140;
        else esc_chinese_character_mode[2] = 0x00;
        return sendMessageToPoint(esc_chinese_character_mode);
    }

    /**
     * 37、选择/取消汉字下划线模式。
     *
     * @param n 当n=1或n=49时选择汉字下划线（1点宽），当n=2或n=50时选择汉字下划线（2点宽），当n为其他值时不加下划线。
     */
    public boolean esc_chinese_character_underline_mode(int n) {
        byte[] esc_chinese_character_underline_mode = new byte[3];
        esc_chinese_character_underline_mode[0] = 0x1C;
        esc_chinese_character_underline_mode[1] = 0x2D;
        if (n == 1 || n == 49) esc_chinese_character_underline_mode[2] = 0x01;
        else if (n == 2 || n == 50) esc_chinese_character_underline_mode[2] = 0x02;
        else esc_chinese_character_underline_mode[2] = 0x00;
        return sendMessageToPoint(esc_chinese_character_underline_mode);
    }

    /**
     * 38、定义自定义汉字。
     *
     * @param c2   c2表示自定义字符编码第二个字节,取值范围为A1H≤c2≤FEH，第一个字节为FEH，
     * @param data data表示自定义汉字的数据，1表示打印一个点，0表示不打印点。
     *             data的长度为72，若data的长度不等于72或data的每个元素值出现小于0或大于255的情况，则忽略该命令。
     */
    public boolean esc_define_chinese_character(int c2, byte[] data) {
        byte[] esc_define_chinese_character = new byte[4];
        esc_define_chinese_character[0] = 0x1C;
        esc_define_chinese_character[1] = 0x32;
        esc_define_chinese_character[2] = (byte) 0xFE;

        if (c2 < 0xA1 || c2 > 0xFE || (data.length != 72)) return false;
        for (int aData : data) {
            if (aData < 0 || aData > 255) return false;
        }
        esc_define_chinese_character[3] = (byte) c2;
        if (myDeviceConnection == null) return false;
        if (!sendMessageToPoint(esc_define_chinese_character, 0, esc_define_chinese_character.length))
            return false;
        byte[] target = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            target[i] = data[i];
        }
        return sendMessageToPoint(target);
    }

    /**
     * 39、选择/取消汉字倍高倍宽。
     *
     * @param b 当b为true时选择汉字倍高倍宽模式，当b为false时取消汉字倍高倍宽模式。
     */
    public boolean esc_chinese_character_twice_height_width(boolean b) {
        byte[] esc_chinese_character_twice_height_width = new byte[3];
        esc_chinese_character_twice_height_width[0] = 0x1C;
        esc_chinese_character_twice_height_width[1] = 0x57;

        if (!b) esc_chinese_character_twice_height_width[2] = 0x00;
        else esc_chinese_character_twice_height_width[2] = 0x01;
        return sendMessageToPoint(esc_chinese_character_twice_height_width);
    }

    /**
     * 40、打印并走纸到右黑标处。
     */
    public boolean esc_print_to_right_black_label() {
        byte[] esc_print_to_right_black_label = {0x0E};
        return sendMessageToPoint(esc_print_to_right_black_label);
    }

    /**
     * 41、走纸到标签处。
     */
    public boolean esc_print_to_label() {
        byte[] esc_print_to_label = {0x1D, 0x0C};
        return sendMessageToPoint(esc_print_to_label);
    }

    /**
     * 42、打印光栅位图。
     *
     * @param m      m表示光栅位图模式，当m=1或m=49时选择倍宽模式，当m=2或m=50时选择倍高模式，当m=3或m=51时选择倍宽倍高模式。
     * @param bitmap 所要打印的位图。
     */
    public boolean esc_print_grating_bitmap(int m, Bitmap bitmap) {
//        bitmap = Bitmap.createBitmap(bitmap);
//        int heigh = bitmap.getHeight();
//        int width = bitmap.getWidth();
//        int iDataLen = width * heigh;
//        int[] pixels = new int[iDataLen];
//        bitmap.getPixels(pixels, 0, width, 0, 0, width, heigh);
//        if(!sendMessageToPoint(PrintCmd.PrintDiskImagefile(pixels, width, heigh))) return false;

        byte[] esc_print_grating_bitmap = new byte[8];
        esc_print_grating_bitmap[0] = 0x1D;
        esc_print_grating_bitmap[1] = 0x76;
        esc_print_grating_bitmap[2] = 0x30;
        if (m == 1 || m == 49) esc_print_grating_bitmap[3] = 0x01;
        else if (m == 2 || m == 50) esc_print_grating_bitmap[3] = 0x02;
        else if (m == 3 || m == 51) esc_print_grating_bitmap[3] = 0x03;
        else esc_print_grating_bitmap[3] = 0x00;
        bitmap = Bitmap.createBitmap(bitmap);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int widthbytes = (width - 1) / 8 + 1;
        int bufsize = height * widthbytes;
        byte[] maparray = new byte[bufsize];
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        /**解析图片 获取位图数据**/
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int pixel = pixels[width * j + i];
                if (pixel != Color.WHITE) {
                    maparray[j * widthbytes + i / 8] |= (byte) (0x80 >> (i % 8));
                }
            }
        }
        esc_print_grating_bitmap[4] = (byte) (widthbytes % 256);
        esc_print_grating_bitmap[5] = (byte) (widthbytes / 256);
        esc_print_grating_bitmap[6] = (byte) (height % 256);
        esc_print_grating_bitmap[7] = (byte) (height / 256);
        if (myDeviceConnection == null) return false;
//        if(!esc_line_height(0)) return false;
        if (!sendMessageToPoint(esc_print_grating_bitmap)) return false;
        return sendMessageToPoint(maparray);
//        return true;
    }

    /**
     * 43、设置参数打印条码。
     *
     * @param HRI_position HRI_position表示HRI字符打印位置(当HRI_position=1或HRI_position=49时HRI字符显示在条形码上方；
     *                     当HRI_position=2或HRI_position=50时HRI字符显示在条形码下方；当HRI_position取其他值时HRI字符不显示)。
     * @param HRI_font     HRI_font表示HRI字符字体（当HRI_font=1或HRI_font=49时选择字体B，当HRI_font取其他值时选择字体A）。
     * @param width        width表示条码宽度（当width=2时设置条形码宽度为2，当width=3时设置条形码宽度为3，当width取其他值时设置条形码宽度为1），
     * @param height       height表示条码高度（当1<=height<=255时设置条码高度为height，当height取其他值时设置条码高度为162），
     * @param type         type表示条码类型（当type=0或type=65时选择条码类型为UPC-A，当type=1或type=66时选择条码类型为UPC-E，
     *                     当type=2或type=67时选择条码类型为EAN13，当type=3或type=68时选择条码类型为EAN8，当type=4或type=69时选择条码类型为CODE39，
     *                     当type=5或type=70时选择条码类型为ITF，当type=6或type=71时选择条码类型为CODABAR，当type=7或type=72时选择条码类型为CODE93，
     *                     当type=8或type=73时选择条码类型为CODE128），
     * @param content      content表示条码内容（UPC-A（长度为11、12）、UPC-E（长度为7、8、11、12）、EAN13（长度为12、13）、EAN8（长度为7、8）、
     *                     ITF（长度为大于2的偶数）只支持数字；
     *                     CODE39（长度大于1且小于255，支持数字、英文、空格、‘$’、‘%’、‘*’、‘+’、‘-’、‘.’、‘/’）；
     *                     CODE93（长度大于1且小于255，支持数字、英文、空格、‘$’、‘%’、‘+’、‘-’、‘.’、‘/’）；
     *                     CODABAR（长度大于2且小于255，支持数字、英文ABCDabcd、‘$’、‘+’、‘-’、‘.’、‘/’、‘:’）；
     *                     CODE128（长度大于2且小于255，支持所有英文）。
     */
    public boolean esc_barcode_1d(int HRI_position, int HRI_font, int width, int height, int type, String content) {
        // TODO: 2018/5/10 打印条形码
        byte[] esc_barcode_1d_HRI_position = {0x1D, 0x48, 0x00};//设置字符打印的位置
        if (HRI_position == 1 || HRI_position == 49) esc_barcode_1d_HRI_position[2] = 0x01;
        if (HRI_position == 2 || HRI_position == 50) esc_barcode_1d_HRI_position[2] = 0x02;
        else esc_barcode_1d_HRI_position[2] = 0x00;
        if (myDeviceConnection == null) return false;
        if (!sendMessageToPoint(esc_barcode_1d_HRI_position, 0, esc_barcode_1d_HRI_position.length))
            return false;

        byte[] esc_barcode_1d_HRI_font = {0x1D, 0x66, 0x00};//选择HRI（Human Readable Interpretation ）字符字型
        if (HRI_font == 1 || HRI_font == 49) esc_barcode_1d_HRI_font[2] = 0x01;
        else esc_barcode_1d_HRI_font[2] = 0x00;
        if (myDeviceConnection == null) return false;
        if (!sendMessageToPoint(esc_barcode_1d_HRI_font, 0, esc_barcode_1d_HRI_font.length))
            return false;

        byte[] esc_barcode_1d_width = {0x1D, 0x77, 0x00};//设置条形码宽度
        if (width == 2) esc_barcode_1d_width[2] = 0x02;
        if (width == 3) esc_barcode_1d_width[2] = 0x03;
        else esc_barcode_1d_width[2] = 0x01;
        if (myDeviceConnection == null) return false;
        if (!sendMessageToPoint(esc_barcode_1d_width, 0, esc_barcode_1d_width.length)) return false;

        byte[] esc_barcode_1d_height = {0x1D, 0x68, (byte) 0xA2};//设置条形码高度
        if (height <= 0 || height > 255) esc_barcode_1d_height[2] = (byte) 0xA2;
        else esc_barcode_1d_height[2] = (byte) height;
        if (myDeviceConnection == null) return false;
        if (!sendMessageToPoint(esc_barcode_1d_height, 0, esc_barcode_1d_height.length))
            return false;

//        byte[] esc_barcode_1d_l = {0x1D, 0x4C, 0x10, 0x00};//设置条形码左侧空白量
//        if (myDeviceConnection == null) return false;
//        if (!sendMessageToPoint(esc_barcode_1d_l, 0, esc_barcode_1d_l.length))
//            return false;
//
       byte[] esc_barcode_1d_l = {0x1D, 0x57, 0x10, 0x19};//设置条形码可打印范围量
        if (myDeviceConnection == null) return false;
        if (!sendMessageToPoint(esc_barcode_1d_l, 0, esc_barcode_1d_l.length))
            return false;


        if (type == 0 || type == 65) type = 0;
        else if (type == 1 || type == 66) type = 1;
        else if (type == 2 || type == 67) type = 2;
        else if (type == 3 || type == 68) type = 3;
        else if (type == 4 || type == 69) type = 0x45;
        else if (type == 5 || type == 70) type = 5;
        else if (type == 6 || type == 71) type = 0x47;
        else if (type == 7 || type == 72) type = 0x48;
        else if (type == 8 || type == 73) type = 8;
        else if (type == 9 || type == 76) type = 9;
        else type = 8;
        byte[] esc_barcode_1d_type = {0x1D, 0x6B, (byte) type};//打印条形码
        if (myDeviceConnection == null) return false;
        if (!sendMessageToPoint(esc_barcode_1d_type, 0, esc_barcode_1d_type.length)) return false;

        byte[] esc_barcode_1d_content_end = new byte[]{0x00};
        if (myDeviceConnection == null) return false;
        if (!sendMessageToPoint(content.getBytes(), 0, content.length())) return false;
        if (!sendMessageToPoint(esc_barcode_1d_content_end)) return false;
        return true;
    }

    /**
     * 44、打印二维码。
     *
     * @param type    type表示二维码类型，当type=0时选择PDF417，当type=2时选择DATAMATRIX，当type取其他值时选择QRCODE。
     * @param content content表示要打印的二维码内容。
     */
    public boolean esc_print_barcode_2d(int type, String content) {
        // TODO: 2018/5/10 打印二维码
        if (type == 0) type = 10;
        else if (type == 2) type = 12;
        else type = 11;
        byte[] esc_print_barcode_2d_type = {0x1D, 0x6B, (byte) type};
        if (myDeviceConnection == null) return false;
        if (!sendMessageToPoint(esc_print_barcode_2d_type)) return false;

        if (myDeviceConnection == null) return false;
        if (!sendMessageToPoint(content.getBytes(), 0, content.length())) return false;

        byte[] esc_barcode_1d_content_end = new byte[]{0x00};
        if (!sendMessageToPoint(esc_barcode_1d_content_end)) return false;
        return true;
    }

    /**
     * 45、页模式下取消打印数据。
     * 使所有设置恢复到打印机开机时的默认值模式。
     */
    public boolean esc_pagemode_cancel_data() {
        byte[] cancelData = {0x18};
        return sendMessageToPoint(cancelData);
    }

    /**
     * 46、实时状态传送。
     *
     * @param n 当n=1时传送打印机状态，当n=2时传送脱机状态，当n=3时传送错误状态，当n=4时传送卷纸传感器状态，当n取其他值时无效。
     */
    public boolean esc_realtime_state_transfer(int n) {
        byte[] esc_realtime_state_transfer = new byte[3];
        esc_realtime_state_transfer[0] = 0x10;
        esc_realtime_state_transfer[1] = 0x04;
        if (n == 1) esc_realtime_state_transfer[2] = 0x01;
        else if (n == 2) esc_realtime_state_transfer[2] = 0x02;
        else if (n == 3) esc_realtime_state_transfer[2] = 0x03;
        else if (n == 4) esc_realtime_state_transfer[2] = 0x04;
        else {
            return false;
        }
        return sendMessageToPoint(esc_realtime_state_transfer);
    }

    /**
     * 47、实时打印机请求。
     *
     * @param n 当n=1时从错误恢复并从错误出现的行开始重新打印，当n=2时在清除接收和打印缓冲区后从错误恢复，当n取其他值时无效。
     */
    public boolean esc_realtime_request(int n) {
        byte[] esc_realtime_request = new byte[3];
        esc_realtime_request[0] = 0x10;
        esc_realtime_request[1] = 0x05;

        if (n == 1) {
            esc_realtime_request[2] = 0x01;
        } else if (n == 2) {
            esc_realtime_request[2] = 0x02;
        } else {
            return false;
        }
        return sendMessageToPoint(esc_realtime_request);
    }

    /**
     * 48、页模式下打印数据。
     */
    public boolean esc_pagemode_print() {
        byte[] esc_realtime_request = new byte[2];
        esc_realtime_request[0] = 0x1B;
        esc_realtime_request[1] = 0x0C;
        return sendMessageToPoint(esc_realtime_request);
    }

    /**
     * 49、选择页模式。
     */
    public boolean esc_select_page_mode() {
        byte[] esc_select_page_mode = new byte[2];
        esc_select_page_mode[0] = 0x1B;
        esc_select_page_mode[1] = 0x4C;
        return sendMessageToPoint(esc_select_page_mode);
    }

    /**
     * 50、选择标准模式。
     */
    public boolean esc_select_standard_mode() {
        byte[] esc_select_page_mode = new byte[2];
        esc_select_page_mode[0] = 0x1B;
        esc_select_page_mode[1] = 0x53;
        return sendMessageToPoint(esc_select_page_mode);
    }

    /**
     * 51、在页模式下选择打印方向。
     *
     * @param n 当n=0或48时从左上角开始从左到右，当n=1或49时从左下角开始从底到上，当n=2或50时从右下角开始从右到左，当n=3或51时从右上角开始从上到下，当n取其他值时从左上角开始从左到右。
     */
    public boolean esc_pagemode_select_print_dir(int n) {
        byte[] esc_pagemode_select_print_dir = new byte[3];
        esc_pagemode_select_print_dir[0] = 0x1B;
        esc_pagemode_select_print_dir[1] = 0x54;

        if (n == 1 || n == 49) {
            esc_pagemode_select_print_dir[2] = 0x01;
        } else if (n == 2 || n == 50) {
            esc_pagemode_select_print_dir[2] = 0x02;
        } else if (n == 3 || n == 51) {
            esc_pagemode_select_print_dir[2] = 0x03;
        } else {
            esc_pagemode_select_print_dir[2] = 0x00;
        }
        return sendMessageToPoint(esc_pagemode_select_print_dir);
    }

    /**
     * 52、在页模式下设置打印区域 。
     *
     * @param xL,xH,yL,yH     定义水平和垂直起始位置。x表示横向，y表示纵向。L表示地位，H表示高位。
     * @param dxL,dxH,dyL,dyH 定义水平和垂直终止位置。x表示横向，y表示纵向。L表示地位，H表示高位。
     */
    public boolean esc_pagemode_select_print_position(int xL, int xH, int yL, int yH, int dxL, int dxH, int dyL, int dyH) {
        byte[] esc_pagemode_select_print_position = new byte[10];
        esc_pagemode_select_print_position[0] = 0x1B;
        esc_pagemode_select_print_position[1] = 0x57;

        if (xL < 0 || xL > 255) {
            esc_pagemode_select_print_position[2] = 0x00;
        } else {
            esc_pagemode_select_print_position[2] = (byte) xL;
        }
        if (xH < 0 || xH > 2) {
            esc_pagemode_select_print_position[3] = 0x00;
        } else {
            esc_pagemode_select_print_position[3] = (byte) xH;
        }
        if (yL < 0 || yL > 255) {
            esc_pagemode_select_print_position[4] = 0x00;
        } else {
            esc_pagemode_select_print_position[4] = (byte) yL;
        }
        if (yH < 0 || yH > 255) {
            esc_pagemode_select_print_position[5] = 0x00;
        } else {
            esc_pagemode_select_print_position[5] = (byte) yH;
        }

        if (dxL < 0 || dxL > 255) {
            esc_pagemode_select_print_position[6] = (byte) 0xFF;
        } else {
            esc_pagemode_select_print_position[6] = (byte) xL;
        }
        if (dxH < 0 || dxH > 2) {
            esc_pagemode_select_print_position[7] = 0x02;
        } else {
            esc_pagemode_select_print_position[7] = (byte) xH;
        }
        if (dyL < 0 || dyL > 255) {
            esc_pagemode_select_print_position[8] = (byte) 0xFF;
        } else {
            esc_pagemode_select_print_position[8] = (byte) yL;
        }
        if (dyH < 0 || dyH > 255) {
            esc_pagemode_select_print_position[9] = (byte) 0xFF;
        } else {
            esc_pagemode_select_print_position[9] = (byte) yH;
        }
        return sendMessageToPoint(esc_pagemode_select_print_position);
    }

    /**
     * 53、选择打印纸传感器以输出缺纸信号。
     *
     * @param n 当n=1时禁止纸将尽传感器，禁止纸尽传感器；当n=2时时激活纸将尽传感器，禁止纸尽传感器；
     *          当n=3时时禁止纸将尽传感器，激活纸尽传感器；当n=4时时激活纸将尽传感器，激活纸尽传感器；当n取其他值时禁止纸将尽传感器，禁止纸尽传感器。
     */
    public boolean esc_select_papersensor_paperless(int n) {
        byte[] esc_select_papersensor_paperless = new byte[4];
        esc_select_papersensor_paperless[0] = 0x1B;
        esc_select_papersensor_paperless[1] = 0x63;
        esc_select_papersensor_paperless[2] = 0x33;

        if (n == 1) {
            esc_select_papersensor_paperless[3] = 0x00;
        } else if (n == 2) {
            esc_select_papersensor_paperless[3] = 0x02;
        } else if (n == 3) {
            esc_select_papersensor_paperless[3] = 0x08;
        } else if (n == 4) {
            esc_select_papersensor_paperless[3] = 0x0A;
        } else {
            esc_select_papersensor_paperless[3] = 0x00;
        }
        return sendMessageToPoint(esc_select_papersensor_paperless);
    }

    /**
     * 54、选择打印纸传感器以停止打印。
     *
     * @param n 当n=1时激活纸将尽传感器；当n取其他值时禁止纸将尽传感器。
     */
    public boolean esc_select_papersensor_stopprint(int n) {
        byte[] esc_select_papersensor_stopprint = new byte[4];
        esc_select_papersensor_stopprint[0] = 0x1B;
        esc_select_papersensor_stopprint[1] = 0x63;
        esc_select_papersensor_stopprint[2] = 0x34;

        if (n == 1) {
            esc_select_papersensor_stopprint[3] = 0x02;
        } else {
            esc_select_papersensor_stopprint[3] = 0x00;
        }
        return sendMessageToPoint(esc_select_papersensor_stopprint);
    }

    /**
     * 55、激活/禁止面板按键。
     *
     * @param bool 当bool为true时禁止面板按键；当bool为flase时激活面板按键。
     */
    public boolean esc_disable_panel_key(boolean bool) {
        byte[] esc_disable_panel_key = new byte[4];
        esc_disable_panel_key[0] = 0x1B;
        esc_disable_panel_key[1] = 0x63;
        esc_disable_panel_key[2] = 0x35;

        if (bool) {
            esc_disable_panel_key[3] = 0x01;
        } else {
            esc_disable_panel_key[3] = 0x00;
        }
        return sendMessageToPoint(esc_disable_panel_key);
    }

    /**
     * 56、设置/解除颠倒打印模式。
     *
     * @param bool 当bool为true时设置颠倒打印模式；当bool为flase时解除颠倒打印模式。
     */
    public boolean esc_inverted_print_mode(boolean bool) {
        byte[] esc_inverted_print_mode = new byte[4];
        esc_inverted_print_mode[0] = 0x1B;
        esc_inverted_print_mode[1] = 0x7B;

        if (bool) {
            esc_inverted_print_mode[2] = 0x01;
        } else {
            esc_inverted_print_mode[2] = 0x00;
        }
        return sendMessageToPoint(esc_inverted_print_mode);
    }

    /**
     * 57、打印NV位图。
     *
     * @param n 是NV位图的数量。
     * @param m 指定位图模式。m=0或m=48时为普通模式；m=1或m=49时为倍宽模式；m=2或m=50时为倍高模式；m=3或m=51时为4倍大小模式；m取其他值时为普通模式；。
     */
    public boolean esc_print_NV_bitmap(int n, int m) {
        byte[] esc_print_NV_bitmap = new byte[4];
        esc_print_NV_bitmap[0] = 0x1C;
        esc_print_NV_bitmap[1] = 0x70;

        if (n < 0 || n > 255) {
            esc_print_NV_bitmap[2] = 0x01;
        } else {
            esc_print_NV_bitmap[2] = (byte) n;
        }

        if (m == 0 || m == 48) {
            esc_print_NV_bitmap[3] = 0x00;
        } else if (m == 1 || m == 49) {
            esc_print_NV_bitmap[3] = 0x01;
        } else if (m == 2 || m == 50) {
            esc_print_NV_bitmap[3] = 0x02;
        } else if (m == 3 || m == 51) {
            esc_print_NV_bitmap[3] = 0x03;
        } else {
            esc_print_NV_bitmap[3] = 0x00;
        }
        return sendMessageToPoint(esc_print_NV_bitmap);
    }

    /**
     * 58、定义NV位图。
     *
     * @param xL 。
     */
    public boolean esc_define_NV_bitmap(int xL, int xH, int yL, int yH, byte[] data) {
        byte[] esc_define_NV_bitmap = new byte[7];
        esc_define_NV_bitmap[0] = 0x1C;
        esc_define_NV_bitmap[1] = 0x71;
        esc_define_NV_bitmap[2] = 0x01;
        if (xL < 0 || xL > 255) {
            esc_define_NV_bitmap[3] = 0x00;
        } else {
            esc_define_NV_bitmap[3] = (byte) xL;
        }
        if (xH < 0 || xH > 2) {
            esc_define_NV_bitmap[4] = 0x00;
        } else {
            esc_define_NV_bitmap[4] = (byte) xH;
        }
        if (yL < 0 || yL > 255) {
            esc_define_NV_bitmap[5] = 0x00;
        } else {
            esc_define_NV_bitmap[5] = (byte) yL;
        }
        if (yH < 0 || yH > 255) {
            esc_define_NV_bitmap[6] = 0x00;
        } else {
            esc_define_NV_bitmap[6] = (byte) yH;
        }
        return sendMessageToPoint(esc_define_NV_bitmap) && sendMessageToPoint(data);
    }

    /**
     * 59、页模式下设置绝对垂直打印位置。
     *
     * @param nL,nH 在页模式下对缓冲数据设定绝对垂直打印起始位置。nL表示低位，nH表示高位。
     */
    public boolean esc_pagemode_set_absolute_vertical_location(int nL, int nH) {
        byte[] esc_pagemode_set_absolute_vertical_location = new byte[4];
        esc_pagemode_set_absolute_vertical_location[0] = 0x1D;
        esc_pagemode_set_absolute_vertical_location[1] = 0x24;

        if (nL < 0 || nL > 255) {
            esc_pagemode_set_absolute_vertical_location[2] = 0x00;
        } else {
            esc_pagemode_set_absolute_vertical_location[2] = (byte) nL;
        }

        if (nH < 0 || nH > 255) {
            esc_pagemode_set_absolute_vertical_location[3] = 0x00;
        } else {
            esc_pagemode_set_absolute_vertical_location[3] = (byte) nH;
        }
        return sendMessageToPoint(esc_pagemode_set_absolute_vertical_location);
    }

    /**
     * 60、执行测试打印。
     *
     * @param n 当n=0或48时打印纸为basic sheeet(卷纸)；当n=1或49时打印纸为卷纸；当n=2或50时打印纸为卷纸；当n取其他值时打印纸为basic sheeet(卷纸)。
     * @param m 当n=1或49时测试模式为十六进制转储；当n=2或50时测试模式为打印机状态打印；当n=3或51时测试模式为卷纸模式打印；当n取其他值时测试模式为十六进制转储。
     */
    public boolean esc_test_print(int n, int m) {
        byte[] esc_test_print = new byte[7];
        esc_test_print[0] = 0x1D;
        esc_test_print[1] = 0x28;
        esc_test_print[2] = 0x41;
        esc_test_print[3] = 0x02;
        esc_test_print[4] = 0x00;

        if (n == 0 || n == 48) {
            esc_test_print[5] = 0x00;
        } else if (n == 1 || n == 49) {
            esc_test_print[5] = 0x01;
        } else if (n == 2 || n == 50) {
            esc_test_print[5] = 0x02;
        } else {
            esc_test_print[5] = 0x00;
        }

        if (m == 1 || m == 49) {
            esc_test_print[6] = 0x01;
        } else if (m == 2 || m == 50) {
            esc_test_print[6] = 0x02;
        } else if (m == 3 || m == 51) {
            esc_test_print[6] = 0x03;
        } else {
            esc_test_print[6] = 0x01;
        }
        return sendMessageToPoint(esc_test_print);
    }

    /**
     * 61、切纸。
     */
    public boolean esc_select_cutting_mode() {
        byte[] esc_select_cutting_mode = new byte[3];
        esc_select_cutting_mode[0] = 0x1D;
        esc_select_cutting_mode[1] = 0x56;
        esc_select_cutting_mode[2] = 0x00;
        return sendMessageToPoint(esc_select_cutting_mode);
    }

    /**
     * 62、选择切纸模式并切纸。
     *
     * @param n 当n=0时打印机进纸到切纸位置并切纸；当0<n<=255时打印机进纸到切纸位置后再进纸n×0.125毫米后切纸；当n取其他值时打印机进纸到切纸位置并切纸。
     */
    public boolean esc_select_cutting_mode(int n) {
        byte[] esc_select_cutting_mode = new byte[4];
        esc_select_cutting_mode[0] = 0x1D;
        esc_select_cutting_mode[1] = 0x56;
        esc_select_cutting_mode[2] = 0x42;

        if (n < 0 || n > 255) {
            esc_select_cutting_mode[3] = 0x00;
        } else if (n == 0) {
            esc_select_cutting_mode[3] = 0x00;
        } else {
            esc_select_cutting_mode[3] = (byte) n;
        }
        return sendMessageToPoint(esc_select_cutting_mode);
    }

    /**
     * 63、页模式下设置相对垂直打印位置。
     *
     * @param nL,nH 页模式下设置从当前位置起，相对垂直打印起点位置。该命令设置的距离从当前位置到【（nL+nH×256）×0.125毫米】。
     */
    public boolean esc_pagemode_relative_print_position(int nL, int nH) {
        byte[] esc_pagemode_relative_print_position = new byte[4];
        esc_pagemode_relative_print_position[0] = 0x1D;
        esc_pagemode_relative_print_position[1] = 0x5C;

        if (nL < 0 || nL > 255) {
            esc_pagemode_relative_print_position[2] = 0x00;
        } else {
            esc_pagemode_relative_print_position[2] = (byte) nL;
        }

        if (nH < 0 || nH > 255) {
            esc_pagemode_relative_print_position[3] = 0x00;
        } else {
            esc_pagemode_relative_print_position[3] = (byte) nH;
        }
        return sendMessageToPoint(esc_pagemode_relative_print_position);
    }

    /**
     * 65、设置/解除平滑模式。
     *
     * @param bool 当bool为true时设置平滑模式，当bool为false时解除平滑模式。
     */
    public boolean esc_smoothing_model(boolean bool) {
        byte[] esc_smoothing_model = new byte[3];
        esc_smoothing_model[0] = 0x1D;
        esc_smoothing_model[1] = 0x62;

        if (bool) {
            esc_smoothing_model[2] = 0x01;
        } else {
            esc_smoothing_model[2] = 0x00;
        }

        return sendMessageToPoint(esc_smoothing_model);
    }

    /**
     * 66、选择用户自定义汉字区。
     *
     * @param n 当n=0或48时选择用户区1（码位范围AAA1～AFFE）；当n=1或49时选择用户区2（码位范围F8A1～FEFE）；当n=2或50时选择用户区3（码位范围A140～A7A0）；当n取其他值时无效。
     */
    public boolean esc_select_custom_chinese_area(int n) {
        byte[] esc_select_custom_chinese_area = new byte[3];
        esc_select_custom_chinese_area[0] = 0x1C;
        esc_select_custom_chinese_area[1] = 0x43;

        if (n == 0 || n == 48) {
            esc_select_custom_chinese_area[2] = 0x00;
        } else if (n == 1 || n == 49) {
            esc_select_custom_chinese_area[2] = 0x01;
        } else if (n == 2 || n == 50) {
            esc_select_custom_chinese_area[2] = 0x02;
        } else {
            return false;
        }

        return sendMessageToPoint(esc_select_custom_chinese_area);
    }

    /**
     * 67、设置汉字左右字间距。
     *
     * @param n1,n2 设置汉字左侧间距为n1×0.125毫米，设置汉字右侧间距为n2×0.125毫米。
     */
    public boolean esc_set_chinese_space(int n1, int n2) {
        byte[] esc_set_chinese_space = new byte[3];
        esc_set_chinese_space[0] = 0x1C;
        esc_set_chinese_space[1] = 0x43;

        if (n1 < 0 || n1 > 255) {
            esc_set_chinese_space[2] = 0x00;
        } else {
            esc_set_chinese_space[2] = (byte) n1;
        }

        if (n2 < 0 || n2 > 255) {
            esc_set_chinese_space[3] = 0x00;
        } else {
            esc_set_chinese_space[3] = (byte) n2;
        }
        return sendMessageToPoint(esc_set_chinese_space);
    }

    /**
     * 68、读取打印机状态。
     */
    public int getPrinterStatus() {
        esc_read();
        byte[] cmd = new byte[3];
        cmd[0] = 0x10;
        cmd[1] = 0x04;
        cmd[2] = 0x01;

        if (!sendMessageToPoint(cmd)) {
            return 1;
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] buffer1 = esc_read();
        if (buffer1 == null) {
            return -1;
        }
        Log.i("10 04 01", String.valueOf(buffer1[0]));
        if ((buffer1[0] == 0x16) || (buffer1[0] == 0x1E)) {
            if ((buffer1[0] == 0x1E)) {
                cmd[2] = 0x02;
                if (!sendMessageToPoint(cmd)) {
                    return 1;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte[] buffer2 = esc_read();
                if (buffer2 == null) {
                    return -1;
                }
                Log.i("10 04 02脱机", String.valueOf(buffer2[0]));
                if ((buffer2[0] & 0x04) == 0x04) {
                    return 3;
                } else if ((buffer2[0] & 0x20) == 0x20) {
                    return 7;
                }

                cmd[2] = 0x03;
                if (!sendMessageToPoint(cmd)) {
                    return 1;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte[] buffer3 = esc_read();
                if (buffer3 == null) {
                    return -1;
                }
                Log.i("10 04 03 脱机", String.valueOf(buffer3[0]));
                if ((buffer3[0] & 0x04) == 0x04) {
                    return 6;
                } else if ((buffer3[0] & 0x08) == 0x08) {
                    return 4;
                } else if ((buffer3[0] & 0x40) == 0x40) {
                    return 5;
                }
                cmd[2] = 0x04;
                if (!sendMessageToPoint(cmd)) {
                    return 1;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte[] buffer4 = esc_read();
                if (buffer4 == null) {
                    return -1;
                }
                Log.i("10 04 04 脱机", String.valueOf(buffer4[0]));
                if (buffer4[0] == 0x12) {
                    return 0;
                } else if ((buffer4[0] & 0x0C) == 0x0C) {
                    return 8;
                } else if ((buffer4[0] & 0x60) == 0x60) {
                    return 7;
                } else if ((buffer4[0] & 0x80) == 0x80) {
                    return 8;
                }
            } else {
                cmd[2] = 0x02;
                if (!sendMessageToPoint(cmd)) {
                    return 1;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte[] buffer2 = esc_read();
                if (buffer2 == null) {
                    return -1;
                }
                Log.i("10 04 02", String.valueOf(buffer2[0]));
                if (buffer2[0] == 0x12) {
                    cmd[2] = 0x03;
                    if (!sendMessageToPoint(cmd)) {
                        return 1;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byte[] buffer3 = esc_read();
                    if (buffer3 == null) {
                        return -1;
                    }
                    Log.i("10 04 03", String.valueOf(buffer3[0]));
                    if (buffer3[0] == 0x12) {
                        cmd[2] = 0x04;
                        if (!sendMessageToPoint(cmd)) {
                            return 1;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        byte[] buffer4 = esc_read();
                        if (buffer4 == null) {
                            return -1;
                        }
                        Log.i("10 04 04", String.valueOf(buffer4[0]));
                        if (buffer4[0] == 0x12) {
                            return 0;
                        } else if ((buffer4[0] & 0x0C) == 0x0C) {
                            return 8;
                        } else if ((buffer4[0] & 0x60) == 0x60) {
                            return 7;
                        } else if ((buffer4[0] & 0x80) == 0x80) {
                            return 8;
                        }
                    }
                } else if ((buffer2[0] & 0x04) == 0x04) {
                    return 3;
                } else if ((buffer2[0] & 0x20) == 0x20) {
                    return 7;
                }
            }

        }
        return -1;
    }

    /**
     * 6、读取输入流。
     */
    public byte[] esc_read() {
        return receiveMessageFromPoint();
    }

    private byte[] receiveMessageFromPoint() {
        if (myDeviceConnection == null) {
            return null;
        }
//        int inMax = epBulkIn.getMaxPacketSize();
        byte[] tmp = new byte[128];
        int recevieNum = myDeviceConnection.bulkTransfer(epBulkIn, tmp, tmp.length, 3000);
        Log.i("recevieDataNum", String.valueOf(recevieNum));
//        Log.i("inMax", String.valueOf(inMax));
//        Log.i("recevieNum", String.valueOf(recevieNum));
//        Log.i("tmp", tmp.length + Arrays.toString(tmp));
        if (recevieNum < 0) {
            return null;
        }

        return tmp;
    }

    /**
     * 68、退纸指令。
     *
     * @param n 退纸n×0.176mm。
     */
    public boolean esc_out_of_paper(int n) {
        byte[] esc_out_of_paper = new byte[3];
        esc_out_of_paper[0] = 0x1B;
        esc_out_of_paper[1] = 0x4B;

        if (n < 0 || n > 255) {
            esc_out_of_paper[2] = 0x00;
        } else {
            esc_out_of_paper[2] = (byte) n;
        }

        return sendMessageToPoint(esc_out_of_paper);
    }


//    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Toast.makeText(mContext,action,Toast.LENGTH_LONG).show();
//            if (ACTION_USB_PERMISSION.equals(action)) {
//                synchronized (this) {
//                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                        if(device != null){
//                            Toast.makeText(mContext,"true",Toast.LENGTH_LONG).show();
//                            UsbDeviceConnection conn = null;
//                            conn = myUsbManager.openDevice(myUsbDevice);
//                            if (conn == null) {
//                                return;
//                            }
//
//                            if (!conn.claimInterface(usbInterface, true)) {
//                                conn.close();
//                            }
//                            myDeviceConnection = conn;
//                        }
//                    }
//                    else {
//                        Toast.makeText(mContext, "Denied!", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            }
//        }
//    };


}
