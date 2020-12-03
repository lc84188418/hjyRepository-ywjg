package com.hjy.log.dao;

import com.hjy.log.entity.TLogRecord;

import java.util.Date;
import java.util.List;

public interface TLogRecordMapper {

    int insertSelective(TLogRecord tLogRecord);

    int deleteById(Object pkRecordId);

    List<TLogRecord> selectAll(TLogRecord logRecord);

    int selectSize(TLogRecord logRecord);

    void deleteRecordByYear(Integer dayNum);
//    void deleteRecordByYear(Date date1);
}
