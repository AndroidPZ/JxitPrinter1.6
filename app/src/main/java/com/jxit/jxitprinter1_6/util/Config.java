package com.jxit.jxitprinter1_6.util;

/**
 * 作者：XPZ on 2018/5/9 10:09.
 */
public interface Config {

    /**
     * 居中并加粗
     */
    String CENTET_BOLD = "1b 61 01 1b 45 01 ";

    /**
     * 居中
     */
    String CENTER = "center";
    /**
     * 左对齐
     */
    String CENTERLEFT = "centerLeft";
    /**
     * 右对齐
     */
    String CENTERRIGHT = "centerRight";
    /**
     * 重置打印机
     */
    String RESET = "reset";
    /**
     * 加粗
     */
    String BOLD = "bold";
    /**
     * 宽的为2的下划线
     */
    String UNDERLINE = "underline";
    /**
     * 取消下划线
     */
    String CANCELUNDERLINE = "cancelUnderline";
    /**
     * 文字2倍大小
     */
    String CHARACTER_SIZE2 = "character_size2";
    /**
     * 文字3倍大小
     */
    String CHARACTER_SIZE3 = "character_size3";
    /**
     * 文字宽度2倍大小
     */
    String CHARACTER_SIZE20 = "character_size20";
    /**
     * 文字宽度3倍大小
     */
    String CHARACTER_SIZE30 = "character_size30";
    /**
     * 文字高度2倍大小
     */
    String CHARACTER_SIZE22 = "character_size22";
    /**
     * 文字高度3倍大小
     */
    String CHARACTER_SIZE33 = "character_size33";
    /**
     * 字体A~B~C~D
     */
    String FONTA = "fonta";
    String FONTB = "fontb";
    String FONTC = "fontc";
    String FONTD = "fontd";
    /**
     * 顺时针旋转90°~180°~270°
     */
    String ROTATE90 = "rotate90";
    String ROTATE180 = "rotate180";
    String ROTATE270 = "rotate270";
    /**
     * 选择/取消黑白反显打印模式
     */
    String BLACK_WHITE_REVERSE_TRUE = "black_white_reverse_true";
    String BLACK_WHITE_REVERSE_FALSE = "black_white_reverse_false";
    /**
     * 打印并回车(非换行)
     */
    String PRINT_ENTER = "print_enter";
    String CODE_ARR_STR[] = {
            "玩法:双色球                          流水号:245",
            "机号:20180112          5720-0530-7ac2-4489-3658",
            "销售期:20180112            84678有效期:20180112",
            "销售时间:18-01-12  19:25:25           金额:10元",
            "A.03    11    17    26    30    31-15   (1)",
            "B.03    11    14    15    19    26-09   (1)",
            "C.03    04    05    14    26    30-05   (1)",
            "D.03    05    11    15    26    33-07   (1)",
            "E.01    15    16    28    30    32-03   (1)",
            "开奖时间:18-01-12               附加码:44883149",
            "地址:呼和浩特市新城区哲理木路2号               "};


    String my = "1B 40 1B 33 00 1B 74 01 1D 21 00 1C 2E " +//初始打印机,设置字符编码,,字符大小,取消文字模式(为打印光感点状区做准备)
            "1b 61 01 " +//居中
            "1b 24 00 00 83 " +//1
            "1b 24 19 00 83 " +//2
            "1b 24 32 00 83 " +//3
            "1b 24 4d 00 83 " +//4
            "1b 24 64 00 83 " +//5
            "1b 24 7d 00 83 " +//6
            "1b 24 96 00 83 " +//7
            "1b 24 af 00 83 " +//8
            "1b 24 c8 00 83 " +//9
            "1b 24 e1 00 83 " +//10
            "1b 24 fa 00 83 " +//11

            "1b 24 19 01 83 " +//12
            "1b 24 32 01 83 " +//13
            "1b 24 4d 01 83 " +//14
            "1b 24 64 01 83 " +//15
            "1b 24 7d 01 83 " +//16
            "1b 24 96 01 83 " +//17
            "1b 24 af 01 83 " +//18
            "1b 24 c8 01 83 " +//19
            "1b 24 e1 01 83 " +//20
            "1b 24 fa 01 83 " +//21
            "0a " +

            "1b 24 00 00 83 " +//1
            "1b 24 19 00 83 " +//2
            "1b 24 32 00 83 " +//3
            "1b 24 4d 00 83 " +//4
            "1b 24 64 00 83 " +//5
            "1b 24 7d 00 83 " +//6
            "1b 24 96 00 83 " +//7
            "1b 24 af 00 83 " +//8
            "1b 24 c8 00 83 " +//9
            "1b 24 e1 00 83 " +//10
            "1b 24 fa 00 83 " +//11

            "1b 24 19 01 83 " +//12
            "1b 24 32 01 83 " +//13
            "1b 24 4d 01 83 " +//14
            "1b 24 64 01 83 " +//15
            "1b 24 7d 01 83 " +//16
            "1b 24 96 01 83 " +//17
            "1b 24 af 01 83 " +//18
            "1b 24 c8 01 83 " +//19
            "1b 24 e1 01 83 " +//20
            "1b 24 fa 01 83 " +//21
            "0a " +

            "1b 24 00 00 83 " +//1
            "1b 24 19 00 83 " +//2
            "1b 24 32 00 83 " +//3
            "1b 24 4d 00 83 " +//4
            "1b 24 64 00 83 " +//5
            "1b 24 7d 00 83 " +//6
            "1b 24 96 00 83 " +//7
            "1b 24 af 00 83 " +//8
            "1b 24 c8 00 83 " +//9
            "1b 24 e1 00 83 " +//10
            "1b 24 fa 00 83 " +//11

            "1b 24 19 01 83 " +//12
            "1b 24 32 01 83 " +//13
            "1b 24 4d 01 83 " +//14
            "1b 24 64 01 83 " +//15
            "1b 24 7d 01 83 " +//16
            "1b 24 96 01 83 " +//17
            "1b 24 af 01 83 " +//18
            "1b 24 c8 01 83 " +//19
            "1b 24 e1 01 83 " +//20
            "1b 24 fa 01 83 " +//21
            "0a " +

            "1b 24 00 00 83 " +//1
            "1b 24 19 00 83 " +//2
            "1b 24 32 00 83 " +//3
            "1b 24 4d 00 83 " +//4
            "1b 24 64 00 83 " +//5
            "1b 24 7d 00 83 " +//6
            "1b 24 96 00 83 " +//7
            "1b 24 af 00 83 " +//8
            "1b 24 c8 00 83 " +//9
            "1b 24 e1 00 83 " +//10
            "1b 24 fa 00 83 " +//11

            "1b 24 19 01 83 " +//12
            "1b 24 32 01 83 " +//13
            "1b 24 4d 01 83 " +//14
            "1b 24 64 01 83 " +//15
            "1b 24 7d 01 83 " +//16
            "1b 24 96 01 83 " +//17
            "1b 24 af 01 83 " +//18
            "1b 24 c8 01 83 " +//19
            "1b 24 e1 01 83 " +//20
            "1b 24 fa 01 83 " +//21
            "0a " +

            "1b 24 00 00 83 " +//1
            "1b 24 19 00 83 " +//2
            "1b 24 32 00 83 " +//3
            "1b 24 4d 00 83 " +//4
            "1b 24 64 00 83 " +//5
            "1b 24 7d 00 83 " +//6
            "1b 24 96 00 83 " +//7
            "1b 24 af 00 83 " +//8
            "1b 24 c8 00 83 " +//9
            "1b 24 e1 00 83 " +//10
            "1b 24 fa 00 83 " +//11

            "1b 24 19 01 83 " +//12
            "1b 24 32 01 83 " +//13
            "1b 24 4d 01 83 " +//14
            "1b 24 64 01 83 " +//15
            "1b 24 7d 01 83 " +//16
            "1b 24 96 01 83 " +//17
            "1b 24 af 01 83 " +//18
            "1b 24 c8 01 83 " +//19
            "1b 24 e1 01 83 " +//20
            "1b 24 fa 01 83 " +//21
            "0a 1d 56 42 00" //回车并切纸
            ;


    /*============================================新打印机===============================================*/
    String heiKuai = "1B 33 00 1B 74 01 1D 21 10 1C 2E " +
            "1B 24 0E 00 83 1B 24 33 00 83 1B 24 65 00 83 1B 24 97 00 83 1B 24 CA 00 83 1B 24 FD 00 83 1B 24 30 01 83 1B 24 63 01 83 1B 24 95 01 83 1B 24 C8 01 83 1B 24 FB 01 83 1B 24 2E 02 83 1B 24 61 02 83 0A " +
            "1B 24 0E 00 83 1B 24 33 00 83 1B 24 65 00 83 1B 24 97 00 83 1B 24 CA 00 83 1B 24 FD 00 83 1B 24 30 01 83 1B 24 63 01 83 1B 24 95 01 83 1B 24 C8 01 83 1B 24 FB 01 83 1B 24 2E 02 83 1B 24 62 02 83 0A " +
            "1B 24 0E 00 83 1B 24 33 00 83 1B 24 65 00 83 1B 24 97 00 83 1B 24 CA 00 83 1B 24 FD 00 83 1B 24 30 01 83 1B 24 63 01 83 1B 24 95 01 83 1B 24 C8 01 83 1B 24 FB 01 83 1B 24 2E 02 83 1B 24 63 02 83 0A " +
            "1B 24 0E 00 83 1B 24 33 00 83 1B 24 65 00 83 1B 24 97 00 83 1B 24 CA 00 83 1B 24 FD 00 83 1B 24 30 01 83 1B 24 63 01 83 1B 24 95 01 83 1B 24 C8 01 83 1B 24 FB 01 83 1B 24 2E 02 83 1B 24 64 02 83 0A " +
            "1B 24 0E 00 83 1B 24 33 00 83 1B 24 65 00 83 1B 24 97 00 83 1B 24 CA 00 83 1B 24 FD 00 83 1B 24 30 01 83 1B 24 63 01 83 1B 24 95 01 83 1B 24 C8 01 83 1B 24 FB 01 83 1B 24 2E 02 83 1B 24 65 02 83 0A " +
            "1C 26 1D 21 00 1B 74 00 1B 33 1E 0A 0A 1D 56 42 00 ";

    /**
     * 河北彩票 PDF417_QR
     */
    String HeBeiCaiPiao =
            //实时状态传送
            "10 04 01 " + //传送打印机状态
                    "10 04 02 " + //传送脱机状态
                    "10 04 03 " + //传送错误状态
                    "10 04 04 " + // 传送卷纸传感器状态
                    "10 04 01 " +
                    "10 04 02 " +
                    "10 04 03 " +
                    "10 04 04 " +

                    "1B 33 1E " +
                    "1B 24 69 00 " +
                    "1D 21 00 " +
                    "42 39 46 33 2D 38 35 46 41 2D 36 36 39 33 2D 32 46 38 36 2D 44 33 43 32 2D 35 43 34 45 2D 46 44 30 44 0A" +
                    "1B 24 61 00 " +
                    "1D 21 11 " +
                    "D6 D0 B9 FA B8 A3 C0 FB B2 CA C6 B1 3C CB AB C9 AB C7 F2 3E 0A " +
                    "1B 24 2D 00 " +
                    "1D 21 00 " +
                    "42 30 30 31 2F B5 A5 CA BD 20 32 30 31 36 2E 31 32 2E 30 36 2D 31 34 3A 31 37 3A 34 33 20 32 30 31 36 30 32 38 C6 DA 0A " +
                    "1B 24 2D 00 " +
                    "1D 21 00 " +
                    "D5 BE BA C5 3A 31 33 30 31 30 30 30 31 20 B2 D9 D7 F7 D4 B1 3A 31 20 C1 F7 CB AE BA C5 3A 39 20 C6 DA 3A 31 20 B1 B6 3A 31 0A " +
                    "1B 24 2D 00 " +
                    "1D 21 00 A2 D9 20 " +
                    "1D 21 10 31 36 " +
                    "1D 21 00 20 " +
                    "1D 21 10 32 32 " +
                    "1D 21 00 20 " +
                    "1D 21 10 32 34 " +
                    "1D 21 00 20 " +
                    "1D 21 10 32 36 " +
                    "1D 21 00 20 " +
                    "1D 21 10 33 31 " +
                    "1D 21 00 20 " +
                    "1D 21 10 33 32 " +
                    "1D 21 00 2D " +
                    "1D 21 10 31 32 " +
                    "1D 21 00 20 " +
                    "0A " +
                    "1B 24 2D 00 " +
                    "1D 21 00 A2 DA 20 " +
                    "1D 21 10 30 33 " +
                    "1D 21 00 20 " +
                    "1D 21 10 32 30 " +
                    "1D 21 00 20 " +
                    "1D 21 10 32 33 " +
                    "1D 21 00 20 " +
                    "1D 21 10 32 35 " +
                    "1D 21 00 20 " +
                    "1D 21 10 33 30 " +
                    "1D 21 00 20 " +
                    "1D 21 10 33 31 " +
                    "1D 21 00 2D " +
                    "1D 21 10 31 31 " +
                    "1D 21 00 20 " +
                    "0A " +
                    "1B 24 2D 00 " +
                    "1D 21 00 A2 DB 20 " +
                    "1D 21 10 30 34 " +
                    "1D 21 00 20 " +
                    "1D 21 10 30 36 " +
                    "1D 21 00 20 " +
                    "1D 21 10 30 37 " +
                    "1D 21 00 20 " +
                    "1D 21 10 32 31 " +
                    "1D 21 00 20 " +
                    "1D 21 10 32 32 " +
                    "1D 21 00 20 " +
                    "1D 21 10 33 31 " +
                    "1D 21 00 2D " +
                    "1D 21 10 30 33 " +
                    "1D 21 00 20 " +
                    "0A " +
                    "1B 24 2D 00 " +
                    "1D 21 00 A2 DC 20 " +
                    "1D 21 10 30 35 " +
                    "1D 21 00 20 " +
                    "1D 21 10 31 31 " +
                    "1D 21 00 20 " +
                    "1D 21 10 31 36 " +
                    "1D 21 00 20 " +
                    "1D 21 10 32 32 " +
                    "1D 21 00 20 " +
                    "1D 21 10 32 37 " +
                    "1D 21 00 20 " +
                    "1D 21 10 32 38 " +
                    "1D 21 00 2D " +
                    "1D 21 10 30 32 " +
                    "1D 21 00 20 0A 1B 24 2D 00 " +
                    "1D 21 00 A2 DD 20 " +
                    "1D 21 10 30 32 " +
                    "1D 21 00 20 " +
                    "1D 21 10 30 38 " +
                    "1D 21 00 20 " +
                    "1D 21 10 31 32 " +
                    "1D 21 00 20 " +
                    "1D 21 10 32 31 " +
                    "1D 21 00 20 " +
                    "1D 21 10 33 30 " +
                    "1D 21 00 20 " +
                    "1D 21 10 33 33 " +
                    "1D 21 00 2D " +
                    "1D 21 10 31 34 " +
                    "1D 21 00 20 0A 1B 24 2D 00 " +
                    "1D 21 00 BF AA BD B1 CA B1 BC E4 3A 32 30 31 36 2F 31 32 2F 30 33 20 20 20 C3 E6 B6 EE 3A 31 30 D4 AA 0A 0A 1B 24 2D 00 " +
                    "1D 21 00 D5 BE B5 D8 D6 B7 3A 64 66 73 64 66 73 64 32 0A 1B 24 2D 00 " +
                    "1D 21 00 D1 E9 C6 B1 C2 EB 3A " +
                    "1D 21 00 43 38 36 42 34 32 36 38 2D 32 37 39 34 39 42 45 35 2D 38 32 31 44 45 37 44 35 2D 33 36 30 34 32 41 46 43 0A 1B 33 1E 1B 4C 18 1B 57 00 00 00 00 34 03 A5 00 1B 4D 00 1B 33 18 1B 24 28 00 1D 28 6B 03 00 30 41 04 1D 28 6B 03 00 30 42 10 1D 28 6B 03 00 30 44 03 1D 28 6B 03 00 30 43 03 1D 28 6B 04 00 30 45 30 31 1D 28 6B 47 00 30 50 30 36 46 38 33 39 41 31 32 45 37 46 37 46 41 34 32 45 31 31 41 44 42 45 37 30 31 41 30 39 30 46 39 41 33 44 44 42 36 37 35 46 37 42 39 45 43 44 45 44 36 39 45 45 31 44 44 41 37 41 41 35 45 32 37 36 43 30 31 1D 28 6B 03 00 30 51 30 1D 24 0F 00 1B 24 E0 01 1D 28 6B 04 00 31 41 32 00 1D 28 6B 03 00 31 43 04 1D 28 6B 03 00 31 45 31 1D 28 6B 35 00 31 50 30 34 36 30 43 37 41 30 35 39 39 36 43 44 30 37 39 32 43 33 44 41 33 42 31 30 32 46 32 3B 45 43 46 45 46 46 46 45 3B 43 3B 44 46 45 39 46 44 37 3B 36 3B 1D 28 6B 03 00 31 51 30 1B 33 1E 0A 0A 0C " +
                    "1D 56 42 00 ";//切纸

    /**
     * 四川二维码彩票(QR)
     */
    String SiChuanQR =
            "10 04 01 " + //实时状态传送
                    "10 04 02 " + //实时状态传送
                    "10 04 03 " + //实时状态传送
                    "10 04 04 " + //实时状态传送
                    "07 01 02 F1 24 75 7F 56 " +
                    "1B 33 1C " + //设置行间距
                    "1B 24 69 00 " + "1D 21 00 " +//设置绝对打印位置设置字符大小
                    "31 33 35 44 2D 34 36 33 30 2D 30 37 43 35 2D 42 45 35 37 2D 42 42 36 36 2D 35 41 30 41 2D 34 39 30 41 " + //上面的字符串
                    "0A " + //打印并换行
                    "1B 24 C3 00 " + "1D 21 01 " +//设置绝对打印位置 和 字符大小
                    "D6 D0 B9 FA B8 A3 C0 FB B2 CA C6 B1 20 20 CB AB C9 AB C7 F2 0A " + //中国福利彩票并换行
                    "1B 24 91 00 " + "1D 21 00 " +//设置绝对打印位置 和 字符大小
                    "CB C4 B4 A8 CA A1 B8 A3 C0 FB B2 CA C6 B1 B7 A2 D0 D0 D6 D0 D0 C4 B3 D0 CF FA 0A " +
                    "1B 24 2D 00 1D 21 00 " +//设置绝对打印位置 和 字符大小
                    "32 30 31 37 30 30 37 C6 DA 20 53 54 3A 35 31 30 31 30 30 30 31 20 53 4E 3A 33 20 28 B5 A5 CA BD 29 0A " +//日期行
                    "1B 24 2D 00 1D 21 11 " +//设置绝对打印位置 和 字符大小
                    "5B C5 E0 D1 B5 C4 A3 CA BD 5D 20 B4 CB C6 B1 CE DE D0 A7 21 21 21 0A " + // 此票无效行
                    "1B 24 2D 00 1D 21 00 " +//设置绝对打印位置 和 字符大小
                    "CF FA CA DB CA B1 BC E4 3A 32 30 31 33 2F 30 37 2F 31 36 2D 30 38 3A 34 30 3A 30 36 20 20 1D 21 00 BF AA BD B1 CA B1 BC E4 3A 30 31 2F 32 34 0A 1B 24 2D 00 1D 21 00 BD F0 B6 EE 3A 31 30 D4 AA 20 20 D5 BE B5 D8 D6 B7 3A B2 E2 CA D4 D5 BE A3 AC C7 EB D7 F6 D5 BE B5 D8 D6 B7 B8 FC D0 C2 0A 1B 24 2D 00 1D 21 00 B5 DA 32 30 31 36 30 35 32 C6 DA BF AA BD B1 BA C5 C2 EB 3A 20 30 31 20 30 32 20 30 33 20 30 34 20 30 35 20 30 36 2B 30 37 0D 0A 1B 24 2D 00 1D 21 00 D1 E9 C6 B1 C2 EB 3A 1D 21 00 34 36 46 43 45 44 44 44 2D 38 41 46 41 41 35 44 31 2D 37 42 31 42 31 42 30 33 2D 32 41 34 33 30 31 39 30 0A 1B 24 2D 00 1D 21 00 BF AA BD B1 BA C5 C2 EB B2 E9 D1 AF 3A 20 68 74 74 70 3A 2F 2F 77 77 77 2E 73 63 66 6C 63 70 2E 63 6F 6D 0A " +

                    "1B 33 00 1B 4C " + //设置行间距 选择页模式
                    "18 " + //页模式下取消打印数据
                    "1B 57 00 00 00 00 A0 02 DC 00 " +//在页模式下设置打印区域
                    "1D 24 16 00 " + //页模式下设置绝对垂直打印位置
                    "1B 24 28 00 " +
                    "1D 28 6B 03 00 30 41 03 " +
                    "1D 28 6B 03 00 30 42 1E " +
                    "1D 28 6B 03 00 30 44 02 " +
                    "1D 28 6B 03 00 30 43 03 " +
                    "1D 28 6B 04 00 30 45 30 33 " +
                    "1D 28 6B 47 00 30 50 30 31 43 38 39 45 39 36 35 45 42 46 34 38 46 33 33 45 37 31 42 41 31 45 31 37 31 41 33 39 44 46 38 41 35 41 43 43 33 37 31 46 37 42 42 45 38 44 41 41 34 45 33 45 31 44 38 41 37 41 41 35 45 32 37 38 39 34 35 " +
                    "1D 28 6B 03 00 30 51 30 " +

                    "1D 24 16 00 " +
                    "1B 24 BD 01 " +
                    "1D 28 6B 04 00 31 41 32 00 " + //选择QR码类型
                    "1D 28 6B 03 00 31 43 04 " + //设置QR条码的模块大小
                    "1D 28 6B 03 00 31 45 31 " + //选择QR条码的纠错等级
                    "1D 28 6B 79 00 31 50 30 " + //下载QR码数据到符号存储区
                    "44 3A 35 31 30 30 30 30 35 31 30 31 30 30 30 31 03 9F C1 F0 A9 1B 67 40 E8 98 50 4C AC AA F0 CB A5 F2 C8 C5 E4 39 60 39 64 9F B4 66 61 25 88 A2 25 49 1C 78 2D A5 57 A7 65 13 CC 5B CA 5F B5 CA 71 0C 10 23 25 E3 70 29 B6 77 6C 9F 49 38 B1 25 1E 91 F9 56 01 03 AF F4 A0 28 EB A2 CC 46 F0 01 42 1E B5 6E 7B ED DF 73 3F E3 28 5B F6 E2 CE 08 87 A1 05 1B 33 E5 " +
                    "1D 28 6B 03 00 31 51 30 0C " + //打印QR条码存储区中的符号数据
                    "1B 33 1C " +  //设置行间距
                    "0A 0A " +
                    "1D 56 42 00 02 ";//选择切纸模式并切纸


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

}
