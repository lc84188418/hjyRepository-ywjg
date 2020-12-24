package com.hjy.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.utils.IDUtils;
import com.hjy.common.utils.JsonUtil;
import com.hjy.common.utils.led.*;
import com.hjy.system.dao.TSysBusinesstypeMapper;
import com.hjy.system.entity.ActiveUser;
import com.hjy.system.entity.TSysBusinesstype;
import com.hjy.system.entity.TSysWindow;
import com.hjy.system.dao.TSysWindowMapper;
import com.hjy.system.service.TSysWindowService;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * (TSysWindow)表服务实现类
 *
 * @author liuchun
 * @since 2020-07-28 14:56:45
 */
@Service
public class TSysWindowServiceImpl implements TSysWindowService {
    @Autowired
    private TSysWindowMapper tSysWindowMapper;
    @Autowired
    private TSysBusinesstypeMapper tSysBusinesstypeMapper;

    /**
     * 新增数据
     *
     * @param parm
     * @return 实例对象
     */
    @Transactional()
    @Override
    public CommonResult insert(String parm) throws Exception{
        List<String> ipList = tSysWindowMapper.selectAllIp();
        String msg = null;
        int code = 0;
        TSysWindow tSysWindow = new TSysWindow();
        JSONObject jsonObject = JSON.parseObject(parm);
        String deptName = JsonUtil.getStringParam(jsonObject,"deptName");
        String windowName = JsonUtil.getStringParam(jsonObject,"windowName");
        String ip = JsonUtil.getStringParam(jsonObject,"ip");
        if(ip != null){
            if(ipList.contains(ip)){
                tSysWindow.setIp("127.0.0.1");
                msg = "窗口数据添加成功，ip:"+ip+"已被分配，已默认为127.0.0.1，该窗口叫号不能正常使用！";
                code = 201;
            }else {
                tSysWindow.setIp(ip);
                msg = "窗口数据添加成功!";
                code = 200;
            }
        }else {
            return new CommonResult(445,"error","ip不能为空",null);
        }
        if(windowName != null){
            if(windowName.length()>5){
                return new CommonResult(446,"error","窗口名过长，最大值为5",null);
            }
        }
        tSysWindow.setPkWindowId(IDUtils.getUUID());
        tSysWindow.setDeptName(deptName);
        tSysWindow.setWindowName(windowName);
        String operatorPeople = JsonUtil.getStringParam(jsonObject,"operatorPeople");
        tSysWindow.setOperatorPeople(operatorPeople);
        String controlCard = JsonUtil.getStringParam(jsonObject,"controlCard");
        tSysWindow.setControlCard(controlCard);
        String branchNumber = JsonUtil.getStringParam(jsonObject,"branchNumber");
        tSysWindow.setBranchNumber(branchNumber);
        String registrationWindow = JsonUtil.getStringParam(jsonObject,"registrationWindow");
        tSysWindow.setRegistrationWindow(registrationWindow);
        String com = JsonUtil.getStringParam(jsonObject,"com");
        tSysWindow.setCom(com);
        tSysWindow.setServiceStatus(1);
        //业务类型
        JSONArray jsonArray = jsonObject.getJSONArray("businessTypes");
        String businesstypes = jsonArray.toString();
        List<TSysBusinesstype> businesstypeList = JSONArray.parseArray(businesstypes, TSysBusinesstype.class);
        if(businesstypeList != null){
            StringBuffer businessType = new StringBuffer();
            //先排序
            Collections.sort(businesstypeList, new Comparator<TSysBusinesstype>() {
                @Override
                public int compare(TSysBusinesstype o1, TSysBusinesstype o2) {
                    //升序
                    return o1.getTypeLevel().compareTo(o2.getTypeLevel());
                }
            });
            List<TSysBusinesstype> businesstypeList2 = businesstypeList;
            for(TSysBusinesstype obj:businesstypeList2){
                if(obj.getTypeLevel().equals("0")){
                    businessType.append(obj.getTypeName());
                }else {
                    businessType.append("/"+obj.getTypeName());
                }
            }
            tSysWindow.setBusinessType(businessType.toString());
        }
        int i = tSysWindowMapper.insertSelective(tSysWindow);
        if(i>0){
            return new CommonResult(code,"success",msg,null);
        }else {
            return new CommonResult(444,"error","窗口数据添加失败!",null);
        }

    }

    /**
     * 修改数据
     * @param param 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public CommonResult updateById(String param, HttpSession session) throws Exception{
        List<String> ipList = tSysWindowMapper.selectAllIp();
        String msg = null;
        int code = 0;
        TSysWindow tSysWindow = new TSysWindow();
        JSONObject jsonObject = JSON.parseObject(param);
        String pkWindowId = JsonUtil.getStringParam(jsonObject,"pkWindowId");
        //旧ip地址 123 null
        String oldIp = tSysWindowMapper.selectIpByPkid(pkWindowId);
        String deptName = JsonUtil.getStringParam(jsonObject,"deptName");
        String windowName = JsonUtil.getStringParam(jsonObject,"windowName");
        //新ip地址456 123 null
        String ip = JsonUtil.getStringParam(jsonObject,"ip");
        if(ip != null){
            if(!ip.equals(oldIp) && ipList.contains(ip)){
                tSysWindow.setIp("127.0.0.1");
                msg = "窗口数据修改成功，ip:"+ip+"已被分配，已默认为127.0.0.1，该窗口叫号不能正常使用！";
                code = 201;
            }else{
                tSysWindow.setIp(ip);
                //就是修改除ip以外的字段值
                msg = "窗口数据修改成功！";
                code = 200;
            }
        }else{
            return new CommonResult(445,"error","ip不能为空",null);
        }
        tSysWindow.setPkWindowId(pkWindowId);
        tSysWindow.setDeptName(deptName);
        tSysWindow.setWindowName(windowName);
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        tSysWindow.setOperatorPeople(activeUser.getFullName());
        String controlCard = JsonUtil.getStringParam(jsonObject,"controlCard");
        tSysWindow.setControlCard(controlCard);
        String branchNumber = JsonUtil.getStringParam(jsonObject,"branchNumber");
        tSysWindow.setBranchNumber(branchNumber);
        String registrationWindow = JsonUtil.getStringParam(jsonObject,"registrationWindow");
        tSysWindow.setRegistrationWindow(registrationWindow);
        String com = JsonUtil.getStringParam(jsonObject,"com");
        tSysWindow.setCom(com);
        //业务类型
        JSONArray jsonArray = jsonObject.getJSONArray("businessTypes");
        String businesstypes = jsonArray.toString();
        List<TSysBusinesstype> businesstypeList = JSONArray.parseArray(businesstypes, TSysBusinesstype.class);
        if(businesstypeList != null){
            StringBuffer businessType = new StringBuffer();
            //先排序
            Collections.sort(businesstypeList, new Comparator<TSysBusinesstype>() {
                @Override
                public int compare(TSysBusinesstype o1, TSysBusinesstype o2) {
                    //升序
                    return o1.getTypeLevel().compareTo(o2.getTypeLevel());
                }
            });
            List<TSysBusinesstype> businesstypeList2 = businesstypeList;
            for(TSysBusinesstype obj:businesstypeList2){
                if(obj.getTypeLevel().equals("0")){
                    businessType.append(obj.getTypeName());
                }else {
                    businessType.append("/"+obj.getTypeName());
                }
            }
            tSysWindow.setBusinessType(businessType.toString());
        } else {
            tSysWindow.setBusinessType(null);
        }
        int i = tSysWindowMapper.updateById(tSysWindow);
        if(i>0){
            return new CommonResult(code,"success",msg,null);
        }else {
            return new CommonResult(444,"error","窗口数据修改失败!",null);
        }
    }
    @Override
    public Map<String, Object> tSysWindowgetOne(String param) {
        Map<String, Object> map = new HashMap<>();
        JSONObject resultJson = new JSONObject();
        JSONObject jsonObject = JSON.parseObject(param);
        String pkWindowId = String.valueOf(jsonObject.get("pk_id"));
        TSysWindow window = tSysWindowMapper.selectById(pkWindowId);
        if(window != null){
            String business = window.getBusinessType();
            if(!StringUtils.isEmpty(business)){
                Map<String, Object> tempmap = new HashMap<>();
                String [] strings = business.split("/");
                for (int i = 0 ;i <strings.length ;i++) {
                    tempmap.put(String.valueOf(i),strings[i]);
                }
                resultJson.put("business",tempmap);
            }
            List<String> stringList = tSysBusinesstypeMapper.selectBusinessName();
            resultJson.put("window",window);
            resultJson.put("businesss",stringList);
            map.put("code",200);
            map.put("status", "success");
            map.put("msg", "获取窗口数据成功");
            map.put("data", resultJson);
            return map;
        }else {
            map.put("code",445);
            map.put("status", "error");
            map.put("msg", "获取窗口数据失败");
            map.put("data", null);
            return map;
        }
    }
    //暂停服务
    @Transactional()
    @Override
    public CommonResult stopService(HttpSession session) throws Exception {
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        String ip = activeUser.getIp();
        TSysWindow window = tSysWindowMapper.selectByIp(ip);
        if(window != null){
            //unicode 暂停服务=\u6682\u505c\u670d\u52a10
//            String msg = "你好123";
            Integer serviceStatus = window.getServiceStatus();
            if(serviceStatus != null && serviceStatus == 0){
                window.setServiceStatus(1);
                //
//                msg = window.getWindowName();
            }
            if(serviceStatus != null && serviceStatus == 1){
                window.setServiceStatus(0);
            }
            int i = tSysWindowMapper.stopService(window);
            if(i > 0){
                /**
                 * 发送提示到窗口上
                 */
                if(!StringUtils.isEmpty(window.getControlCard())){
//                    int nCardId = Integer.parseInt(window.getControlCard());
                    //在窗口LED屏上展示暂停服务的提示
                    String msg = "你好123";
                    byte[] bytes = LEDUtil.sendSingleColorText(msg);
                    //C4 E3 BA C3 31 32  33 00
                    byte[] bytes1 = {(byte) 0xC4, (byte) 0xE3, (byte) 0xBA, (byte) 0xC3,31,32,33,00};
                    String temp = LEDUtil.sendSingleColorText2(bytes);
                    String temp2 = LEDUtil.sendSingleColorText3(bytes);
                    String temp3 = temp.toUpperCase();
                    System.out.println("temp：" + temp);
                    System.out.println("temp2：" + temp2);
                    System.out.println("temp3：" + temp3);
                    PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(2,bytes,0);
                    PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(3,bytes1,0);
                    PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(4,temp,0);
                    PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(5,temp2,0);
                    PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(6,temp3,0);
                    PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(7,bytes1,0);
                    PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(8,temp,0);

                    return new CommonResult(200,"success","暂停服务成功!",null);
                }else {
                    return new CommonResult(446,"error","暂停服务成功！该窗口未配置控制卡地址，无法展示‘暂停服务’",null);
                }
            }else {
                return new CommonResult(444,"error","暂停服务失败!",null);
            }
        }else {
            return new CommonResult(445,"error","该窗口非业务窗口，操作无效!",null);
        }
    }

    /**
     * 通过ID查询单条数据
     *
     * @param pkWindowId 主键
     * @return 实例对象
     */
    @Override
    public TSysWindow selectById(Object pkWindowId) throws Exception{
        return this.tSysWindowMapper.selectById(pkWindowId);
    }

    @Override
    public TSysWindow selectByIp(String ip) {
        return tSysWindowMapper.selectByIp(ip);
    }
    /**
     * 通过主键删除数据
     * @param pkWindowId 主键
     * @return 是否成功
     */
    @Override
    public int deleteById(Object pkWindowId) throws Exception{
        return tSysWindowMapper.deleteById(pkWindowId);
    }

    /**
     * 查询多条数据
     * @return 对象列表
     */
    @Override
    public List<TSysWindow> selectAll() throws Exception{
        return this.tSysWindowMapper.selectAll();
    }
    /**
     * 通过实体查询多条数据
     * @return 对象列表
     */
    @Override
    public List<TSysWindow> selectAllByEntity(TSysWindow tSysWindow) throws Exception{
        return this.tSysWindowMapper.selectAllByEntity(tSysWindow);
    }

    @Override
    public List<String> selectWindowName() {
        return tSysWindowMapper.selectWindowName();
    }

    @Override
    public int updateOperatorPeople(TSysWindow tSysWindow) {
        return tSysWindowMapper.updateOperatorPeople(tSysWindow);
    }
    //通过主键查询该窗口配置的ip地址
    @Override
    public String selectIpByPkid(String pkWindowId) {
        return tSysWindowMapper.selectIpByPkid(pkWindowId);
    }
}