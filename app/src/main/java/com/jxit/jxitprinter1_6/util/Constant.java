package com.jxit.jxitprinter1_6.util;

public class Constant {
    public static final String end = "\n";
    public static String mBarCodeData = "123456789";
    public static String mBmpPathData = "/storage/sdcard0/DCIM/test.bmp";
    public static String mBmpPath = "/storage/sdcard0/DCIM/";
    public static String m_strCN1 = "正常";
    public static String m_strCN2 = "缺纸";
    public static String m_strCN3 = "异常";
    public static String m_strCN4 = "获取状态失败";
    public static String m_strUS1 = "Normal";
    public static String m_strUS2 = "Out of paper";
    public static String m_strUS3 = "Abnormal";
    public static String m_strUS4 = "Gets status failure";

    public static String TESTlOTTERY_SSQ = "\n"
            + "玩法:双色球                         流水号:245" + end
            + "机号:20180112         5720-0530-7ac2-4489-3658" + end
            + "销售期:20180112            84678有效期:20180112" + end
            + "销售时间:18-01-12  19;25:25          金额:10元" + end;

    public static String TESTlOTTERY_SSQ1 = "\n"
            + "A.03    11    17    26    30    31-15   (1)" + end
            + "B.03    11    14    15    19    26-09   (1)" + end
            + "C.03    04    05    14    26    30-05   (1)" + end
            + "D.03    05    11    15    26    33-07   (1)" + end
            + "E.01    15    16    28    30    32-03   (1)" + end;

    public static String TESTlOTTERY_SSQ2 = "\n"
            + "开奖时间:18-01-12             附加码:44883149" + end
            + "地址:呼和浩特市新城区哲理木路2号               " + end + end;


    // 小票打印文字中英文对照
    public static int ADD_NUM = 1000;
    public static String TITLE_CN = "中国农业银行\n\n" + "办理业务(一)\n\n";
    public static String TITLE_US = "Agricultural Bank China\n\n" + "Transact business (1)\n\n";
    public static String QUEUE_NUMBER = String.valueOf(ADD_NUM) + "\n\n";
    public static String STRDATA_CN = "您前面有 10 人等候，请注意叫号\n\n"
            + "欢迎光临！我们将竭诚为你服务。\n";
    public static String STRDATA_US = "There are 10 people waiting in front of you, please note the number\n\n"
            + "Welcome! We will serve you wholeheartedly.\n";
    public static String TextStr = "0D 0A 20 cd e6 b7 a8 a3 ba c6 df c0 d6 b2 ca 2d b5 a5 ca bd 20 20 20 20 20 20 20 20 20 20 bb fa ba c5 a3 ba 32 32 30 31 30 30 31 30 0d 0a 20 33 39 " +
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
            "02 83 1b 24 20 02 83 0a 0a 1c 26 1d 21 00 1b 74 00 1b 33 1e 0a";

}
