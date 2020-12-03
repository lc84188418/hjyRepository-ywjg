package com.hjy.hall.service;

import com.hjy.common.utils.page.PageResult;
import com.hjy.hall.entity.THallJidongche;

/**
 * (THallJidongche)表服务接口
 *
 * @author liuchun
 * @since 2020-07-27 15:51:25
 */
public interface THallJidongcheService {

    /**
     * 通过ID查询单条数据
     *
     * @param pkJidongcheId 主键
     * @return 实例对象
     */
    THallJidongche selectById(String pkJidongcheId);


    /**
     * 新增数据
     *
     * @param tHallJidongche 实例对象
     * @return 实例对象
     */
    int insert(THallJidongche tHallJidongche);

    /**
     * 修改数据
     *
     * @param param 实例对象
     * @return 实例对象
     */
    int updateById(String param);

    /**
     * 通过主键删除数据
     *
     * @param pkJidongcheId 主键
     * @return 是否成功
     */
    int deleteById(String pkJidongcheId);
    /**
     * 分页查询
     * @return list
     */
    PageResult selectAllPage();
    /**
     * 分页查询
     * @return list
     */
    PageResult selectAll(String param);
    /**
     * 通过流水号查询数据
     * @param param 流水号
     * @return 影响行数
     */
    THallJidongche selectByAssociationNumber(String param);

    int selectTodayCount();
}