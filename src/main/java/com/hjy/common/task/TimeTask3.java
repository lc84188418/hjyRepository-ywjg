package com.hjy.common.task;

import com.hjy.log.dao.TLogRecordMapper;
import com.hjy.system.dao.TSysParamMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
@Slf4j
public class TimeTask3 {

    //注入mapper
    @Autowired
    TLogRecordMapper tLogRecordMapper;
    @Autowired
    TSysParamMapper tSysParamMapper;
    /**
     * 执行定时任务3
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void task(){
        Date nowDate = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDateStr = sf.format(nowDate);
        log.info(nowDateStr+"正在执行定时任务3：删除规定期限外的日志记录");

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar c = Calendar.getInstance();
//        //过去一年
//        c.setTime(new Date());
//        c.add(Calendar.YEAR, -1);
//        Date y = c.getTime();
//        String year = sdf.format(y);
//        System.err.println("过去一年："+year);
        //查询参数配置中操作日志记录保存时间（整天），如果没有就默认为90次
        String CZRZJLBCSJ = tSysParamMapper.selectParamById("CZRZJLBCSJ");
        int dayNum = 90;
        if(CZRZJLBCSJ != null){
            dayNum = Integer.parseInt(CZRZJLBCSJ);
        }
        tLogRecordMapper.deleteRecordByYear(dayNum);
    }
}
