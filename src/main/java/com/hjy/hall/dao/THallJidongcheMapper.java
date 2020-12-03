package com.hjy.hall.dao;

import com.hjy.hall.entity.THallJidongche;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (THallJidongche)表数据库访问层
 *
 * @author liuchun
 * @since 2020-07-27 15:51:25
 */
public interface THallJidongcheMapper {

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
     * @return 影响行数
     */

    int insertSelective(THallJidongche tHallJidongche);

    /**
     * 修改数据
     *
     * @param tHallJidongche 实例对象
     * @return 影响行数
     */
    int updateById(THallJidongche tHallJidongche);

    /**
     * 通过主键删除数据
     *
     * @param pkJidongcheId 主键
     * @return 影响行数
     */
    int deleteById(String pkJidongcheId);

    /**
     * 查询所有行数据
     *
     * @return 对象列表
     */
    List<THallJidongche> selectAll(THallJidongche tHallJidongche);

    /**
     * 通过实体作为筛选条件查询
     *
     * @param tHallJidongche 实例对象
     * @return 对象列表
     */
    //分页查询总记录条数
    int selectSize(THallJidongche tHallJidongche);
    //分页查询记录
    List<THallJidongche> selectAllPage(int startRow, int endRow);
    //列表总记录条数
    int selectSizePage();

    THallJidongche selectByAssociationNumber(@Param("param") String param);

}