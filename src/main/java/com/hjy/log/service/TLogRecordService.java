package com.hjy.log.service;

import com.hjy.common.utils.page.PageResult;
import com.hjy.log.entity.TLogRecord;

import java.util.List;

/**
 * (TLogRecord)表服务接口
 *
 * @author liuchun
 * @since 2020-10-15 09:47:51
 */
public interface TLogRecordService {

    /**
     * 新增数据
     *
     * @param tLogRecord 实例对象
     * @return 实例对象
     */
    int insert(TLogRecord tLogRecord) throws Exception;

    /**
     * 通过主键删除数据
     *
     * @param pkRecordId 主键
     * @return 是否成功
     */
    int deleteById(Object pkRecordId) throws Exception;

    /**
     * 通过实体查询所有数据
     *
     * @return list
     */

    PageResult selectAllPage(String param);
}