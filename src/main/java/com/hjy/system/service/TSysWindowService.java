package com.hjy.system.service;

import com.hjy.common.domin.CommonResult;
import com.hjy.system.entity.TSysBusinesstype;
import com.hjy.system.entity.TSysWindow;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * (TSysWindow)表服务接口
 *
 * @author liuchun
 * @since 2020-07-28 14:56:45
 */
public interface TSysWindowService {

    /**
     * 通过ID查询单条数据
     */
    TSysWindow selectById(Object pkWindowId)throws Exception;

    /**
     * 新增数据
     */
    CommonResult  insert(String param) throws Exception;

    /**
     * 修改数据
     */
    CommonResult updateById(String param, HttpSession session) throws Exception;

    /**
     * 通过主键删除数据
     */
    int deleteById(Object pkWindowId) throws Exception;

    /**
     * 查询所有数据
     */
     List<TSysWindow> selectAll() throws Exception;
     /**
     * 通过实体查询所有数据
     */
     List<TSysWindow> selectAllByEntity(TSysWindow tSysWindow)throws Exception;

    List<String> selectWindowName();
    /**
     * 登录时修改窗口的操作用户
     */
    int updateOperatorPeople(TSysWindow tSysWindow);
    //通过主键查询该窗口配置的ip地址
    String selectIpByPkid(String pkWindowId);
    //查询单个数据
    Map<String, Object> tSysWindowgetOne(String param);
    //暂停服务
    CommonResult stopService(HttpSession session);

    TSysWindow selectByIp(String ip);
}