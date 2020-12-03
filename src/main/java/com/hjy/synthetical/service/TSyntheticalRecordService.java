package com.hjy.synthetical.service;

import com.hjy.common.domin.CommonResult;
import com.hjy.synthetical.entity.TSyntheticalRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * (TSyntheticalRecord)表服务接口
 *
 * @author liuchun
 * @since 2020-10-30 17:44:37
 */
public interface TSyntheticalRecordService {

    /**
     * 通过ID查询单条数据
     *
     * @param param 主键
     * @return 实例对象
     */
    CommonResult selectById(String param) throws Exception;
    CommonResult hallqueuegetRecord(String param) throws Exception;

    /**
     * 新增数据
     *
     * @param tSyntheticalRecord 实例对象
     * @return 实例对象
     */
    CommonResult insert(TSyntheticalRecord tSyntheticalRecord) throws Exception;

    CommonResult tSyntheticalRecordAdd(TSyntheticalRecord tSyntheticalRecord, MultipartFile[] files);


    /**
     * 修改数据
     *
     * @param tSyntheticalRecord 实例对象
     * @return 实例对象
     */
    CommonResult updateById(TSyntheticalRecord tSyntheticalRecord) throws Exception;

    /**
     * 通过主键删除数据
     *
     * @param param 主键
     * @return 是否成功
     */
    CommonResult deleteById(String param) throws Exception;
    /**
     * 查询所有数据
     * @return list
     */
    CommonResult tSyntheticalRecordList(String param);

    List<TSyntheticalRecord> selectByZzjgdm(String bidcard);
}