package com.hjy.common.utils;

import java.util.UUID;

public class IDUtils {

    /**
     * 获得一个32位UUID
     * @return String UUID
     */
    public static String getUUID(){
        String uuid = UUID.randomUUID().toString();
        //去掉“-”符号
        return uuid.replaceAll("-", "");
    }
}
