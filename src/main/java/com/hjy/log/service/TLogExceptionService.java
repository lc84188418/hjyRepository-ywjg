package com.hjy.log.service;

import com.hjy.log.entity.TLogException;

import java.util.List;

/**
 * (TLogException)表服务接口
 *
 * @author liuchun
 * @since 2020-10-15 09:47:48
 */
public interface TLogExceptionService {

    /**
     * 通过ID查询单条数据
     *
     * @param pkExcId 主键
     * @return 实例对象
     */
    TLogException selectById(Object pkExcId) throws Exception;


    /**
     * 新增数据
     *
     * @param tLogException 实例对象
     * @return 实例对象
     */
    int insert(TLogException tLogException) throws Exception;

    /**
     * 修改数据
     *
     * @param tLogException 实例对象
     * @return 实例对象
     */
    int updateById(TLogException tLogException) throws Exception;

    /**
     * 通过主键删除数据
     *
     * @param pkExcId 主键
     * @return 是否成功
     */
    int deleteById(Object pkExcId) throws Exception;

    /**
     * 查询所有数据
     *
     * @return list
     */
    List<TLogException> selectAll() throws Exception;

    /**
     * 通过实体查询所有数据
     *
     * @return list
     */
    List<TLogException> selectAllByEntity(TLogException tLogException) throws Exception;

}