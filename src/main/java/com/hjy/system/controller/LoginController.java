package com.hjy.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.annotation.OperLog;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.exception.FebsException;
import com.hjy.common.utils.*;
import com.hjy.common.utils.led.SerialPortManager;
import com.hjy.common.utils.led.appConfig;
import com.hjy.hall.service.THallTakenumberService;
import com.hjy.system.entity.ActiveUser;
import com.hjy.system.entity.TSysPerms;
import com.hjy.system.entity.TSysUser;
import com.hjy.system.entity.TSysWindow;
import com.hjy.system.service.ShiroService;
import com.hjy.system.service.TSysUserService;
import com.hjy.system.service.TSysWindowService;
import com.hjy.system.service.WebSocketService;
import gnu.io.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class LoginController {
    @Autowired
    private ShiroService shiroService;
    @Autowired
    private TSysUserService userService;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private TSysWindowService tSysWindowService;
    @Autowired
    private THallTakenumberService tHallTakenumberService;
    /**
     * 登录
     */
//    @OperLog(operModul = "系统-登录",operType = "登录",operDesc = "用户登录")
    @PostMapping("/login")
    public CommonResult login(@RequestBody TSysUser tSysUser,HttpServletRequest request,HttpSession session) throws UnknownHostException, SocketException {
        boolean rememberMe =true;
        String username = tSysUser.getUsername().trim();
        String passwordN0 = tSysUser.getPassword();
        String password = PasswordEncryptUtils.MyPasswordEncryptUtil(username,passwordN0);
        //用户信息
        TSysUser user = shiroService.selectUserByUsername(username);
        //账号不存在、密码错误
        if (user == null) {
            return new CommonResult(444,"error","账号不存在");
        } else if(!user.getPassword().equals(password)) {
            return new CommonResult(445,"error","密码错误");
        }else if(user.getEnableStatus().equals("0")){
            return new CommonResult(446,"error","该账户已被管理员禁用，请联系管理员");
        }else {
            //获取ip
            String ip= IPUtil.getIpAddress(request);
            user.setIp(ip);
            //生成token，并保存到数据库
            Map<String, Object> result = shiroService.createToken(user, session);
            TSysWindow window = new TSysWindow();
            window.setOperatorPeople(user.getFullName());
            window.setOperatorTime(new Date());
            window.setIp(ip);
            int i = tSysWindowService.updateOperatorPeople(window);
            return new CommonResult(200,"success","登陆成功",result);
        }
    }
    /**
     *登录成功
     * @return
     * throws FebsException
     */
    @OperLog(operModul = "系统-登录",operType = "登录",operDesc = "用户登录")
    @RequiresPermissions({"index"})
    @PostMapping("/index")
    public CommonResult index(HttpSession session,HttpServletRequest request,HttpServletResponse resp) throws FebsException, IOException {
        try {
            //获取当前登录用户
            ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
            //放入快捷菜单
            List<TSysPerms> ids = userService.selectPermsByUser(activeUser.getUserId());
            activeUser.setQuickMenu(ids);
            return new CommonResult(200,"success","获取数据成功!",activeUser);
        }catch (Exception e) {
            String message = "系统内部异常";
            log.error(message, e);
            throw new FebsException(message);
        }finally{
            //server处理逻辑
            webSocketService.IndexData(request);
        }
    }

    /**
     *退出系统
     * @return
     */
    @OperLog(operModul = "系统-退出",operType = "注销",operDesc = "注销登录退出系统")
    @PostMapping("/logout")
    public CommonResult logout(HttpSession session, HttpServletRequest request) throws FebsException {
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        //清空缓存
        //取出当前验证主体
        Subject subject = SecurityUtils.getSubject();
        //不为空，执行一次logout的操作，将session全部清空
        if (subject != null) {
            subject.logout();
        }
        try{
            //删除token
            String tokenId = TokenUtil.getRequestToken(request);
            shiroService.deleteToken(tokenId);
            //更新窗口操作时间
            String ip = shiroService.selectIpByTokenId(tokenId);
            if(!"127.0.0.1".equals(ip)){
                TSysWindow window = new TSysWindow();
                window.setIp(ip);
                window.setOperatorTime(new Date());
                tSysWindowService.updateOperatorPeople(window);
            }
            if(activeUser != null){
                //更新最后一次登录时间
                shiroService.updateLoginTime(activeUser.getUserId());
                /**
                 * 关闭串口
                 */
                String getOrdinalIp = PropertiesUtil.getValue("webSocket.callNum.ip");
                if(getOrdinalIp.contains(activeUser.getIp())){
                    SerialPort serial = appConfig.serial;
                    SerialPortManager.closePort(serial);
                }
            }
            return new CommonResult(200,"success","成功退出系统!",null);
        }catch (Exception e) {
            String message = "系统内部异常";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 用于测试的接口
     */
    @PostMapping("/idtest")
    public CommonResult test(@RequestBody String param){
        JSONObject resultJson = new JSONObject();
        JSONObject jsonObject = JSON.parseObject(param);
        //参数示列
        String lengthStr = JsonUtil.getStringParam(jsonObject,"lengthStr");

        //UUID
        String id = IDUtils.getUUID();
        resultJson.put("id",id);
        //
        String reasonTempBuffer = "";
        int lengthInt = lengthStr.length();
        int lengthInt2 = lengthStr.getBytes().length;
        resultJson.put("字节长度",lengthInt2);
        resultJson.put("实际长度",lengthInt);

        if(lengthInt > 4000){
            String reason = lengthStr.substring(0,3900);
            reasonTempBuffer = "此处文字过长，只显示部分内容，详情可在综合平台查看~~"+reason;
        }else {
            reasonTempBuffer = lengthStr;
        }
        resultJson.put("lengthStr",reasonTempBuffer);
        return new CommonResult(200,"success","成功",resultJson);
    }

    /**
     * 5 维护接口
     * @return 修改结果
     */
    @PostMapping("/system/maintain")
    public CommonResult systemMaintain() throws FebsException {
        try {
            shiroService.systemMaintain();
            return new CommonResult(200, "success", "维护成功!", null);
        } catch (Exception e) {
            String message = "维护成功";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}
