package com.jxit.jxitprinter1_6;

import com.jxit.jxitprinter1_6.util.ConfigMy;
import com.jxit.jxitprinter1_6.util.ToBG18030;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    /**
     * 没有黑标
     */
    String NewCoeda = "1b 61 01 cd e6 b7 a8 3a cb ab c9 ab c7 f2 " +
            "20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 " +
            "c1 f7 cb ae ba c5 3a 31 31 0a bb fa ba c5 3a 31 38 30 30 30 30 30 31 20 20 20 20 20 20 20 20 20 20 " +
            "31 39 32 41 2d 42 34 33 30 2d 37 36 43 33 2d 36 35 39 37 2d 32 44 46 32 0a cf fa ca db c6 da 3a 32 " +
            "30 31 38 30 35 35 20 20 20 20 20 20 20 38 30 35 35 20 20 20 20 20 20 20 d3 d0 d0 a7 c6 da 3a 32 30 " +
            "31 38 30 35 35 0a cf fa ca db ca b1 bc e4 3a 32 30 31 38 2d 30 35 2d 31 35 20 31 34 3a 34 38 3a 30 " +
            "33 20 20 20 20 20 20 20 20 20 20 bd f0 b6 ee 3a 31 30 d4 aa 0a 1b 40 1b 61 01 1b 45 01 0a 30 36 20 " +
            "20 20 30 38 20 20 20 31 31 20 20 20 32 30 20 20 20 32 34 20 20 20 32 36 20 2d 20 30 31 20 20 20 20 " +
            "28 31 29 0a 30 37 20 20 20 30 39 20 20 20 31 32 20 20 20 32 31 20 20 20 32 35 20 20 20 32 38 20 2d " +
            "20 30 31 20 20 20 20 28 31 29 0a 30 34 20 20 20 30 36 20 20 20 30 39 20 20 20 31 38 20 20 20 32 32 " +
            "20 20 20 32 34 20 2d 20 30 31 20 20 20 20 28 31 29 0a 30 38 20 20 20 31 30 20 20 20 31 33 20 20 20 " +
            "32 33 20 20 20 32 36 20 20 20 32 39 20 2d 20 30 31 20 20 20 20 28 31 29 0a 30 31 20 20 20 30 33 20 " +
            "20 20 30 37 20 20 20 31 35 20 20 20 31 38 20 20 20 32 37 20 2d 20 30 31 20 20 20 20 28 31 29 0a 0a " +
            "1b 40 1b 61 01 bf aa bd b1 ca b1 bc e4 3a 32 30 31 38 2d 30 35 2d 31 35 20 20 20 20 20 20 20 20 20 " +
            "20 20 20 20 20 b8 bd bc d3 c2 eb 3a 34 34 38 35 31 32 0a b5 d8 d6 b7 3a c4 da c3 c9 b9 c5 d7 d4 d6 " +
            "ce c7 f8 b8 a3 c0 fb b2 ca c6 b1 b7 a2 d0 d0 b9 dc c0 ed d6 d0 d0 c4 20 20 20 20 20 20 20 20 20 20 " +
            "20 20 20 20 20 20 20 0a 0a 1b 40 1b 33 00 1b 74 01 1d 21 00 1c 2e 1b 61 01 ";
    /**
     * 测试数据
     */
    String NewCoed = "1b 61 01 cd e6 b7 a8 3a cb ab c9 ab c7 f2 " +
            "20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 " +
            "c1 f7 cb ae ba c5 3a 31 31 0a bb fa ba c5 3a 31 38 30 30 30 30 30 31 20 20 20 20 20 20 20 20 20 20 " +
            "31 39 32 41 2d 42 34 33 30 2d 37 36 43 33 2d 36 35 39 37 2d 32 44 46 32 0a cf fa ca db c6 da 3a 32 " +
            "30 31 38 30 35 35 20 20 20 20 20 20 20 38 30 35 35 20 20 20 20 20 20 20 d3 d0 d0 a7 c6 da 3a 32 30 " +
            "31 38 30 35 35 0a cf fa ca db ca b1 bc e4 3a 32 30 31 38 2d 30 35 2d 31 35 20 31 34 3a 34 38 3a 30 " +
            "33 20 20 20 20 20 20 20 20 20 20 bd f0 b6 ee 3a 31 30 d4 aa 0a 1b 40 1b 61 01 1b 45 01 0a 30 36 20 " +
            "20 20 30 38 20 20 20 31 31 20 20 20 32 30 20 20 20 32 34 20 20 20 32 36 20 2d 20 30 31 20 20 20 20 " +
            "28 31 29 0a 30 37 20 20 20 30 39 20 20 20 31 32 20 20 20 32 31 20 20 20 32 35 20 20 20 32 38 20 2d " +
            "20 30 31 20 20 20 20 28 31 29 0a 30 34 20 20 20 30 36 20 20 20 30 39 20 20 20 31 38 20 20 20 32 32 " +
            "20 20 20 32 34 20 2d 20 30 31 20 20 20 20 28 31 29 0a 30 38 20 20 20 31 30 20 20 20 31 33 20 20 20 " +
            "32 33 20 20 20 32 36 20 20 20 32 39 20 2d 20 30 31 20 20 20 20 28 31 29 0a 30 31 20 20 20 30 33 20 " +
            "20 20 30 37 20 20 20 31 35 20 20 20 31 38 20 20 20 32 37 20 2d 20 30 31 20 20 20 20 28 31 29 0a 0a " +
            "1b 40 1b 61 01 bf aa bd b1 ca b1 bc e4 3a 32 30 31 38 2d 30 35 2d 31 35 20 20 20 20 20 20 20 20 20 " +
            "20 20 20 20 20 b8 bd bc d3 c2 eb 3a 34 34 38 35 31 32 0a b5 d8 d6 b7 3a c4 da c3 c9 b9 c5 d7 d4 d6 " +
            "ce c7 f8 b8 a3 c0 fb b2 ca c6 b1 b7 a2 d0 d0 b9 dc c0 ed d6 d0 d0 c4 20 20 20 20 20 20 20 20 20 20 " +
            "20 20 20 20 20 20 20 0a 0a 1b 40 1b 33 00 1b 74 01 1d 21 00 1c 2e 1b 61 01 1b 24 19 00 83 1b 24 32 " +
            "00 83 1b 24 64 00 83 1b 24 7d 00 83 1b 24 96 00 83 1b 24 c8 00 83 1b 24 19 01 83 1b 24 4d 01 83 1b " +
            "24 7d 01 83 1b 24 c8 01 83 1b 24 e1 01 83 1b 24 fa 01 83 0a 1b 24 00 00 83 1b 24 19 00 83 1b 24 4d " +
            "00 83 1b 24 64 00 83 1b 24 af 00 83 1b 24 c8 00 83 1b 24 fa 00 83 1b 24 19 01 83 1b 24 4d 01 83 1b " +
            "24 7d 01 83 1b 24 96 01 83 1b 24 af 01 83 1b 24 fa 01 83 0a 1b 24 19 00 83 1b 24 32 00 83 1b 24 64 " +
            "00 83 1b 24 96 00 83 1b 24 af 00 83 1b 24 e1 00 83 1b 24 fa 00 83 1b 24 19 01 83 1b 24 64 01 83 1b " +
            "24 fa 01 83 0a 1b 24 19 00 83 1b 24 32 00 83 1b 24 7d 00 83 1b 24 e1 00 83 1b 24 7d 01 83 1b 24 96 " +
            "01 83 1b 24 c8 01 83 1b 24 fa 01 83 0a 1b 24 00 00 83 1b 24 19 00 83 1b 24 32 00 83 1b 24 4d 00 83 " +
            "1b 24 64 00 83 1b 24 7d 00 83 1b 24 96 00 83 1b 24 af 00 83 1b 24 c8 00 83 1b 24 e1 00 83 1b 24 fa " +
            "00 83 1b 24 19 01 83 1b 24 32 01 83 1b 24 4d 01 83 1b 24 64 01 83 1b 24 7d 01 83 1b 24 96 01 83 1b " +
            "24 af 01 83 1b 24 c8 01 83 1b 24 e1 01 83 1b 24 fa 01 83 0a";


    String CODE_ARR[] = {
            "玩法:双色球                          流水号:245",
            "机号:15086008          2934-B427-3147-0C50-E7C9",
            "销售期:20180809         96304    有效期:0418059",
            "销售时间:18-08-09  18:09:25           金额:10元",
            "A.06    10    16    20    24    33-13   (1)",
            "B.03    11    14    15    19    26-09   (1)",
            "C.03    04    05    14    26    30-05   (1)",
            "D.06    10    16    19    24    33-16   (1)",
            "E.01    15    16    28    30    32-03   (1)",
            "开奖时间:18-08-09               附加码:44883149",
            "地址:呼和浩特市新城区哲理木路99号               "
    };


    String str =
            "1b 4c " + //选择页模式
                    "1b 57 00 00 00 00 60 01 c0 00 " + //在页模式下设置打印区域
                    "1b 54 00 " +//在页模式下选择打印方向
                    "1d 24 0a 00 " + //页模式下设置绝对垂直打印位置
                    "1b 24 18 00 " +
                    "0a " +
                    "0a " +
                    "B5 D8 D6 B7 3A BA F4 BA CD BA C6 CC D8 CA D0 D0 " +  //数据
                    "C2 B3 C7 C7 F8 D5 DC C0 EF C4 BE C2 B7 32 BA C5 " +
                    "0D 0A CB AB C9 AB C7 F2 30 33 31 C6 DA CE FD C1 " +
                    "D6 B9 F9 C0 D5 36 32 39 37 D5 BE D6 D0 B3 F6 31 " +
                    "30 30 30 CD F2 B4 F3 BD B1 0D 0A " +
                    "1b 57 68 01 00 00 3c 02 c0 00 " + //68 -> 104 准备打印二维码
                    "1d 24 0a 00 " +//页模式下设置绝对垂直打印位置
                    "1d 28 6b 03 00 31 43 04 " + //
                    "1d 28 6b 04 00 31 45 01 " +
                    "1d 28 6b 14 01 31 50 30 " +
                    "70 6a 72 77 75 6c 71 6d 77 71 6e 75 76 6f 77 71 77 68 6c 6e " +
                    "1d 28 6b 03 00 31 51 30 " +
                    "0c ";


    /**
     * 十六进制字符串转换成二进制
     *
     * @param hex 十六进制字符串
     * @return 二进制
     */
    public static String hexStringToBit(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) ((byte) "0123456789ABCDEF".indexOf(achar[pos]) << 4 |
                    (byte) "0123456789ABCDEF".indexOf(achar[pos + 1]));
        }
        byte b;
        StringBuffer stringBuffer = new StringBuffer("");
        for (int i = 0; i < result.length; i++) {
            b = result[i];
            stringBuffer.append("" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                    + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                    + " "
                    + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                    + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1)
                    + " ");
        }
        return stringBuffer.toString();
    }

    /**
     * 将字符串 转换为GB18030国际编码(例:CD E6 B7 A8 )
     *
     * @param code 需要转码的字符串
     * @return GB18030国际编码(例:CD E6 B7 A8)
     */
    public static String strToGB18030(String code) {
        StringBuffer unicode = new StringBuffer();
        byte[] b = new byte[0];
        try {
            b = code.getBytes("GB18030");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(b[i]);
            unicode.append(s.substring(s.length() - 2, s.length()));
            unicode.append(" ");
        }
        return unicode.toString();
    }

    /**
     * 测试二维么码
     *
     * @throws Exception
     */
    @Test
    public void testStr2QRcode() throws Exception {
//        System.out.println(strToGB18030("pjrwulqmwqnuvowqwhln"));
//        System.out.println(NewCoeda);
        System.out.println(printPatrolResult());

    }


    /**
     * 测试
     * printPatrolResult
     */
    private String printPatrolResult() {

        /*
        * 使用指令和BG18030 码打印票面
        * */
        StringBuffer stringBuffer = new StringBuffer(ConfigMy.END);
        for (int i = 0; i < CODE_ARR.length; i++) {

            if (i >= 4 && i <= 8) {
                if (i == 4) {
                    stringBuffer.append(ConfigMy.END).append(ConfigMy.CENTET_BOLD).append(ToBG18030.strToGB18030(CODE_ARR[i])).append(ConfigMy.END);
                } else {
                    stringBuffer.append(ConfigMy.CENTET_BOLD).append(ToBG18030.strToGB18030(CODE_ARR[i])).append(ConfigMy.END);
                }
            } else {
                if (i == 9) {
                    stringBuffer.append(ConfigMy.END).append(ConfigMy.UNBOLD).append(ToBG18030.strToGB18030(CODE_ARR[i])).append(ConfigMy.END);
                } else {
                    if (i != 10) {
                        stringBuffer.append(ConfigMy.CENTER).append(ToBG18030.strToGB18030(CODE_ARR[i])).append(ConfigMy.END);
                    }
                }
            }
        }


        if (false) {
            stringBuffer
                    .append("1f 07 48 01 07 " +//在开启页模式之前添加,定制版本几x几大小(不够自动填充)
                            "1b 4c " +//选择页模式
                            "1b 57 00 00 00 00 60 01 c0 00 " +//在页模式下设置打印区域
                            "1b 54 00 " +//在页模式下选择打印方向
                            "1d 24 0a 00 " +//设置绝对打印位置
                            "1b 24 18 00 "
                    )

                    .append(ConfigMy.END)
                    .append(ConfigMy.UNBOLD)
                    .append(ToBG18030.strToGB18030("  地址:呼和浩特市新城区哲理木路88号"))
                    .append(ConfigMy.END)
                    .append(ToBG18030.strToGB18030("  双色球031期锡林郭勒6297站中出1000万大奖"));

            /*打印二维码*/
            stringBuffer
                    .append(ConfigMy.END)
                    .append(ConfigMy.END)
                    .append(ConfigMy.END)
                    .append("1b 57 68 01 00 00 3c 02 c0 00 " + //68 -> 104 准备打印二维码
                            "1d 24 0a 00 " +                //页模式下设置绝对垂直打印位置
                            "1d 28 6b 03 00 31 43 04 " +    //6b之后的 四位
                            "1d 28 6b 03 00 31 45 01 " +    //6b之后的 四位
                            "1d 28 6b 17 00 31 50 30 " +    //17 00 这个是二维码字符的长度 计算长度时要在原有长度上加3
                            "70 6a 72 77 75 6c 71 6d 77 71 6e 75 76 6f 77 71 77 68 6c 6e " +//数据
                            "1d 28 6b 03 00 31 51 30 "+
                            "0c ");
        } else {
            stringBuffer.append(ConfigMy.UNBOLD)
                    .append(ToBG18030.strToGB18030("地址:呼和浩特市新城区哲理木路88号              "))
                    .append(ConfigMy.END)
                    .append(ToBG18030.strToGB18030("双色球031期锡林郭勒6297站中出1000万大奖        "));
            /*打印条形码*/
            stringBuffer
                    .append(ConfigMy.END)
                    .append(ConfigMy.END)
                    .append(
                            //"1d 4c 18 03 " +//这只左侧空白量
                            "1d 28 6b 03 00 30 41 07 " + //设置PDF417宽度 07
                                    "1d 28 6b 03 00 30 42 0a " + //设置PDF417高度 0a
                                    "1d 28 6b 03 00 30 43 03 " + //设置其中黑块大小
                                    "1d 28 6b 03 00 30 44 03 " +
                                    "1d 28 6b 04 00 30 45 30 33 " +
                                    "1d 28 6b 17 00 30 50 30 " + //设定打印字符长度 同理 额外加3
                                    "70 6a 72 77 75 6c 71 6d 77 71 6e 75 76 6f 77 71 77 68 6c 6e " +//数据
                                    "1d 28 6b 03 00 30 51 30");

        }

/*打印点阵*/
//        stringBuffer.append(ConfigMy.END)
//                .append(ConfigMy.PRINT_DIAN)
//                .append(ConfigMy.CENTER)
//                .append(ToBG18030.hexStrToBG18030("2934-B427-3147-0C50-E7C9"))
//                .append(ConfigMy.PRINT_ENTER)
//                .append(ConfigMy.RESET);
        return stringBuffer.toString();
    }


    @Test
    public void addition_isCorrect() throws Exception {
        String str = "2934-B427-3147-0C50-E7C9";
        String replace = str.replace("-", "");
        StringBuffer buffer = new StringBuffer("");
        for (int i = 0; i < replace.length(); i++) {
            buffer.append(replace.charAt(i));
        }
        String daoString = new StringBuffer(buffer).reverse().toString();
        String bitStr = hexStringToBit(daoString);

        ArrayList<String> arrL1 = new ArrayList<>();//第一行
        ArrayList<String> arrL2 = new ArrayList<>();//第二行
        ArrayList<String> arrL3 = new ArrayList<>();//第三行
        ArrayList<String> arrL4 = new ArrayList<>();//第四行

        String[] split = bitStr.split(" ");
        for (String code : split) {
            arrL1.add(code.substring(0, 1));
            arrL2.add(code.substring(1, 2));
            arrL3.add(code.substring(2, 3));
            arrL4.add(code.substring(3, 4));
        }
        String s = "";
        s = Splicing_A_Line(arrL1) +
                Splicing_A_Line(arrL2) +
                Splicing_A_Line(arrL3) +
                Splicing_A_Line(arrL4);

        System.out.println(s);
    }

    private String Splicing_A_Line(ArrayList<String> arrL1) {
        StringBuffer stringBuffer = new StringBuffer("");
        for (int i = 0; i < arrL1.size(); i++) {
            switch (arrL1.get(i)) {
                case "1":
                    stringBuffer.append(ConfigMy.strARRs[i]);
                    break;
                case "0":

                    break;
                default:
                    break;
            }
        }
        return stringBuffer.append(ConfigMy.strARRs[20] + ConfigMy.END).toString();
    }

}