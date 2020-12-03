package com.hjy.system.controller;

import com.hjy.common.annotation.OperLog;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.exception.FebsException;
import com.hjy.system.entity.ActiveUser;
import com.hjy.system.service.TSysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 *
 * @author liuchun
 * @since 2020-07-24 17:05:59
 */
@Slf4j
@RestController
public class TSysSetController {
    /**
     * 服务对象
     */
    @Autowired
    private TSysUserService tSysUserService;
    /**
     * 2 查询所有数据
     * @return 所有数据
     */
    @OperLog(operModul = "首页-快捷菜单设置-快捷菜单设置",operType = "查看",operDesc = "查看快捷菜单")
    @PostMapping("/system/quickSet/list")
    public CommonResult quickSetList(HttpSession session) throws FebsException{
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        try {
            CommonResult commonResult = tSysUserService.selectQuickSetByUser(activeUser.getUserId());
            return commonResult;
        } catch (Exception e) {
            String message = "查询数据失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 3 快捷菜单设置
     */
    @PostMapping("/system/quickSet/set")
    public CommonResult quickSet(@RequestBody String param) throws FebsException{
        try {
            CommonResult commonResult = tSysUserService.quickSet(param);
            return commonResult;
        } catch (Exception e) {
            String message = "快捷菜单设置失败！";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

}