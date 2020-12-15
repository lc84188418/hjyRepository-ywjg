package com.hjy.system.controller;

import com.hjy.common.annotation.OperLog;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.exception.FebsException;
import com.hjy.system.entity.TSysBusinesstype;
import com.hjy.system.entity.TSysDept;
import com.hjy.system.entity.TSysWindow;
import com.hjy.system.service.TSysBusinesstypeService;
import com.hjy.system.service.TSysDeptService;
import com.hjy.system.service.TSysWindowService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * (TSysWindow)表控制层
 *
 * @author liuchun
 * @since 2020-07-28 14:56:45
 */
@Slf4j
@RestController
public class TSysWindowController {
    /**
     * 服务对象
     */
    @Autowired
    private TSysWindowService tSysWindowService;
    @Autowired
    private TSysBusinesstypeService tSysBusinesstypeService;
    @Autowired
    private TSysDeptService tSysDeptService;
    /**
     * 1 跳转到新增页面
     */
     @GetMapping(value = "/system/window/addPage")
     public CommonResult tSysWindowAddPage() throws FebsException {
        try {
            //查询所有业务类型
            List<TSysBusinesstype> businesstypeList = tSysBusinesstypeService.selectAll();
            JSONObject json = new JSONObject();
            List<String> businesstypes = new ArrayList<>();
            for(TSysBusinesstype obj:businesstypeList){
                businesstypes.add(obj.getTypeName());
            }
            json.put("businesstypeList",businesstypes);
            List<TSysDept> deptList = tSysDeptService.selectAll();
            json.put("deptList",deptList);
            return new CommonResult(200,"success","成功!",json);
        } catch (Exception e) {
            String message = "失败";
            log.error(message, e);
            throw new FebsException(message);
        }
     }
    /**
     * 1 新增数据
     * @param param
     * @return 新增结果
     */
    @OperLog(operModul = "系统管理-参数设置-窗口管理",operType = "添加",operDesc = "添加窗口信息")
    @RequiresPermissions({"window:view"})
//    @RequiresPermissions({"window:add"})
    @PostMapping("/system/window/add")
    public CommonResult tSysWindowAdd(@RequestBody String param ) throws FebsException{
        try {
            //添加窗口数据
            CommonResult commonResult = tSysWindowService.insert(param);
            return commonResult;
        } catch (Exception e) {
            String message = "数据添加失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 2 查询所有数据
     * @return 所有数据
     */
    @OperLog(operModul = "系统管理-参数设置-窗口管理",operType = "查看",operDesc = "查看窗口列表")
    @RequiresPermissions({"window:view"})
    @GetMapping("/system/window/list")
    public CommonResult tSysWindowList() throws FebsException{
        try {
            //
            List<TSysWindow> tSysWindowList = tSysWindowService.selectAll();
            List<String> depts = tSysDeptService.selectDeptUnit();
            List<String> business = tSysBusinesstypeService.selectBusinessName();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tSysWindowList",tSysWindowList);
            jsonObject.put("depts",depts);
            jsonObject.put("business",business);
            return new CommonResult(200,"success","查询数据成功!",jsonObject);
        } catch (Exception e) {
            String message = "查询数据失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 2 通过实体查询所有数据
     * @return 所有数据
     */
    @PostMapping("/system/window/listByEntity")
    public CommonResult tSysWindowListByEntity(@RequestBody TSysWindow tSysWindow) throws FebsException{
        try {
            //
            List<TSysWindow> tSysWindowList = tSysWindowService.selectAllByEntity(tSysWindow);
            return new CommonResult(200,"success","查询数据成功!",tSysWindowList);
        } catch (Exception e) {
            String message = "查询数据失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 3 删除数据
     * @return 删除结果
     */
    @OperLog(operModul = "系统管理-参数设置-窗口管理",operType = "删除",operDesc = "删除窗口信息")
    @RequiresPermissions({"window:view"})
//    @RequiresPermissions({"window:del"})
    @DeleteMapping("/system/window/del")
    public CommonResult tSysWindowDel(@RequestBody String parm) throws FebsException{
        JSONObject jsonObject = JSON.parseObject(parm);
        String idStr=String.valueOf(jsonObject.get("pk_id"));
        try {
            //
            tSysWindowService.deleteById(idStr);
            return new CommonResult(200,"success","数据删除成功!",null);
        } catch (Exception e) {
            String message = "数据删除失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    
    /**
     * 4 通过主键查询单条数据
     * @return 单条数据
     */
    @OperLog(operModul = "系统管理-参数设置-窗口管理",operType = "查看",operDesc = "查看窗口详情")
    @PostMapping("/system/window/getOne")
    public Map<String,Object> tSysWindowgetOne(@RequestBody String param) throws FebsException{
        try {
            //
            Map<String,Object> resultMap = tSysWindowService.tSysWindowgetOne(param);
            return resultMap;
        } catch (Exception e) {
            String message = "数据获取失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    
    /**
     * 4 修改数据
     * @param param 实体对象
     * @return 修改结果
     */
    @OperLog(operModul = "系统管理-参数设置-窗口管理",operType = "修改",operDesc = "修改窗口信息")
    @RequiresPermissions({"window:view"})
//    @RequiresPermissions({"window:update"})
    @PutMapping("/system/window/update")
    public CommonResult tSysWindowUpdate(@RequestBody String param, HttpSession session) throws FebsException{
        try {
            //
            CommonResult commonResult = tSysWindowService.updateById(param,session);
            return commonResult;
        } catch (Exception e) {
            String message = "修改失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

}