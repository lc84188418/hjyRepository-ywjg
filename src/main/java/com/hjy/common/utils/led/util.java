package com.hjy.common.utils.led;

import javax.swing.JDialog;
import javax.swing.JLabel;

public class util {
    /**
     * 显示信息
     * 
     * @param msg
     */
    static void errorMessage(String msg, String title) {
        JDialog dialog = new JDialog(appConfig.mainFrame, title, true);
        JLabel label = new JLabel(msg);
        dialog.add(label);
        dialog.setSize(300, 200);
        dialog.setVisible(true);
    }

    /**
     * 连接两个字节数组
     * 
     * @param bysource1
     * @param bysource2
     * @return
     */
    public static byte[] bytesconcat(byte[] bysource1, byte[] bysource2) {
        if (bysource1 == null){
            return bysource2;
        }
        if (bysource2 == null){
            return bysource1;
        }
        byte[] bys = new byte[bysource1.length + bysource2.length];
        for (int i = 0; i < bysource1.length; i++) {
            bys[i] = bysource1[i];
        }
        for (int i = 0; i < bysource2.length; i++) {
            bys[i + bysource1.length] = bysource2[i];
        }
        return bys;
    }

    /**
     * 字符串转字节数组
     * 
     * @param string
     * @return
     */
    public static byte[] stringToBytes(String string) {
        string = string.replace(" ", "");
        if (string == null || string.length() <= 0){
            return null;
        }
        byte[] bys = new byte[string.length() / 2];
        for (int i = 0, j = 0; i < string.length(); i += 2, j++) {
            bys[j] = (byte) Integer.parseInt(string.substring(i, i + 2), 16);
        }
        return bys;
    }

    /**
     * 字节数组转换成十六进制字符串
     *
     * @param buffer
     * @return
     */
    public static String bytesToString(byte[] buffer) {
        if (buffer == null || buffer.length <= 0) {
            return "";
        }
        String strData = "";
        for (byte by : buffer) {
            strData += intToString((int) by);
        }
        return strData;
    }

    public static String intToString(int iData){
        return intToString(iData, 1);
    }

    /**
     * 整数转换成指定长度的十六进制字符串
     *
     * @param iData
     * @param iLen
     * @return
     */
    public static String intToString(int iData, int iLen) {
        String strData = Integer.toHexString(iData);
        while (strData.length() < (iLen * 2)) {
            strData = "0" + strData;
        }
        if (strData.length() > (iLen * 2)) {
            strData = strData.substring(strData.length() - 2, strData.length());
        }
        return strData.toUpperCase();
    }
}
