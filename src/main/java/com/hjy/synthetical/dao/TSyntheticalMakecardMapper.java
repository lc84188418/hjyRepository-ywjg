package com.hjy.synthetical.dao;

import com.hjy.synthetical.entity.TSyntheticalMakecard;

import java.util.List;

/**
 * (TSyntheticalMakecard)表数据库访问层
 *
 * @author liuchun
 * @since 2020-08-17 09:53:44
 */
public interface TSyntheticalMakecardMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param pkCardId 主键
     * @return 实例对象
     */
    TSyntheticalMakecard selectById(String pkCardId);

    /**
     * 新增数据
     *
     * @param tHallMakecard 实例对象
     * @return 影响行数
     */
    int insertSelective(TSyntheticalMakecard tHallMakecard);

    /**
     * 修改数据
     *
     * @param tHallMakecard 实例对象
     * @return 影响行数
     */
    int updateById(TSyntheticalMakecard tHallMakecard);

    /**
     * 通过主键删除数据
     *
     * @param pkCardId 主键
     * @return 影响行数
     */
    int deleteById(String pkCardId);

    /**
     * 查询所有行数据
     *
     * @return 对象列表
     */
    List<TSyntheticalMakecard> selectAll();

    /**
     * 通过实体作为筛选条件查询
     *
     * @param tHallMakecard 实例对象
     * @return 对象列表
     */
    List<TSyntheticalMakecard> selectAllByEntity(TSyntheticalMakecard tHallMakecard);
    /**
     * 只查询制证完成的数据
     * @return 对象列表
     */
    List<TSyntheticalMakecard> selectAllMaintain();
}