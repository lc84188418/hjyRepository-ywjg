package com.hjy.common.task;

import com.hjy.common.utils.Http.HttpClient4;
import com.hjy.common.utils.PropertiesUtil;
import com.hjy.hall.dao.THallTakenumberMapper;
import com.hjy.hall.service.THallQueueService;
import com.hjy.warning.dao.TWarnLnfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TimeTask1 {

    //注入mapper
    @Autowired
    THallTakenumberMapper takenumberMapper;
    @Autowired
    THallQueueService tHallQueueService;
    @Autowired
    TWarnLnfoMapper tWarnLnfoMapper;
    /**
     * 执行定时任务1
     * 0 0 0 * * ?
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void task(){
        Date nowDate = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDateStr = sf.format(nowDate);
        log.info(nowDateStr+"-正在执行定时任务1：每天凌晨0点0分0秒清除取号表");
        takenumberMapper.deleteAll();
        tHallQueueService.deteleNoHandBeforeData();
        tWarnLnfoMapper.deteleNoHandBeforeData();
    }
    //清除前一天的取号数据
    @PostConstruct
    public void deteleBeforeData(){
        log.info("项目已启动，开始删除今天之前的取号数据");
        takenumberMapper.deteleBeforeData();
        tHallQueueService.deteleBeforeData();
        tWarnLnfoMapper.systemMaintain();
    }
}
