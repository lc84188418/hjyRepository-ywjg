package com.hjy.hall.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.annotation.OperLog;
import com.hjy.common.exception.FebsException;
import com.hjy.common.utils.JsonUtil;
import com.hjy.common.utils.page.PageResult;
import com.hjy.hall.entity.THallJiashizheng;
import com.hjy.hall.service.THallJiashizhengService;
import com.hjy.system.service.TSysBusinesstypeService;
import com.hjy.system.service.TSysDeptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hjy.common.domin.CommonResult;

import java.util.List;

/**
 * (THallJiashizheng)表控制层
 *
 * @author liuchun
 * @since 2020-07-27 14:17:45
 */
@Slf4j
@RestController
public class THallJiashizhengController {
    /**
     * 服务对象
     */
    @Autowired
    private THallJiashizhengService tHallJiashizhengService;
    @Autowired
    private TSysDeptService tSysDeptService;
    @Autowired
    private TSysBusinesstypeService tSysBusinesstypeService;

    /**
     * 1 跳转到新增页面
     */
    @GetMapping(value = "/hall/jiashizheng/addPage")
    public CommonResult tHallJiashizhengAddPage() throws FebsException {
        try {
            //
            return new CommonResult(200, "success", "成功!", null);
        } catch (Exception e) {
            String message = "失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 1 新增数据
     *
     * @param tHallJiashizheng 实体对象
     * @return 新增结果
     */
    @OperLog(operModul = "大厅管理-业务办理-驾驶证业务申请告知书",operType = "添加",operDesc = "新增驾驶证业务告知书")
    @PostMapping("/hall/jiashizheng/add")
    public CommonResult tHallJiashizhengAdd(@RequestBody THallJiashizheng tHallJiashizheng) throws FebsException {
        try {
            //
            tHallJiashizhengService.insert(tHallJiashizheng);
            return new CommonResult(200, "success", "数据添加成功!", null);
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
    @OperLog(operModul = "大厅管理-业务办理-驾驶证业务申请告知书",operType = "查看",operDesc = "查看驾驶证业务告知书列表")
    @RequiresPermissions({"jiashizheng:view"})
    @GetMapping("/hall/jiashizheng/listPage")
    public CommonResult tHallJiashizhengListPage() throws FebsException {
        try {
            //
            PageResult result = tHallJiashizhengService.selectAllPage();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PageResult",result);
            List<String> deptNames = tSysDeptService.selectDeptUnit();
            jsonObject.put("deptName",deptNames);
            List<String> business = tSysBusinesstypeService.selectBusinessName();
            jsonObject.put("business",business);
            return new CommonResult(200, "success", "查询数据成功!", jsonObject);
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
    @OperLog(operModul = "大厅管理-业务办理-驾驶证业务申请告知书",operType = "查询",operDesc = "条件查询驾驶证业务告知书")
    @PostMapping("/hall/jiashizheng/list")
    public CommonResult tHallJiashizhengListPage(@RequestBody String param) throws FebsException {
        try {
            //
            PageResult result = tHallJiashizhengService.selectAll(param);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PageResult",result);
            List<String> deptNames = tSysDeptService.selectDeptUnit();
            jsonObject.put("deptName",deptNames);
            List<String> business = tSysBusinesstypeService.selectBusinessName();
            jsonObject.put("business",business);
            return new CommonResult(200, "success", "查询数据成功!", jsonObject);
        } catch (Exception e) {
            String message = "查询数据失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 3 删除数据
     *
     * @return 删除结果
     */
    @OperLog(operModul = "大厅管理-业务办理-驾驶证业务申请告知书",operType = "删除",operDesc = "删除驾驶证业务告知书")
    @RequiresPermissions({"jiashizheng:view"})
    @DeleteMapping("/hall/jiashizheng/del")
    public CommonResult tHallJiashizhengDel(@RequestBody String param) throws FebsException {
        JSONObject jsonObject = JSON.parseObject(param);
        String idStr = JsonUtil.getStringParam(jsonObject,"pkJiashiId");
        try {
            //
            tHallJiashizhengService.deleteById(idStr);
            return new CommonResult(200, "success", "数据删除成功!", null);
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
    @OperLog(operModul = "大厅管理-业务办理-驾驶证业务申请告知书",operType = "查看",operDesc = "查看驾驶证业务告知书详情")
    @PostMapping("/hall/jiashizheng/getOne")
    public CommonResult tHallJiashizhenggetOne(@RequestBody String param) throws FebsException {
        JSONObject jsonObject = JSON.parseObject(param);
        String idStr = String.valueOf(jsonObject.get("pkId"));
        try {
            //
            THallJiashizheng tHallJiashizheng = tHallJiashizhengService.selectById(idStr);
            return new CommonResult(200, "success", "数据获取成功!", tHallJiashizheng);
        } catch (Exception e) {
            String message = "数据获取失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 4 修改数据
     * @param param
     * @return 修改结果
     */
    @OperLog(operModul = "大厅管理-业务办理-驾驶证业务申请告知书",operType = "修改",operDesc = "修改驾驶证业务告知书")
    @RequiresPermissions({"jiashizheng:view"})
    @PutMapping("/hall/jiashizheng/update")
    public CommonResult tHallJiashizhengUpdate(@RequestBody String param) throws FebsException {
        try {
            //
            tHallJiashizhengService.updateById(param);
            return new CommonResult(200, "success", "修改成功!", null);
        } catch (Exception e) {
            String message = "修改失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

}