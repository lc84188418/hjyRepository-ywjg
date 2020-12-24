package com.hjy.common.utils.led;

import java.io.UnsupportedEncodingException;
import com.sun.jna.*;
import com.sun.jna.ptr.PointerByReference;
import java.io.UnsupportedEncodingException;

/**
 * @author liuchun
 * @Package com.hjy.common.utils.led
 * @date 2020/12/15 10:18
 * description:
 */


public class MD5Encoder{
    //获得byte数组
    public static byte[] getBytes(String input) throws UnsupportedEncodingException {
        int byteLen=0;
        byte[] bytes=new byte[3*input.length()];
        for(int i=0;i<input.length();i++) {
            char now=input.charAt(i);
            byte[] nowCharBytes=null;
            if(isChinese(now)) {
                nowCharBytes=(""+now).getBytes("utf-8");
            }else {
                nowCharBytes=(""+now).getBytes();
            }
            for(int j=0;j<nowCharBytes.length;j++) {
                bytes[byteLen++]=nowCharBytes[j];
            }
        }
        return bytes;
    }
    //判断是否为中文
    public static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }
}
