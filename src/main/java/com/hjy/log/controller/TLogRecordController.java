package com.hjy.log.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.annotation.OperLog;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.exception.FebsException;
import com.hjy.common.utils.page.PageResult;
import com.hjy.log.entity.TLogRecord;
import com.hjy.log.service.TLogRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * (TLogRecord)表控制层
 *
 * @author liuchun
 * @since 2020-10-15 09:47:51
 */
@Slf4j
@RestController
public class TLogRecordController {
    /**
     * 服务对象
     */
    @Autowired
    private TLogRecordService tLogRecordService;

    /**
     * 2 通过实体查询所有数据
     *
     * @return 所有数据
     */
    @OperLog(operModul = "日志管理-操作日志-日志管理",operType = "查看",operDesc = "查看日志记录列表")
    @RequiresPermissions({"log:view"})
    @PostMapping("/log/record/list")
    public CommonResult tLogRecordList(@RequestBody String param) throws FebsException {
        try {
            //
            PageResult result = tLogRecordService.selectAllPage(param);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PageResult",result);
            return new CommonResult(200,"success","查询数据成功!",jsonObject);
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
    @DeleteMapping("/tLogRecord/del")
    public CommonResult tLogRecordDel(@RequestBody String parm) throws FebsException {
        JSONObject jsonObject = JSON.parseObject(parm);
        String idStr = String.valueOf(jsonObject.get("pk_id"));
        try {
            //
            tLogRecordService.deleteById(idStr);
            return new CommonResult(200, "success", "数据删除成功!", null);
        } catch (Exception e) {
            String message = "数据删除失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

}