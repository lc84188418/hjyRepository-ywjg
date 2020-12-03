package com.hjy.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.task.ObjectAsyncTask;
import com.hjy.common.utils.IDUtils;
import com.hjy.common.utils.JsonUtil;
import com.hjy.common.utils.PasswordEncryptUtils;
import com.hjy.common.utils.page.PageObjectUtils;
import com.hjy.common.utils.page.PageRequest;
import com.hjy.common.utils.page.PageResult;
import com.hjy.common.utils.page.PageUtil;
import com.hjy.list.entity.TListInfo;
import com.hjy.system.dao.TSysRoleMapper;
import com.hjy.system.entity.*;
import com.hjy.system.dao.TSysUserMapper;
import com.hjy.system.service.TSysUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

/**
 * (TSysUser)表服务实现类
 *
 * @author liuchun
 * @since 2020-07-24 17:05:59
 */
@Service
public class TSysUserServiceImpl implements TSysUserService {
    @Autowired
    private TSysUserMapper tSysUserMapper;
    @Autowired
    private TSysRoleMapper tSysRoleMapper;
    /**
     * 通过ID查询单条数据
     * @return 实例对象
     */
    @Override
    public TSysUser selectById(String pkUserId) throws Exception{
        return this.tSysUserMapper.selectById(pkUserId);
    }

    /**
     * 新增数据
     * @param tSysUser 实例对象
     * @return 实例对象
     */
    @Override
    public int insert(TSysUser tSysUser) throws Exception{
        tSysUser.setPkUserId(IDUtils.getUUID());
        //加密
        //默认密码
        String password = "123456";
        String passwordMd5 = PasswordEncryptUtils.MyPasswordEncryptUtil(tSysUser.getUsername(),password);
        tSysUser.setPassword(passwordMd5);
        tSysUser.setCreateTime(new Date());
        tSysUser.setModifyTime(new Date());
        return tSysUserMapper.insertSelective(tSysUser);
    }

    /**
     * 修改数据
     *
     * @param tSysUser 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public int updateById(TSysUser tSysUser) throws Exception{
        return tSysUserMapper.updateById(tSysUser);
    }

    /**
     * 通过主键删除数据
     *
     * @param pkUserId 主键
     * @return 是否成功
     */
    @Transactional()
    @Override
    public int deleteById(String pkUserId) throws Exception{
        return tSysUserMapper.deleteById(pkUserId);
    }

    /**
     * 查询多条数据
     * @return 对象列表
     */
    @Override
    public List<TSysUser> selectAll() throws Exception{
        return this.tSysUserMapper.selectAll();
    }
    /**
     * 通过实体查询多条数据
     * @return 对象列表
     */
    @Override
    public List<TSysUser> selectAllByEntity(TSysUser tSysUser) throws Exception{
        return this.tSysUserMapper.selectAllByEntity(tSysUser);
    }

    @Override
    public String selectUserRoleByUserId(String fk_user_id) {
        return tSysUserMapper.selectUserRoleByUserId(fk_user_id);
    }
    @Transactional()
    @Override
    public int deleteUserRoleByUserId(String fk_user_id) {
        return tSysUserMapper.deleteUserRoleByUserId(fk_user_id);
    }

    @Override
    public PageResult selectAllPage(String param) {
        JSONObject json = JSON.parseObject(param);
        //实体数据
        String unit = JsonUtil.getStringParam(json,"unit");
        String fullName = JsonUtil.getStringParam(json,"fullName");
        String IDcard = JsonUtil.getStringParam(json,"IDcard");
        String policeNum = JsonUtil.getStringParam(json,"policeNum");
        TSysUser user = new TSysUser();
        user.setUnit(unit);
        user.setFullName(fullName);
        user.setIDcard(IDcard);
        user.setPoliceNum(policeNum);
        //分页记录条数
        int total = tSysUserMapper.selectSize(user);
        PageResult result = PageUtil.getPageResult(param,total);
        List<TSysUser> users = tSysUserMapper.selectAllPage(result.getStartRow(),result.getEndRow(),unit,fullName,IDcard,policeNum);
        result.setContent(users);
        return result;
    }
    @Transactional()
    @Override
    public int updatePassword(String parm, ActiveUser activeUser) throws Exception{
        //用户名
        String username = activeUser.getUsername();
        //数据库旧密码
        String oldPasswordMd5 = activeUser.getPassword();
        JSONObject json = JSON.parseObject(parm);
        //输入的旧密码
        String inputOldPassword = JsonUtil.getStringParam(json,"oldPassword");
        //输入的旧密码加密
        String inputOldPasswordMd5 = PasswordEncryptUtils.MyPasswordEncryptUtil(username,inputOldPassword);
        if(!inputOldPasswordMd5.equals(oldPasswordMd5)){
            return 2;
        }
        //输入的新密码
        String inputNewPassword =JsonUtil.getStringParam(json,"newPassword");
        //输入的新密码加密
        String inputNewPasswordMd5 = PasswordEncryptUtils.MyPasswordEncryptUtil(username,inputNewPassword);
        TSysUser user = new TSysUser();
        user.setPkUserId(activeUser.getUserId());
        user.setPassword(inputNewPasswordMd5);
        user.setModifyTime(new Date());
        return tSysUserMapper.updateById(user);
    }

    @Transactional()
    @Override
    public Map<String,Object> insertUserAndRole(String param) {
        Map<String,Object> result = new HashMap<>();
        JSONObject json = JSON.parseObject(param);
        String IDcard = JsonUtil.getStringParam(json,"IDcard");
        String username = JsonUtil.getStringParam(json,"username");
        if(username.getBytes().length > 25){
            result.put("code", 446);
            result.put("status", "error");
            result.put("message","用户名过长，请重新输入！（25位字符或8位汉字）");
            return result;
        }
        List<String> usernameList = tSysRoleMapper.selectAllUsername();
        if(usernameList.contains(username)){
            result.put("code", 445);
            result.put("status", "error");
            result.put("message","该用户名已存在，请重新输入！");
            return result;
        }
        TSysUser tSysUser = new TSysUser();
        String pkUserId = IDUtils.getUUID();
        tSysUser.setPkUserId(pkUserId);
        String unit = JsonUtil.getStringParam(json,"unit");
        String fullName = JsonUtil.getStringParam(json,"fullName");
        if(fullName.getBytes().length > 30){
            result.put("code", 447);
            result.put("status", "error");
            result.put("message","真实姓名过长，请重新输入！（30位字符或10位汉字）");
            return result;
        }
        String email = JsonUtil.getStringParam(json,"email");
        String tel = JsonUtil.getStringParam(json,"tel");
        String enableStatus = JsonUtil.getStringParam(json,"enableStatus");
        String policeNum = JsonUtil.getStringParam(json,"policeNum");
        String ip = JsonUtil.getStringParam(json,"ip");
        String address = JsonUtil.getStringParam(json,"address");
        tSysUser.setUsername(username);
        tSysUser.setUnit(unit);
        tSysUser.setFullName(fullName);
        tSysUser.setEmail(email);
        tSysUser.setTel(tel);
        tSysUser.setEnableStatus(enableStatus);
        tSysUser.setIDcard(IDcard);
        tSysUser.setPoliceNum(policeNum);
        tSysUser.setIp(ip);
        tSysUser.setAddress(address);
        //加密
        //默认密码
        String password = "123456";
        String passwordMd5 = PasswordEncryptUtils.MyPasswordEncryptUtil(tSysUser.getUsername(),password);
        tSysUser.setPassword(passwordMd5);
        tSysUser.setCreateTime(new Date());
        tSysUser.setModifyTime(new Date());
        tSysUserMapper.insertSelective(tSysUser);
        //是否直接分配角色
        String roleId = JsonUtil.getStringParam(json,"roleId");
        if(StringUtils.isEmpty(roleId)){
            result.put("code", 201);
            result.put("status", "success");
            result.put("msg", "添加用户成功,但暂未分配角色，无法使用！");
            return result;
        }else {
            ObjectAsyncTask.addUserRoleByUserRole(pkUserId,roleId);
            result.put("code", 200);
            result.put("status", "success");
            result.put("msg", "添加用户与分配角色成功");
            return result;
        }
    }

    @Transactional()
    @Override
    public int updateUser(String param) {
        JSONObject json = JSON.parseObject(param);
        TSysUser user = new TSysUser();
        //用户基本信息
        String pkUserId = String.valueOf(json.get("pkUserId"));
        user.setPkUserId(pkUserId);
        String email = JsonUtil.getStringParam(json,"email");
        user.setEmail(email);
        String tel = JsonUtil.getStringParam(json,"tel");
        user.setTel(tel);
        String IDcard = JsonUtil.getStringParam(json,"IDcard");
        user.setIDcard(IDcard);
        String fullName = JsonUtil.getStringParam(json,"fullName");
        user.setFullName(fullName);
        String policeNum = JsonUtil.getStringParam(json,"policeNum");
        user.setPoliceNum(policeNum);
        String unit = JsonUtil.getStringParam(json,"unit");
        user.setUnit(unit);
        String ip = JsonUtil.getStringParam(json,"ip");
        user.setIp(ip);
        String address = JsonUtil.getStringParam(json,"address");
        user.setIp(ip);
        String enableStatus = JsonUtil.getStringParam(json,"enableStatus");
        user.setEnableStatus(enableStatus);
        user.setAddress(address);
        user.setModifyTime(new Date());
        String fkRoleId = JsonUtil.getStringParam(json,"roleId");
        //修改用户信息
        if(fkRoleId != null){
            tSysUserMapper.updateById(user);
            //删除原有角色
            tSysUserMapper.deleteUserRoleByUserId(pkUserId);
            ObjectAsyncTask.addUserRoleByUserRole(pkUserId,fkRoleId);
            return 1;
        }else {
            return 0;
        }
    }

    //分配角色
    @Transactional()
    @Override
    public CommonResult roleDistribute(String param) {
        JSONObject json = JSON.parseObject(param);
        String fk_user_id=String.valueOf(json.get("fk_user_id"));
        String fk_role_id=String.valueOf(json.get("fk_role_id"));
        ReUserRole userRole  = new ReUserRole();
        userRole.setPk_userRole_id(IDUtils.getUUID());
        userRole.setFk_user_id(fk_user_id);
        userRole.setFk_role_id(fk_role_id);
        //删除原有用户角色数据
        int i = tSysUserMapper.deleteUserRoleByUserId(fk_user_id);
        //添加用户角色信息
        int j = tSysUserMapper.addUserRoleByUserRole(userRole);
        if(i > 0 && j > 0 ){
            return new CommonResult(200,"success","角色分配成功!",null);
        }else {
            return new CommonResult(444,"error","角色分配失败!",null);
        }
    }
    //快捷菜单设置
    @Transactional()
    @Override
    public CommonResult quickSet(String param) {
        JSONObject json = JSON.parseObject(param);
        String fk_user_id=String.valueOf(json.get("fk_user_id"));
        JSONArray jsonArray = json.getJSONArray("ids");
        String permsIdsStr = jsonArray.toString();
        List<String> idList = JSONArray.parseArray(permsIdsStr,String.class);
        //通过user_id删除原有的perms
        tSysUserMapper.deleteUserPermsByUserId(fk_user_id);
        //添加选中的权限菜单
        List<ReUserPerms> userPermsList = new ArrayList<>();
        for (String s:idList){
            ReUserPerms userPerms = new ReUserPerms();
            userPerms.setPk_userPerms_id(IDUtils.getUUID());
            userPerms.setFk_user_id(fk_user_id);
            userPerms.setFk_perms_id(s);
            userPermsList.add(userPerms);
        }
        tSysUserMapper.addUserPermsByList(userPermsList);
        return new CommonResult(200,"success","快捷菜单设置成功!",null);
    }

    @Override
    public List<TSysPerms> selectPermsByUser(String userId) {
        return tSysUserMapper.selectPermsByUser(userId);
    }

    @Override
    public CommonResult selectQuickSetByUser(String fk_user_id) {
        JSONObject jsonObject = new JSONObject();
        //该用户所有的菜单
        List<TSysPerms> list = tSysUserMapper.selectQuickSetByUser(fk_user_id);
        jsonObject.put("permsLit",list);
        //该用户已设置的快捷菜单
        List<String> ids = tSysUserMapper.selectUserPermsByUser(fk_user_id);
        jsonObject.put("ids",ids);
        return new CommonResult(200,"success","获取快捷菜单数据成功!",jsonObject);
    }

    @Override
    public void addUserRoleByUserRole(ReUserRole userRole) {
        tSysRoleMapper.addUserRoleByUserRole(userRole);
    }
}