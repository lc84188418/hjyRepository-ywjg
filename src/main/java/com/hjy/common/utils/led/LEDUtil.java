package com.hjy.common.utils.led;

import java.util.StringJoiner;

/**
 * @author liuchun
 * @Package com.hjy.common.utils.led
 * @date 2020/12/21 9:55
 * description:
 */
public class LEDUtil {
    public static byte[] sendSingleColorText(String msg)throws Exception{
        byte[] b = msg.getBytes("gb2312");
        byte[] bytes = new byte[b.length+1];
        for(int j = 0; j < b.length; j++){
            bytes[j] = b[j];
        }
        bytes[b.length]=0;
        return bytes;
    }
    public static String sendSingleColorText2(byte[] bytes)throws Exception{
        StringJoiner sj = new StringJoiner(" ");
        //证明是否正确
        for (int j = 0; j < bytes.length; j++) {
            String hex = Integer.toHexString(bytes[j] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sj.add(hex);
        }
        return sj.toString();
    }
    public static String sendSingleColorText3(byte[] bytes)throws Exception{
        StringBuffer sb = new StringBuffer();
        //证明是否正确
        for (int j = 0; j < bytes.length; j++) {
            String hex = Integer.toHexString(bytes[j] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
