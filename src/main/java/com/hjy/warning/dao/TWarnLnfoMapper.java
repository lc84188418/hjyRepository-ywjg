package com.hjy.warning.dao;

import com.hjy.warning.entity.Warning;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (TWarnLnfo)表数据库访问层
 *
 * @author liuchun
 * @since 2020-09-22 18:22:31
 */
public interface TWarnLnfoMapper {

    /**
     * 新增数据
     *
     * @param tWarnLnfo 实例对象
     * @return 影响行数
     */
    int insertSelective(Warning tWarnLnfo);

    /**
     * 修改数据
     *
     * @param tWarnLnfo 实例对象
     * @return 影响行数
     */
    int update(Warning tWarnLnfo);

    /**
     * 通过主键删除数据
     * @param pkWarningId 主键
     * @return 影响行数
     */
    int deleteById(Warning pkWarningId);
    /**
     * 通过主键查询数据
     * @param pkWarningId 主键
     * @return 影响行数
     */
    Warning selectById(String pkWarningId);

    Warning selectByWindow(@Param("handleStatus") String handleStatus);

    int selectSize(Warning warning);

    List<Warning> selectAllPage(Warning warning);
    //维护接口
    int systemMaintain();
    //定时任务，处理未办结的业务
    void deteleNoHandBeforeData();
}