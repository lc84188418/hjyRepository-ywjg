package com.hjy.system.dao;

import com.hjy.system.entity.TSysBusinesstype;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (TSysBusinesstype)表数据库访问层
 *
 * @author liuchun
 * @since 2020-07-28 16:54:27
 */
public interface TSysBusinesstypeMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param pkBusinesstypeId 主键
     * @return 实例对象
     */
    TSysBusinesstype selectById(Object pkBusinesstypeId);

    /**
     * 新增数据
     *
     * @param tSysBusinesstype 实例对象
     * @return 影响行数
     */
    int insertSelective(TSysBusinesstype tSysBusinesstype);

    /**
     * 修改数据
     *
     * @param tSysBusinesstype 实例对象
     * @return 影响行数
     */
    int updateById(TSysBusinesstype tSysBusinesstype);

    /**
     * 通过主键删除数据
     *
     * @param pkBusinesstypeId 主键
     * @return 影响行数
     */
    int deleteById(@Param("pkBusinesstypeId") String pkBusinesstypeId);

    /**
     * 查询所有行数据
     * @return 对象列表
     */
    List<TSysBusinesstype> selectAll();
     /**
     * 通过实体作为筛选条件查询
     *
     * @param tSysBusinesstype 实例对象
     * @return 对象列表
     */
    List<TSysBusinesstype> selectAllByEntity(TSysBusinesstype tSysBusinesstype);
    //查询所有业务类型名称
    List<String> selectBusinessName();
    //根据业务类型名称查询标识
    String selectTypeLevelByTypeName(@Param("businessType")String businessType);
    //查询所有业务类型名称+标识
    List<TSysBusinesstype> selectBusinessNameAndLevel();
    List<TSysBusinesstype> selectBusinessNameAndLevel2();
    //查询该业务是否需要流水号
    String selectWhetherNullByBusinessType(@Param("businessType")String businessType);
}