package com.hjy.common.task;

import com.hjy.list.dao.TListInfoMapper;
import com.hjy.system.dao.TSysParamMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TimeTask2 {

    //注入mapper
    @Autowired
    TListInfoMapper tListInfoMapper;
    @Autowired
    TSysParamMapper tSysParamMapper;
    /**
     * 执行定时任务2
     * 每月1号1点0时0分
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void task(){
        Date nowDate = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDateStr = sf.format(nowDate);
        log.info(nowDateStr+"正在执行定时任务2：每月1号1点0时0分 重置黑名单代理次数");

        String ZDJRHMDQX = tSysParamMapper.selectParamById("ZDJRHMDQX");
        int year = 1;
        int dayNum = year * 365;
        if(ZDJRHMDQX != null){
            //判断是否是正整数
            String regex="^[1-9]+[0-9]*$";
            Pattern p = Pattern.compile(regex);
            Matcher m=p.matcher(ZDJRHMDQX);
            if(m.find()){
                int temp = Integer.parseInt(ZDJRHMDQX);
                dayNum = temp * 365;
            }
        }
        tListInfoMapper.deleteBlackByYear(dayNum);
    }
}
