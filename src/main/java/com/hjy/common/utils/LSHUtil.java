package com.hjy.common.utils;


import com.hjy.hall.dao.THallJiashizhengMapper;
import com.hjy.hall.dao.THallJidongcheMapper;
import com.hjy.hall.service.THallJiashizhengService;
import com.hjy.hall.service.THallJidongcheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class LSHUtil {

    @Autowired
    private THallJiashizhengService jiashizhengService;
    @Autowired
    private THallJidongcheService jidongcheService;
    private static LSHUtil lshUtil;
    @PostConstruct
    public void init() {
        lshUtil= this;
        lshUtil.jidongcheService= this.jidongcheService;
        lshUtil.jiashizhengService= this.jiashizhengService;
    }
    /**
     * 获取退办单的流水号
     */
    public static String getLSH(String type){
        StringBuffer stringBuffer = new StringBuffer();
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        Date nowDate = new Date();
        String dateStr = ft.format(nowDate);
        stringBuffer.append(dateStr);
        if("机动车".equals(type)){
            //查询今日已有多少条退办单
            int count = lshUtil.jidongcheService.selectTodayCount()+1;
            if(count>0 && count<10){
                stringBuffer.append("100"+count);
            }else if(count>=10 && count<100){
                stringBuffer.append("10"+count);
            }else {
                stringBuffer.append("1"+count);
            }
        }else {
            //查询今日已有多少条退办单
            int count = lshUtil.jiashizhengService.selectTodayCount()+1;
            if(count>0 && count<10){
                stringBuffer.append("200"+count);
            }else if(count>=10 && count<100){
                stringBuffer.append("20"+count);
            }else {
                stringBuffer.append("2"+count);
            }
        }
        return stringBuffer.toString();
    }
}
