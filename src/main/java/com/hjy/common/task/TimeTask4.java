package com.hjy.common.task;

import com.hjy.system.dao.TSysParamMapper;
import com.hjy.system.entity.TSysParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class TimeTask4 {

    //注入mapper
    @Autowired
    TSysParamMapper tSysParamMapper;
    /**
     * 执行定时任务4
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void task(){
        Date nowDate = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDateStr = sf.format(nowDate);
        log.info(nowDateStr+"正在执行定时任务4：设置访问同步库数据");
        TSysParam param = new TSysParam();
        param.setPkParamId("SFHQTBKSJ");
        param.setParamValue("是");
        int i = tSysParamMapper.updateById(param);
    }
}
