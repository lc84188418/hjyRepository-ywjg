package com.hjy.system.dao;

import com.hjy.system.entity.TSysWindow;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (TSysWindow)表数据库访问层
 *
 * @author liuchun
 * @since 2020-07-28 14:56:45
 */
public interface TSysWindowMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param pkWindowId 主键
     * @return 实例对象
     */
    TSysWindow selectById(Object pkWindowId);

    /**
     * 新增数据
     *
     * @param tSysWindow 实例对象
     * @return 影响行数
     */
    int insertSelective(TSysWindow tSysWindow);

    /**
     * 修改数据
     *
     * @param tSysWindow 实例对象
     * @return 影响行数
     */
    int updateById(TSysWindow tSysWindow);

    /**
     * 通过主键删除数据
     *
     * @param pkWindowId 主键
     * @return 影响行数
     */
    int deleteById(Object pkWindowId);

    /**
     * 查询所有行数据
     * @return 对象列表
     */
    List<TSysWindow> selectAll();
     /**
     * 通过实体作为筛选条件查询
     *
     * @param tSysWindow 实例对象
     * @return 对象列表
     */
    List<TSysWindow> selectAllByEntity(TSysWindow tSysWindow);
    //通过ip查询窗口名称
    String selectWindowNameByIp(@Param("Ip") String Ip);
    //通过ip查询窗口信息
    TSysWindow selectWindowByIp(@Param("Ip")String ip);
    //查询所有窗口的窗口名
    List<String> selectWindowName();
    //登录时修改窗口操作用户
    int updateOperatorPeople(TSysWindow tSysWindow);
    //查询窗口中所有已经分配过的ip
    List<String> selectAllIp();
    //通过主键查询该窗口配置的ip地址
    String selectIpByPkid(String pkWindowId);
    //通过ip查询窗口所有字段信息
    TSysWindow selectByIp(@Param("Ip")String ip);
    //暂停服务
    int stopService(TSysWindow window);

    TSysWindow selectWindowByName(@Param("windowName")String windowName);
}