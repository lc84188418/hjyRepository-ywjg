package com.hjy.hall.service;

import com.hjy.common.utils.page.PageResult;
import com.hjy.hall.entity.THallJiashizheng;

/**
 * (THallJiashizheng)表服务接口
 *
 * @author liuchun
 * @since 2020-07-27 14:17:48
 */
public interface THallJiashizhengService {

    /**
     * 通过ID查询单条数据
     *
     * @param pkJiashiId 主键
     * @return 实例对象
     */
    THallJiashizheng selectById(String pkJiashiId);


    /**
     * 新增数据
     *
     * @param tHallJiashizheng 实例对象
     * @return 实例对象
     */
    int insert(THallJiashizheng tHallJiashizheng);

    /**
     * 修改数据
     *
     * @param param
     * @return 实例对象
     */
    int updateById(String param);

    /**
     * 通过主键删除数据
     *
     * @param pkJiashiId 主键
     * @return 是否成功
     */
    int deleteById(String pkJiashiId);


    /**
     * 通过实体查询所有数据
     *
     * @return list
     */

    PageResult selectAllPage();

    PageResult selectAll(String param);
    /**
     * 通过流水号查询数据
     * @param associationNumber 流水号
     * @return 影响行数
     */
    THallJiashizheng selectByAssociationNumber(String associationNumber);

    int selectTodayCount();
}