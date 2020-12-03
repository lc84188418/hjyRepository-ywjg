package com.hjy.synthetical.dao;

import com.hjy.synthetical.entity.TSyntheticalRecord;

import java.util.List;

/**
 * @author liuchun
 * @createDate 2020/10/30 17:49
 * @Classname TSyntheticalRecordMapper
 * @Description TODO
 */
public interface TSyntheticalRecordMapper {

    TSyntheticalRecord selectById(String pkRecordId);

    int insertSelective(TSyntheticalRecord tSyntheticalRecord);

    int updateById(TSyntheticalRecord tSyntheticalRecord);

    int deleteById(String pkRecordId);

    List<TSyntheticalRecord> selectAllPage(TSyntheticalRecord tSyntheticalRecord);
    //分页+条件查询记录条数
    int selectSize(TSyntheticalRecord entity);
    //通过组织机构代码去查备案信息
    List<TSyntheticalRecord> selectByZzjgdm(String ZZJGDM);
}
