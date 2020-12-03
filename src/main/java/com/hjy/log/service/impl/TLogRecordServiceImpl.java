package com.hjy.log.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.utils.JsonUtil;
import com.hjy.common.utils.page.PageResult;
import com.hjy.common.utils.page.PageUtil;
import com.hjy.hall.entity.THallJiashizheng;
import com.hjy.log.dao.TLogRecordMapper;
import com.hjy.log.entity.TLogRecord;
import com.hjy.log.service.TLogRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * (TLogRecord)表服务实现类
 *
 * @author liuchun
 * @since 2020-10-15 09:47:51
 */
@Service
public class TLogRecordServiceImpl implements TLogRecordService {
    @Autowired
    private TLogRecordMapper tLogRecordMapper;

    /**
     * 新增数据
     *
     * @param tLogRecord 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public int insert(TLogRecord tLogRecord) throws Exception {
        return tLogRecordMapper.insertSelective(tLogRecord);
    }

    /**
     * 通过主键删除数据
     *
     * @param pkRecordId 主键
     * @return 是否成功
     */
    @Transactional()
    @Override
    public int deleteById(Object pkRecordId) throws Exception {
        return tLogRecordMapper.deleteById(pkRecordId);
    }

    /**
     * 查询多条数据
     *
     * @return 对象列表
     */
    @Override
    public PageResult selectAllPage(String param) {
        JSONObject json = JSON.parseObject(param);
        //实体数据
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String recordModule = JsonUtil.getStringParam(json,"recordModule");
        String recordType = JsonUtil.getStringParam(json,"recordType");
        String recordUserName = JsonUtil.getStringParam(json,"recordUserName");
        String recordFullName = JsonUtil.getStringParam(json,"recordFullName");
        String queryStartStr = JsonUtil.getStringParam(json,"queryStart");
        Date queryStart = null;
        if(queryStartStr != null && queryStartStr.length()>8){
            try {
                queryStart = ft.parse(queryStartStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String queryEndStr = JsonUtil.getStringParam(json,"queryEnd");
        Date queryEnd = null;
        if(queryEndStr != null && queryEndStr.length()>8){
            try {
                queryEnd = ft.parse(queryEndStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //实体数据
        TLogRecord logRecord = new TLogRecord();
        logRecord.setRecordModule(recordModule);
        logRecord.setRecordType(recordType);
        logRecord.setRecordUserName(recordUserName);
        logRecord.setRecordFullName(recordFullName);
        logRecord.setStartTime(queryStart);
        logRecord.setEndTime(queryEnd);
        //分页记录条数
        int total = tLogRecordMapper.selectSize(logRecord);
        PageResult result = PageUtil.getPageResult2(param,total);
        logRecord.setStartRow(result.getStartRow());
        logRecord.setEndRow(result.getEndRow());
        List<TLogRecord> list = tLogRecordMapper.selectAll(logRecord);
        result.setContent(list);
        return result;
    }
}