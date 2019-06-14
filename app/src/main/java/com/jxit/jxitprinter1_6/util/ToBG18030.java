package com.jxit.jxitprinter1_6.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * 作者：XPZ on 2018/5/11 19:19.
 */
public class ToBG18030 {


    private static String ONE_LINE = //一行的点状码
            "1b 24 00 00 83 1b 24 19 00 83 1b 24 32 00 83 1b 24 4d 00 83 1b 24 64 00 83 " +
            "1b 24 7d 00 83 1b 24 96 00 83 1b 24 af 00 83 1b 24 c8 00 83 1b 24 e1 00 83 " +
            "1b 24 fa 00 83 1b 24 19 01 83 1b 24 32 01 83 1b 24 4d 01 83 1b 24 64 01 83 " +
            "1b 24 7d 01 83 1b 24 96 01 83 1b 24 af 01 83 1b 24 c8 01 83 1b 24 e1 01 83 " +
            "1b 24 fa 01 83 0a ";

/*
* 一下为 点状矩阵
* */
    /**
     * 准备一行中的点状码
     */
    private static String[] strARRs = {
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
//        System.out.println("转换为GB18030:: " + unicode.toString().toUpperCase());
        return unicode.toString();
    }

    /**
     * @param str 需要转换成点状矩阵码 的十六位进制 字符串
     * @return 对应的BG18030码
     */
    public static String hexStrToBG18030(String str) {
        String replace = str.replace("-", "");
        StringBuffer buffer = new StringBuffer("");
        for (int i = 0; i < replace.length(); i++) {
            buffer.append(replace.charAt(i));
        }
        String daoString = new StringBuffer(buffer).reverse().toString();
        String bitStr = hexStringToBit(daoString);
//        System.out.println("转为二进制:: " + bitStr);

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
        String Point_code = "";
        Point_code = Splicing_A_Line(arrL4) +
                Splicing_A_Line(arrL3) +
                Splicing_A_Line(arrL2) +
                Splicing_A_Line(arrL1) +
                ONE_LINE
        ;
        return Point_code;
    }

    /**
     * 十六进制字符串转换成二进制
     *
     * @param hex 十六进制字符串
     * @return 二进制
     */
    private static String hexStringToBit(String hex) {
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
            stringBuffer.append("" +
                    (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1) +
                    (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1) + " " +
                    (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1) +
                    (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1) + " "
            );
        }
        return stringBuffer.toString();
    }

    /**
     * 拼接对应的GB18030码
     *
     * @param arrL1
     * @return
     */
    private static String Splicing_A_Line(ArrayList<String> arrL1) {
        StringBuffer stringBuffer = new StringBuffer("");
        for (int i = 0; i < arrL1.size(); i++) {
            switch (arrL1.get(i)) {
                case "1":
                    stringBuffer.append(strARRs[i]);
                    break;
                default:
                    break;
            }
        }
        return stringBuffer.append(ConfigMy.strARRs[20] + ConfigMy.END).toString();
    }

}
