package com.hjy.system.service;

import com.hjy.common.domin.CommonResult;
import com.hjy.common.utils.page.PageResult;
import com.hjy.system.entity.ActiveUser;
import com.hjy.system.entity.ReUserRole;
import com.hjy.system.entity.TSysPerms;
import com.hjy.system.entity.TSysUser;
import java.util.List;
import java.util.Map;

/**
 * (TSysUser)表服务接口
 *
 * @author liuchun
 * @since 2020-07-24 17:05:59
 */
public interface TSysUserService {

    /**
     * 通过ID查询单条数据
     * @return 实例对象
     */
    TSysUser selectById(String pkUserId)throws Exception;


    /**
     * 新增数据
     * @param tSysUser 实例对象
     * @return 实例对象
     */
    int insert(TSysUser tSysUser) throws Exception;

    /**
     * 修改数据
     *
     * @param tSysUser 实例对象
     * @return 实例对象
     */
    int updateById(TSysUser tSysUser) throws Exception;

    /**
     * 通过主键删除数据
     *
     * @param pkUserId 主键
     * @return 是否成功
     */
    int deleteById(String pkUserId) throws Exception;

    /**
     * 查询所有数据
     * @return list
     */
     List<TSysUser> selectAll() throws Exception;
     /**
     * 通过实体查询所有数据
     * @return list
     */
     List<TSysUser> selectAllByEntity(TSysUser tSysUser)throws Exception;
    /**
     * 通过userId查询已分配角色数据
     */
    String selectUserRoleByUserId(String fk_user_id);
    /**
     * 通过userId删除已分配角色数据
     */
    int deleteUserRoleByUserId(String fk_user_id);
    /**
     * 分页查询所有数据
     * @return list
     */
    PageResult selectAllPage(String param);
    /**
     * 修改密码
     */
    int updatePassword(String parm, ActiveUser activeUser) throws Exception;
    /**
     * 添加用户且添加角色
     */
    Map<String,Object> insertUserAndRole(String param);
    /**
     * 修改用户且修改角色
     */
    int updateUser(String param);

    void addUserRoleByUserRole(ReUserRole userRole);

    CommonResult roleDistribute(String param);
    //查看自己的快捷菜单
    CommonResult selectQuickSetByUser(String param);
    //快捷菜单设置
    CommonResult quickSet(String param);
    //查看自己的快捷菜单信息
    List<TSysPerms> selectPermsByUser(String userId);
}