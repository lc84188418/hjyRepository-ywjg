package com.hjy.hall.dao;

import com.hjy.hall.entity.THallJiashizheng;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (THallJiashizheng)表数据库访问层
 *
 * @author liuchun
 * @since 2020-07-27 14:17:47
 */

public interface THallJiashizhengMapper {

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
     * @return 影响行数
     */

    int insertSelective(THallJiashizheng tHallJiashizheng);

    /**
     * 修改数据
     *
     * @param tHallJiashizheng 实例对象
     * @return 影响行数
     */
    int updateById(THallJiashizheng tHallJiashizheng);

    /**
     * 通过主键删除数据
     *
     * @param pkJiashiId 主键
     * @return 影响行数
     */
    int deleteById(String pkJiashiId);

    /**
     * 查询所有行数据
     *
     * @return 对象列表
     */

    /**
     * 通过实体作为筛选条件查询
     *
     * @return 对象列表
     */

    int selectSizePage();

    List<THallJiashizheng> selectAllPage(int startRow, int endRow);

    int selectSize(THallJiashizheng tHallJiashizheng);

    List<THallJiashizheng> selectAll(THallJiashizheng tHallJiashizheng);

    THallJiashizheng selectByAssociationNumber(@Param("param") String param);
}