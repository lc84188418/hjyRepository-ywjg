package com.hjy.hall.dao;

import com.hjy.hall.entity.THallTakenumber;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (THallTakenumber)表数据库访问层
 *
 * @author liuchun
 * @since 2020-07-29 10:28:25
 */
public interface THallTakenumberMapper {

    /**
     * 通过ID查询单条数据
     */
    THallTakenumber selectById(String pkTakenumId);

    /**
     * 新增数据
     */
    int insertSelective(THallTakenumber tHallTakenumber);

    /**
     * 修改数据
     */
    int updateById(THallTakenumber tHallTakenumber);

    /**
     * 通过主键删除数据
     */
    int deleteById(String pkTakenumId);

    /**
     * 查询所有行数据
     */
    List<THallTakenumber> selectAll();

    /**
     * 通过实体作为筛选条件查询
     */
    List<THallTakenumber> selectAllByEntity(THallTakenumber tHallTakenumber);

    /**
     * 取号记数
     */
    int count();
    int count2(@Param("sign") String sign);

    THallTakenumber getByOrdinal(@Param("Ordinal") String Ordinal);

    void deleteAll();
    //取号时查询前方等候人数，自己号以外
    int selectWaitNum(@Param("ordinal") String ordinal);

    List<String> selectAllWaitNum();
    //大厅实时等候人数
    int indexDataWaitNum();

    void deteleBeforeData();

    int wetherExist(@Param("Ordinal") String Ordinal);
    //顺序叫号时，取得最小的号码
    String getMinOrdinal(@Param("ordinal") String type);
    //导办取号，获取该业务类型标识的最大号码
    String getMaxTackNumber(String sign);
    //顺序叫号时，以防万一若有重复数据，则删除
    String deleteDuplicateData();

    int deleteByOrdinal(@Param("Ordinal") String Ordinal);
}