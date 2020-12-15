package com.hjy.list.controller;

import com.hjy.common.annotation.OperLog;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.exception.FebsException;
import com.hjy.common.utils.page.PageResult;
import com.hjy.list.entity.TListInfo;
import com.hjy.list.service.TListInfoService;
import com.hjy.system.entity.ActiveUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * (TListInfo)表控制层
 *
 * @author liuchun
 * @since 2020-08-05 12:45:38
 */
@Slf4j
@RestController
public class TListInfoController {
    /**
     * 服务对象
     */
    @Autowired
    private TListInfoService tListInfoService;

    /**
     * 1 跳转到新增页面
     */
//     @RequiresPermissions({"list:addPage"})
     @GetMapping(value = "/list/info/addPage")
     public CommonResult tListInfoAddPage() throws FebsException {
        try {
            //
            return new CommonResult(200,"success","成功!",null);
        } catch (Exception e) {
            String message = "失败";
            log.error(message, e);
            throw new FebsException(message);
        }
     }
    /**
     * 1 新增数据
     * @param tListInfo 实体对象
     *  ,@RequestParam(value = "file", required = false) MultipartFile[] files,HttpSession session
     * @return 新增结果
     *
     */
    @OperLog(operModul = "黑名单管理-人员黑名单-黑红名单申请表",operType = "添加",operDesc = "新增黑红名单信息")
    @RequiresPermissions({"list:view"})
//    @RequiresPermissions({"list:add"})
    @PostMapping("/list/info/add")
    public CommonResult tListInfoAdd(@RequestBody TListInfo tListInfo,HttpSession session) throws FebsException{
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        tListInfo.setOperator(activeUser.getFullName());
        try {
            //
            tListInfoService.insert(tListInfo);
//            tListInfoService.insertFile(tListInfo,files);
            return new CommonResult(200,"success","黑/红名单添加成功!",null);
        } catch (Exception e) {
            String message = "黑/红名单添加失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 2 查询所有数据
     * @return 所有数据
     */
    @OperLog(operModul = "黑名单管理-人员黑名单-黑红名单查询",operType = "查询",operDesc = "查询黑红名单列表")
    @RequiresPermissions({"list:view"})
    @PostMapping("/list/info/list")
    public CommonResult tListInfoList(@RequestBody String param) throws FebsException{
        try {
            //
            PageResult result = tListInfoService.selectAllPage(param);
            return new CommonResult(200,"success","查询数据成功!",result);
        } catch (Exception e) {
            String message = "查询数据失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 先添加到审批页面
     * @return 删除结果
     */
    @OperLog(operModul = "黑名单管理-人员黑名单-黑红名单查询",operType = "删除",operDesc = "申请删除黑红名单信息")
    @RequiresPermissions({"list:view"})
//    @RequiresPermissions({"list:del"})
    @DeleteMapping("/list/info/delApproval")
    public CommonResult delApproval(@RequestBody String param,HttpSession session) throws FebsException{
        try {
            CommonResult commonResult = tListInfoService.delApproval(param,session);
            return commonResult;
        } catch (Exception e) {
            String message = "申请删除数据失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 3 审批删除
     * @return 删除结果
     */
    @OperLog(operModul = "黑名单管理-人员黑名单-黑红名单审批",operType = "删除",operDesc = "删除黑红名单信息")
    @RequiresPermissions({"approval:view"})
//    @RequiresPermissions({"list:del"})
    @DeleteMapping("/list/info/del/approval")
    public CommonResult tListInfoDel(@RequestBody TListInfo tListInfo) throws FebsException{
        try {
            CommonResult commonResult = tListInfoService.tListInfoDel(tListInfo);
            return commonResult;
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
    @OperLog(operModul = "黑名单管理-人员黑名单-黑红名单查询",operType = "查看",operDesc = "查看黑红名单详情")
    @PostMapping("/list/info/getOne")
    public CommonResult tListInfogetOne(@RequestBody String param) throws FebsException{
        try {
            //
            CommonResult commonResult = tListInfoService.selectById(param);
            return commonResult;
        } catch (Exception e) {
            String message = "数据获取失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 4 修改数据
     * @param tListInfo 实体对象
     * @return 修改结果
     */
    @OperLog(operModul = "黑名单管理-人员黑名单-黑红名单查询",operType = "修改",operDesc = "修改黑红名单信息")
    @RequiresPermissions({"list:view"})
//    @RequiresPermissions({"list:update"})
    @PutMapping("/list/info/update")
    public CommonResult tListInfoUpdate(@RequestBody TListInfo tListInfo) throws FebsException{
        try {
            //
            int i = tListInfoService.updateById(tListInfo);
            if(i > 0){
                return new CommonResult(200,"success","修改成功!",null);
            }else {
                return new CommonResult(444,"error","修改失败!",null);
            }
        } catch (Exception e) {
            String message = "修改失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 5 审批
     * @param tListInfo 实体对象
     * @return 修改结果
     */
    @OperLog(operModul = "黑名单管理-人员黑名单-黑红名单审批",operType = "审批",operDesc = "黑红名单审批")
    @RequiresPermissions({"approval:view"})
//    @RequiresPermissions({"approval:add"})
    @PutMapping("/list/info/approval")
    public CommonResult tListInfoApproval(@RequestBody TListInfo tListInfo, HttpSession session) throws FebsException{
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        tListInfo.setApprovalPeople(activeUser.getFullName());
        try {
            //
            tListInfoService.updateById(tListInfo);
            return new CommonResult(200,"success","审批成功!",null);
        } catch (Exception e) {
            String message = "审批失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 5 待审批
     * @return 审批列表
     */
    @OperLog(operModul = "黑名单管理-人员黑名单-黑红名单审批",operType = "查看",operDesc = "待审批黑红名单列表")
    @RequiresPermissions({"approval:view"})
    @PostMapping("/list/info/waitApproval")
    public CommonResult tListInfoApprovalList(@RequestBody String param) throws FebsException{
        try {
            //
            PageResult result = tListInfoService.selectWaitApproval(param);
            return new CommonResult(200,"success","待审批记录获取成功!",result);
        } catch (Exception e) {
            String message = "待审批记录获取失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 综合查询
     */
    @OperLog(operModul = "黑名单管理-人员黑名单-黑红名单查询",operType = "查看",operDesc = "综合查询")
    @RequiresPermissions({"approval:view"})
    @PostMapping("/list/info/syntheticalSelect")
    public CommonResult syntheticalSelect(@RequestBody String param) throws FebsException{
        try {
            //
            CommonResult commonResult = tListInfoService.syntheticalSelect(param);
            return commonResult;
        } catch (Exception e) {
            String message = "待审批记录获取失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}