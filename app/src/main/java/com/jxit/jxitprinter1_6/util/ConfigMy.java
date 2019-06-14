package com.jxit.jxitprinter1_6.util;

/**
 * 作者：XPZ on 2018/6/6 10:02.
 */
public interface ConfigMy {

    /**
     * 居中并加粗
     */
    String CENTET_BOLD = "1b 61 01 1b 45 01 ";

    /**
     * 居中
     */
    String CENTER = "1b 61 01 ";
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
    String RESET = "1B 40 ";
    /**
     * 加粗
     */
    String BOLD = "1b 45 01 ";
    /**
     * 取消加粗
     */
    String UNBOLD = "1b 45 00 ";
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
     * 打印并回车(非换行)
     */
    String PRINT_ENTER = "0a 1d 56 42 00 ";
    /**
     * 换行
     */
    String END = "0a ";//换行


    /**
     * 正式票中 , 中心返回的类似生成点阵码的 , 字符串
     */
    String 正式票的是编码 = "pjrwulqmwqnuvowqwhln";

    /**
     * 初始打印机,设置行间距,选择字符代码,,设置字符大小,取消文字模式(为打印光感点状区做准备)
     */
    String PRINT_DIAN = "1B 40 1B 33 00 1B 74 01 1D 21 00 1C 2E ";
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
            "地址:呼和浩特市新城区哲理木路2号               "
    };

    String[] strARRs = {
            "1b 24 00 00 83 ",//1
            "1b 24 19 00 83 ",//2
            "1b 24 32 00 83 ",//3
            "1b 24 4d 00 83 ",//4
            "1b 24 64 00 83 ",//5
            "1b 24 7d 00 83 ",//6
            "1b 24 96 00 83 ",//7
            "1b 24 af 00 83 ",//8
            "1b 24 c8 00 83 ",//9
            "1b 24 e1 00 83 ",//10
            "1b 24 fa 00 83 ",//11
            "1b 24 19 01 83 ",//12
            "1b 24 32 01 83 ",//13
            "1b 24 4d 01 83 ",//14
            "1b 24 64 01 83 ",//15
            "1b 24 7d 01 83 ",//16
            "1b 24 96 01 83 ",//17
            "1b 24 af 01 83 ",//18
            "1b 24 c8 01 83 ",//19
            "1b 24 e1 01 83 ",//20
            "1b 24 fa 01 83 " //21
    };

    String my = //一行的点状码
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
                    "0a ";


}
