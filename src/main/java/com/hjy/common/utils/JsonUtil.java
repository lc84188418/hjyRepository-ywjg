package com.hjy.common.utils;

import com.alibaba.fastjson.JSONObject;

public class JsonUtil {
    public static String getStringParam(JSONObject jsonObject,String paramName){
        if(jsonObject.get(paramName)!=null){
            return String.valueOf(jsonObject.get(paramName));
        }
        return null;
    }
}
