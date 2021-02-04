package com.hjy.synthetical.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.annotation.OperLog;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.exception.FebsException;
import com.hjy.common.task.ObjectAsyncTask;
import com.hjy.synthetical.entity.TSyntheticalMakecard;
import com.hjy.synthetical.service.TSyntheticalMakecardService;
import com.hjy.system.entity.ActiveUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * (TSyntheticalMakecard)表控制层
 *
 * @author liuchun
 * @since 2020-08-17 09:53:43
 */
@Slf4j
@RestController
public class TSyntheticalMakecardController {
    /**
     * 服务对象
     */
    @Autowired
    private TSyntheticalMakecardService tHallMakecardService;

    /**
     * 1添加制证信息
     * @return 单条数据
     */
    @OperLog(operModul = "综合管理-综合管理-制证管理",operType = "添加",operDesc = "添加制证信息")
    @PostMapping("/synthetical/makeCard/add")
    public CommonResult tHallMakecardAdd(@RequestBody TSyntheticalMakecard tHallMakecard,HttpSession session) throws FebsException {
        try {
            //
            int i = tHallMakecardService.makeCardAdd(tHallMakecard,session);
            if(i == 1 ){
                return new CommonResult(200, "success", "制证信息添加成功!", null);
            } else if(i == 2) {
                return new CommonResult(445, "error", "本人姓名或公司名以及证件号不能为空!", null);
            }else {
                return new CommonResult(444, "error", "制证信息添加失败！", null);
            }
        } catch (Exception e) {
            String message = "制证信息添加失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 2 查询所有数据
     *
     * @return 所有数据
     */
    @OperLog(operModul = "综合管理-综合管理-制证管理",operType = "查看",operDesc = "查看制证列表")
    @RequiresPermissions({"makeCard:view"})
    @GetMapping("/synthetical/makeCard/list")
    public CommonResult tHallMakecardList() throws FebsException {
        try {
            //
            List<TSyntheticalMakecard> tHallMakecardList = tHallMakecardService.selectAll();
            return new CommonResult(200, "success", "查询数据成功!", tHallMakecardList);
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
    @PostMapping("/synthetical/makeCard/listByEntity")
    public CommonResult tHallMakecardListByEntity(@RequestBody TSyntheticalMakecard tHallMakecard) throws FebsException {
        try {
            //
            List<TSyntheticalMakecard> tHallMakecardList = tHallMakecardService.selectAllByEntity(tHallMakecard);
            return new CommonResult(200, "success", "查询数据成功!", tHallMakecardList);
        } catch (Exception e) {
            String message = "查询数据失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 制作完成
     * @param tHallMakecard 实体对象
     * @return 修改结果
     */
    @OperLog(operModul = "综合管理-综合管理-制证管理",operType = "制证完成",operDesc = "证件制作完成")
    @PutMapping("/synthetical/makeCard/makeComplete")
    public CommonResult makeComplete(@RequestBody TSyntheticalMakecard tHallMakecard, HttpSession session) throws FebsException {
        try {
            return tHallMakecardService.makeComplete(tHallMakecard,session);
        } catch (Exception e) {
            String message = "修改失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 批量制作完成
     * @param param 实体对象
     * @return 修改结果
     */
    @OperLog(operModul = "综合管理-综合管理-制证管理",operType = "制证完成",operDesc = "批量证件制作完成")
    @PutMapping("/synthetical/makeCard/makeComplete_batch")
    public CommonResult makeCompleteBatch(@RequestBody String param, HttpSession session) throws FebsException {
        try {
            return tHallMakecardService.makeCompleteBatch(param,session);
        } catch (Exception e) {
            String message = "修改失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 3 删除数据-领取、弃用
     * @return 删除结果
     */
    @OperLog(operModul = "综合管理-综合管理-制证管理",operType = "领取/弃用",operDesc = "制证领用/弃用")
    @DeleteMapping("/synthetical/makeCard/del")
    public CommonResult tHallMakecardDel(@RequestBody TSyntheticalMakecard tHallMakecard) throws FebsException {
        try {
            //
            return tHallMakecardService.tHallMakecardDel(tHallMakecard);
        } catch (Exception e) {
            String message = "数据删除失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 3 批量领取-删除数据-领取、弃用
     * @return 删除结果
     */
    @OperLog(operModul = "综合管理-综合管理-制证管理",operType = "领取/弃用",operDesc = "制证领用/弃用")
    @DeleteMapping("/synthetical/makeCard/del_batch")
    public CommonResult tHallMakecardDelBatch(@RequestBody String param) throws FebsException {
        try {
            //
            return tHallMakecardService.tHallMakecardDelBatch(param);
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
    @OperLog(operModul = "综合管理-综合管理-制证管理",operType = "查看",operDesc = "查看制证详情")
    @PostMapping("/synthetical/makeCard/getOne")
    public CommonResult tHallMakecardgetOne(@RequestBody String parm) throws FebsException {
        JSONObject jsonObject = JSON.parseObject(parm);
        String idStr = String.valueOf(jsonObject.get("pkCardId"));
        try {
            //
            TSyntheticalMakecard tHallMakecard = tHallMakecardService.selectById(idStr);
            return new CommonResult(200, "success", "数据获取成功!", tHallMakecard);
        } catch (Exception e) {
            String message = "数据获取失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 4 修改数据
     * @param tHallMakecard 实体对象
     * @return 修改结果
     */
    @OperLog(operModul = "综合管理-综合管理-制证管理",operType = "修改",operDesc = "修改制证信息")
    @PutMapping("/synthetical/makeCard/update")
    public CommonResult tHallMakecardUpdate(@RequestBody TSyntheticalMakecard tHallMakecard) throws FebsException {
        try {
            //
            tHallMakecardService.updateById(tHallMakecard);
            return new CommonResult(200, "success", "修改成功!", null);
        } catch (Exception e) {
            String message = "修改失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

}