package com.hjy.hall.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.annotation.OperLog;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.exception.FebsException;
import com.hjy.common.utils.page.PageResult;
import com.hjy.hall.entity.THallJidongche;
import com.hjy.hall.service.THallJidongcheService;
import com.hjy.system.service.TSysBusinesstypeService;
import com.hjy.system.service.TSysDeptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * (THallJidongche)表控制层
 *
 * @author liuchun
 * @since 2020-07-27 15:51:24
 */
@Slf4j
@RestController
public class THallJidongcheController {
    /**
     * 服务对象
     */
    @Autowired
    private THallJidongcheService tHallJidongcheService;
    @Autowired
    private TSysDeptService tSysDeptService;
    @Autowired
    private TSysBusinesstypeService tSysBusinesstypeService;

    /**
     * 1 跳转到新增页面
     */
    @GetMapping(value = "/hall/jidongche/addPage")
    public CommonResult tHallJidongcheAddPage() throws FebsException {
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
     * @param tHallJidongche 实体对象
     * @return 新增结果
     */
    @OperLog(operModul = "大厅管理-业务办理-机动车业务申请告知书",operType = "添加",operDesc = "新增机动车业务告知书")
    @PostMapping("/hall/jidongche/add")
    public CommonResult tHallJidongcheAdd(@RequestBody THallJidongche tHallJidongche) throws FebsException {
        try {
            //
            tHallJidongcheService.insert(tHallJidongche);
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
    @OperLog(operModul = "大厅管理-业务办理-机动车业务申请告知书",operType = "查看",operDesc = "查看机动车业务告知书列表")
    @RequiresPermissions({"jidongche:view"})
    @GetMapping("/hall/jidongche/listPage")
    public CommonResult tHallJidongcheListPage() throws FebsException {
        try {
            //
            PageResult result = tHallJidongcheService.selectAllPage();
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
    @OperLog(operModul = "大厅管理-业务办理-机动车业务申请告知书",operType = "查询",operDesc = "条件查询机动车业务告知书")
//    @RequiresPermissions({"jidongche:view"})
    @PostMapping("/hall/jidongche/list")
    public CommonResult tHallJidongcheList(@RequestBody String param) throws FebsException {
        try {
            //
            PageResult result = tHallJidongcheService.selectAll(param);
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
    @OperLog(operModul = "大厅管理-业务办理-机动车业务申请告知书",operType = "删除",operDesc = "删除机动车业务告知书")
    @RequiresPermissions({"jidongche:view"})
    @DeleteMapping("/hall/jidongche/del")
    public CommonResult tHallJidongcheDel(@RequestBody String parm) throws FebsException {
        JSONObject jsonObject = JSON.parseObject(parm);
        String idStr = String.valueOf(jsonObject.get("pk_jidongche_id"));
        try {
            //
            tHallJidongcheService.deleteById(idStr);
            return new CommonResult(200, "success", "数据删除成功!", null);
        } catch (Exception e) {
            String message = "数据删除失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 4 通过主键查询单条数据
     *
     * @return 单条数据
     */
    @OperLog(operModul = "大厅管理-业务办理-机动车业务申请告知书",operType = "详情",operDesc = "查看机动车业务告知书详情")
    @PostMapping("/hall/jidongche/getOne")
    public CommonResult tHallJidongchegetOne(@RequestBody String parm) throws FebsException {
        JSONObject jsonObject = JSON.parseObject(parm);
        String idStr = String.valueOf(jsonObject.get("pkId"));
        try {
            //
            THallJidongche tHallJidongche = tHallJidongcheService.selectById(idStr);
            return new CommonResult(200, "success", "数据获取成功!", tHallJidongche);
        } catch (Exception e) {
            String message = "数据获取失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 4 修改数据
     *
     * @param param 实体对象
     * @return 修改结果
     */
    @OperLog(operModul = "大厅管理-业务办理-机动车业务申请告知书",operType = "修改",operDesc = "修改机动车业务告知书")
    @RequiresPermissions({"jidongche:view"})
    @PutMapping("/hall/jidongche/update")
    public CommonResult tHallJidongcheUpdate(@RequestBody String param) throws FebsException {
        try {
            //
            tHallJidongcheService.updateById(param);
            return new CommonResult(200, "success", "修改成功!", null);
        } catch (Exception e) {
            String message = "修改失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

}