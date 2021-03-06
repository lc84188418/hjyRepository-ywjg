package com.hjy.system.service.impl;


import com.hjy.common.auth.TokenGenerator;
import com.hjy.common.utils.DateUtil;
import com.hjy.common.utils.Http.HttpClient4;
import com.hjy.common.utils.PropertiesUtil;
import com.hjy.hall.dao.THallQueueMapper;
import com.hjy.hall.dao.THallTakenumberMapper;
import com.hjy.hall.entity.THallQueue;
import com.hjy.hall.entity.THallTakenumber;
import com.hjy.system.dao.TSysRoleMapper;
import com.hjy.system.dao.TSysTokenMapper;
import com.hjy.system.dao.TSysUserMapper;
import com.hjy.system.entity.*;
import com.hjy.system.service.ShiroService;
import com.hjy.warning.dao.TWarnLnfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author liuchun
 * @Date 2019/3/30 22:18
 * @Version 1.0
 */
@Service
public class ShiroServiceImpl implements ShiroService {
    //12小时后失效
    private final static int EXPIRE = 12;

    @Autowired
    private TSysUserMapper tSysUserMapper;
    @Autowired
    private TSysRoleMapper tSysRoleMapper;
    @Autowired
    private TSysTokenMapper tSysTokenMapper;
    @Autowired
    private THallQueueMapper tHallQueueMapper;
    @Autowired
    private THallTakenumberMapper tHallTakenumberMapper;
    @Autowired
    private TWarnLnfoMapper tWarnLnfoMapper;

    /**
     * 根据userid查找角色
     *
     * @return User
     */
    @Override
    public TSysRole selectRoleByUserId(String pkUserId) {
        return tSysRoleMapper.selectRoleByUserId(pkUserId);
    }

    @Override
    public String selectRoleIdByUserId(String fkUserId) {
        return tSysRoleMapper.selectRoleIdByUserId(fkUserId);
    }

    @Override
    public void deleteToken(String tokenId) {
        tSysTokenMapper.deleteToken(tokenId);
    }
    /**
     * 更新最后一次登录时间
     */
    @Override
    public void updateLoginTime(String userId) {
        TSysUser user = new TSysUser();
        user.setPkUserId(userId);
        user.setLastLoginDate(new Date());
        tSysUserMapper.updateById(user);
    }

    @Override
    public Map<String, Object> selectIndexData(HttpServletResponse resp) {
        return null;
    }

    @Override
    public String selectIpByUsername(String username) {
        return tSysTokenMapper.selectIpByUsername(username);
    }

    @Override
    public String selectIpByTokenId(String tokenId) {
        return tSysTokenMapper.selectIpByTokenId(tokenId);

    }
    //维护接口
    @Override
    public void systemMaintain() {
        tHallQueueMapper.systemMaintain();
        tHallTakenumberMapper.deleteAll();
        tWarnLnfoMapper.systemMaintain();
        //清空文件
        String turnon = PropertiesUtil.getValue("test.whether.turn.on.httpClient");
        if(turnon.equals("true")){
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("ordinal","systemMaintain");
            String msg = null;
            try {
                String url = PropertiesUtil.getValue("httpClient.request.url");
                msg = HttpClient4.sendPost(url+"/systemMaintain",paramMap);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }

    }

    /**
     * 通过roleid查找权限码
     */
    @Override
    public List<String> selectPermsCodeByRole(String fkRoleId){
        return tSysRoleMapper.selectPermsCodeByRole(fkRoleId);
    }
    @Override
    public List<TSysPerms> selectPermsByRole(String fkRoleId) {
        return tSysRoleMapper.selectPermsByRole(fkRoleId);
    }
    /**
     * 根据username查找用户
     * @return User
     */
    @Override
    public TSysUser selectUserByUsername(String username) {
        return tSysUserMapper.selectUserByUsername(username);
    }

    /**
     * 生成一个token
     *@return Result
     */
    @Override
    public Map<String, Object> createToken(TSysUser tSysUser, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        //生成一个token（session）
        String token = TokenGenerator.generateValue();
        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = DateUtil.addTime(now,1);
        //先删除
        tSysTokenMapper.deleteTokenByIp(tSysUser.getIp());
        //再添加
        SysToken tokenEntity = new SysToken();
        tokenEntity.setFkUserId(tSysUser.getPkUserId());
        //保存token
        tokenEntity.setPkTokenId(token);
        tokenEntity.setUpdateTime(now);
        tokenEntity.setExpireTime(expireTime);
        tokenEntity.setUsername(tSysUser.getUsername());
        tokenEntity.setPassword(tSysUser.getPassword());
        tokenEntity.setIp(tSysUser.getIp());
        tokenEntity.setFullName(tSysUser.getFullName());
        tokenEntity.setPoliceNum(tSysUser.getPoliceNum());
        tSysTokenMapper.insertToken(tokenEntity);
        result.put("token", token);
        result.put("expire", expireTime);
        return result;
    }


    @Override
    public SysToken findByToken(String accessToken) {
        return tSysTokenMapper.findByToken(accessToken);
    }

}
