package com.hjy.common.utils.led;

import gnu.io.SerialPort;
import org.apache.commons.lang.StringUtils;

import javax.swing.JDialog;
import javax.swing.JLabel;
import java.io.UnsupportedEncodingException;

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
     * 字符串转16进制
     *
     * @param string
     * @return
     */
    public static String stringTo16(String string) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        byte[] b = string.getBytes("gb2312");
        for(int i=0;i<b.length;i++){
            String hex = Integer.toHexString(b[i]& 0xFF);
            if(hex.length() == 1){
                hex = "0"+hex;
            }
            sb.append(hex+" ");
        }
        return sb.toString().toUpperCase();
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
    /**
     * 数据长度高字节计算
     *
     * @param msg
     * @return
     */
    public static String byteLength(String msg) {
        String[] s = msg.split(" ");
        int num = 7+s.length;
        return intToString(num,1);
    }
    /**
     * 校验和计算
     *  @param data
     * @return
     */
    public static String jyh(String data) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        data = data.replace(" ","");
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        len = hex.length();
        if (len < 2) {
            hex = "0" + hex;
        }
        return hex.toUpperCase();
    }
    /**
     * 发送消息总接口
     * @param cardPort led屏地址
     * @param msg 显示内容
     * @return
     */
    public static void sendMsg(String cardPort,String msg) throws UnsupportedEncodingException {
        if(StringUtils.isEmpty(msg) || StringUtils.isEmpty(cardPort)){
            return;
        }
        int kzkId = Integer.parseInt(cardPort);
        //LED屏地址
        String cardStr = intToString(kzkId,1);
        //显示内容
        String msg16 = stringTo16(msg);
        //数据长度高字节
        String byteLength = byteLength(msg16);
        StringBuffer context = new StringBuffer();
        context.append(cardStr);
        context.append(" 14 00 ");
        context.append(byteLength);
        context.append(" 00 01 00 18 52 00 00 ");
        context.append(msg16);
        //校验和
        String jyhStr = jyh(context.toString());
        context.append(jyhStr);
        context.append(" 0A");
        context.insert(0,"3A ");
        //正确示例
        //起始码、led屏地址、指令码、数据长度、数据长度、页码、区码、显示标志、字体大小、字体颜色、保存标志、播报标志、显示内容（长度不一）、校验和、结束码
        //暂停服务-D4 DD CD A3 B7 FE CE F1
//        String context = "3A 0A 14 00 0F 00 01 00 18 52 00 00 D4 DD CD A3 B7 FE CE F1 2D 0A";
        //号窗口-BA C5 B4 B0 BF DA
//        String context2 = "3A 08 14 00 0D 00 01 00 18 52 00 00 BA C5 B4 B0 BF DA 11 0A";
        //F001-46 30 30 31
//        String context3 = "3A 01 14 00 0B 00 01 00 18 52 00 00 46 30 30 31 62 0A";
        byte[] bytes = stringToBytes(context.toString());
        SerialPort serial = appConfig.serial;
        if(serial != null){
            SerialPortManager.sendToPort(serial,bytes);
        }
        SerialPortManager.sendToPort(serial,bytes);

    }
}
