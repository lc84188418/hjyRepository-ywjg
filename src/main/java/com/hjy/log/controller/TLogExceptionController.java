package com.hjy.log.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.exception.FebsException;
import com.hjy.log.entity.TLogException;
import com.hjy.log.service.TLogExceptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * (TLogException)表控制层
 *
 * @author liuchun
 * @since 2020-10-15 09:47:49
 */
@Slf4j
@RestController
public class TLogExceptionController {
    /**
     * 服务对象
     */
    @Autowired
    private TLogExceptionService tLogExceptionService;

    /**
     * 1 跳转到新增页面
     */
    @GetMapping(value = "/tLogException/addPage")
    public CommonResult tLogExceptionAddPage() throws FebsException {
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
     * @param tLogException 实体对象
     * @return 新增结果
     */
    @PostMapping("/tLogException/add")
    public CommonResult tLogExceptionAdd(@RequestBody TLogException tLogException) throws FebsException {
        try {
            //
            tLogExceptionService.insert(tLogException);
            return new CommonResult(200, "success", "数据添加成功!", null);
        } catch (Exception e) {
            String message = "数据添加失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 2 查询所有数据
     *
     * @return 所有数据
     */
    @GetMapping("/tLogException/list")
    public CommonResult tLogExceptionList() throws FebsException {
        try {
            //
            List<TLogException> tLogExceptionList = tLogExceptionService.selectAll();
            return new CommonResult(200, "success", "查询数据成功!", tLogExceptionList);
        } catch (Exception e) {
            String message = "查询数据失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 2 通过实体查询所有数据
     *
     * @return 所有数据
     */
    @PostMapping("/tLogException/listByEntity")
    public CommonResult tLogExceptionListByEntity(@RequestBody TLogException tLogException) throws FebsException {
        try {
            //
            List<TLogException> tLogExceptionList = tLogExceptionService.selectAllByEntity(tLogException);
            return new CommonResult(200, "success", "查询数据成功!", tLogExceptionList);
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
    @DeleteMapping("/tLogException/del")
    public CommonResult tLogExceptionDel(@RequestBody String parm) throws FebsException {
        JSONObject jsonObject = JSON.parseObject(parm);
        String idStr = String.valueOf(jsonObject.get("pk_id"));
        try {
            //
            tLogExceptionService.deleteById(idStr);
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
    @PostMapping("/tLogException/getOne")
    public CommonResult tLogExceptiongetOne(@RequestBody String parm) throws FebsException {
        JSONObject jsonObject = JSON.parseObject(parm);
        String idStr = String.valueOf(jsonObject.get("pk_id"));
        try {
            //
            TLogException tLogException = tLogExceptionService.selectById(idStr);
            return new CommonResult(200, "success", "数据获取成功!", tLogException);
        } catch (Exception e) {
            String message = "数据获取失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 4 修改数据
     *
     * @param tLogException 实体对象
     * @return 修改结果
     */
    @PutMapping("/tLogException/update")
    public CommonResult tLogExceptionUpdate(@RequestBody TLogException tLogException) throws FebsException {
        try {
            //
            tLogExceptionService.updateById(tLogException);
            return new CommonResult(200, "success", "修改成功!", null);
        } catch (Exception e) {
            String message = "修改失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

}