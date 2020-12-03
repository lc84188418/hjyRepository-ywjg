package com.hjy.synthetical.controller;

import com.hjy.common.annotation.OperLog;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.exception.FebsException;
import com.hjy.synthetical.entity.TSyntheticalRecord;
import com.hjy.synthetical.service.TSyntheticalRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * (TSyntheticalRecord)表控制层
 *
 * @author liuchun
 * @since 2020-10-30 17:44:37
 */
@Slf4j
@RestController
public class TSyntheticalRecordController {
    /**
     * 服务对象
     */
    @Autowired
    private TSyntheticalRecordService tSyntheticalRecordService;


    /**
     * 1 新增数据
     * @RequestBody
     * @RequestParam(value = "file", required = false) MultipartFile[] files,
     */
    @OperLog(operModul = "综合管理-备案管理-备案采集",operType = "添加",operDesc = "添加备案记录")
    @RequiresPermissions({"record:add"})
    @PostMapping("/synthetical/record/add")
    public CommonResult tSyntheticalRecordAdd(@RequestBody TSyntheticalRecord tSyntheticalRecord, HttpSession session) throws FebsException {
        try {
            //
            CommonResult commonResult = tSyntheticalRecordService.insert(tSyntheticalRecord);
//            CommonResult commonResult = tSyntheticalRecordService.tSyntheticalRecordAdd(tSyntheticalRecord,files);
            return commonResult;
        } catch (Exception e) {
            String message = "数据添加失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 2 查询所有数据
     */
    @OperLog(operModul = "综合管理-备案管理-备案查询",operType = "查看",operDesc = "查看备案记录")
    @RequiresPermissions({"record:view"})
    @PostMapping("/synthetical/record/list")
    public CommonResult tSyntheticalRecordList(@RequestBody String param) throws FebsException {
        try {
            //
            CommonResult commonResult = tSyntheticalRecordService.tSyntheticalRecordList(param);
            return commonResult;
        } catch (Exception e) {
            String message = "查询数据失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 3 删除数据
     */
    @OperLog(operModul = "综合管理-备案管理-备案管理",operType = "删除",operDesc = "删除备案记录")
    @RequiresPermissions({"record:view"})
//    @RequiresPermissions({"record:del"})
    @DeleteMapping("/synthetical/record/del")
    public CommonResult tSyntheticalRecordDel(@RequestBody String param) throws FebsException {
        try {
            //
            CommonResult commonResult = tSyntheticalRecordService.deleteById(param);
            return commonResult;
        } catch (Exception e) {
            String message = "数据删除失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 4 通过主键查询单条数据
     */
    @OperLog(operModul = "综合管理-备案管理-备案管理",operType = "查看",operDesc = "查看备案记录详情")
    @PostMapping("/synthetical/record/getOne")
    public CommonResult tSyntheticalRecordgetOne(@RequestBody String param) throws FebsException {
        try {
            //
            CommonResult commonResult = tSyntheticalRecordService.selectById(param);
            return commonResult;
        } catch (Exception e) {
            String message = "数据获取失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 4 修改数据
     */
    @OperLog(operModul = "综合管理-备案管理-备案管理",operType = "修改",operDesc = "修改备案记录")
    @RequiresPermissions({"record:view"})
//    @RequiresPermissions({"record:update"})
    @PutMapping("/synthetical/record/update")
    public CommonResult tSyntheticalRecordUpdate(@RequestBody TSyntheticalRecord tSyntheticalRecord) throws FebsException {
        try {
            //
            CommonResult commonResult = tSyntheticalRecordService.updateById(tSyntheticalRecord);
            return commonResult;
        } catch (Exception e) {
            String message = "修改失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 叫号时，单位业务查询备案信息
     */
    @PostMapping("/hall/queue/getRecord")
    public CommonResult hallqueuegetRecord(@RequestBody String param) throws FebsException {
        try {
            CommonResult commonResult = tSyntheticalRecordService.hallqueuegetRecord(param);
            return commonResult;
        } catch (Exception e) {
            String message = "数据获取失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}