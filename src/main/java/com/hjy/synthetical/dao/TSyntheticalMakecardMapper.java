package com.hjy.synthetical.dao;

import com.hjy.synthetical.entity.TSyntheticalMakecard;
import com.hjy.system.entity.ReRolePerms;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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
    /**
     * 批量修改制证完成
     * @return 修改数据量
     */
    int makeCompleteBatchUpdate(@Param("idList")List<String> idList, @Param("operatorPeople")String operatorPeople, Date sysdate);
    /**
     * 批量获取制证信息
     * @return 制证信息列表
     */
    List<TSyntheticalMakecard> selectAllById(@Param("idList")List<String> idList);
    /**
     * 批量删除数据
     * @return 制证信息列表
     */
    int deleteByIdList(@Param("idList")List<String> idList);
}