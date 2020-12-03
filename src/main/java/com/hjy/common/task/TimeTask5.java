package com.hjy.common.task;

import com.hjy.common.utils.PropertiesUtil;
import com.hjy.common.utils.file.CardFileUtil;
import com.hjy.common.utils.file.SmbFileUtil;
import com.hjy.synthetical.dao.TSyntheticalMakecardMapper;
import com.hjy.synthetical.entity.TSyntheticalMakecard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class TimeTask5 {

    //注入mapper
    @Autowired
    private TSyntheticalMakecardMapper tHallMakecardMapper;
    /**
     * 执行定时任务5
     * 防止因为操作人员的原因，导致制证文件数据错乱，目前原因未知
     */
    @Scheduled(cron = "0 0 13 * * ?")
    public void task(){
        Date nowDate = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDateStr = sf.format(nowDate);
        log.info(nowDateStr+"正在执行定时任务5：维护制证文件");
        List<TSyntheticalMakecard> list = tHallMakecardMapper.selectAllMaintain();
        CardFileUtil.MakeCardShareFileMaintain(list);
        //本地文件修改完成后上传到共享文件
        String shareDir = PropertiesUtil.getValue("share.file.directory");
        String localFilePath = "d://hjy//ywjg//makeCard//左边.txt";
        try {
            SmbFileUtil.smbPut(shareDir,localFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
