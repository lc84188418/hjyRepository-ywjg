package com.hjy.common.utils;

import org.apache.commons.lang.StringUtils;

/**
 * @author liuchun
 * @createDate 2020/11/3 23:30
 * @Classname StrBufferUtil
 * @Description 针对于录入信息长度过长，折中取一部分
 */
public class StrBufferUtil {
    public static String handTooLengthStrBuffer(String param){
        if(StringUtils.isEmpty(param)){
            return null;
        }else {
            StringBuffer resultBuffer = new StringBuffer();
            //
            int byteLength = param.getBytes().length;
            if(byteLength >3999){
                //字节过长，只取其中一部分
                resultBuffer.append("此处文字过长，只显示部分内容，详情可在综合平台查看~~");
                if(param.length() > 1300){
                    resultBuffer.append(param.substring(0,1000));
                }
                return resultBuffer.toString();
            }else {
                return param;
            }
        }

    }
}
