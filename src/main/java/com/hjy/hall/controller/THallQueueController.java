package com.hjy.hall.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.annotation.OperLog;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.exception.FebsException;
import com.hjy.common.utils.page.PageResult;
import com.hjy.hall.entity.Statistics;
import com.hjy.hall.entity.THallQueue;
import com.hjy.hall.entity.THallQueueCount;
import com.hjy.hall.service.THallQueueService;
import com.hjy.system.dao.TSysParamMapper;
import com.hjy.system.entity.TSysBusinesstype;
import com.hjy.system.entity.TSysParam;
import com.hjy.system.service.TSysBusinesstypeService;
import com.hjy.system.service.TSysWindowService;
import com.hjy.system.service.WebSocketService;
import com.hjy.tbk.service.TbkDrivinglicenseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (THallQueue)表控制层
 *
 * @author liuchun
 * @since 2020-07-29 14:33:19
 */
@Slf4j
@RestController
public class THallQueueController {
    /**
     * 服务对象
     */
    @Autowired
    private THallQueueService tHallQueueService;
    @Autowired
    private TSysBusinesstypeService tSysBusinesstypeService;
    @Autowired
    private TbkDrivinglicenseService tbkDrivinglicenseService;
    @Autowired
    private TSysWindowService tSysWindowService;
    @Autowired
    private WebSocketService webSocketService;

    /**
     * 窗口暂停服务
     * @return 修改结果
     */
    @PutMapping("/hall/queue/stopService")
    public CommonResult stopService(HttpSession session) throws FebsException{
        try {
            //
            CommonResult commonResult = tSysWindowService.stopService(session);
            return commonResult;
        } catch (Exception e) {
            String message = "暂停服务失败！";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 重播统一接口
     */
    @PostMapping("/hall/queue/repaly")
    public Map<String, Object> repaly(@RequestBody String param,HttpServletRequest request) throws FebsException{
        try {
            Map<String, Object> map = tHallQueueService.repaly(request,param);
            return map;
        } catch (Exception e) {
            String message = "重新呼叫失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 业务办理-取号页面
     * @return 跳转结果
     */
    @RequiresPermissions({"takeNumber:view"})
    @GetMapping("/hall/queue/getOrdinal/page")
    public CommonResult getOrdinalpage(HttpServletRequest request) throws FebsException, IOException {
        try {
            //获取业务类型
            List<String> businesstypeList = tSysBusinesstypeService.selectBusinessName();
            return new CommonResult(200, "success", "获取业务类型数据成功", businesstypeList);
        } catch (Exception e) {
            String message = "获取业务类型数据失败";
            log.error(message, e);
            throw new FebsException(message);
        }finally {
            //server处理逻辑
            webSocketService.IndexData(request);
        }
    }
    /**
     * 业务办理-导办取号
     * 代理人：跑腿的人  a
     * 当事人：业务受理人 b
     * @return 取号结果
     */
    @OperLog(operModul = "大厅管理-业务办理-导办取号",operType = "取号",operDesc = "导办取号")
    @PostMapping("/hall/queue/getOrdinal")
    public Map<String, Object> getOrdinal(@RequestBody String param, HttpServletRequest request,HttpServletResponse response,HttpSession session) throws FebsException, IOException {
        try {
            Map<String, Object> map = tHallQueueService.getOrdinal(param,session);
            return map;
        } catch (Exception e) {
            String message = "取号失败";
            log.error(param);
            log.error(message, e);
            throw new FebsException(message);
        } finally {
            //server处理逻辑
            webSocketService.IndexData(request);
        }
    }

    /**
     * 业务办理-排队叫号页面
     */
    @RequiresPermissions({"queue:call"})
    @GetMapping("/hall/queue/call/page")
    public Map<String,Object> callPage(HttpServletRequest request) throws FebsException , IOException{
        try {
            return tHallQueueService.callPage(request);
        } catch (Exception e){
            String message = "系统异常，获取窗口及业务数据失败！";
            log.error(message, e);
            throw new FebsException(message);
        }finally {
            //server处理逻辑
            webSocketService.IndexData(request);
        }
    }

    /**
     * 业务办理-排队叫号
     * 顺序叫号
     * @return 叫号结果
     */
    @OperLog(operModul = "大厅管理-业务办理-排队叫号",operType = "叫号",operDesc = "排队叫号")
    @PostMapping("/hall/queue/call")
    public CommonResult call(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws FebsException, IOException {
        try {
            CommonResult commonResult = tHallQueueService.orderCall(request,session);
            log.info(commonResult.getMsg());
            return commonResult;
        } catch (Exception e) {
            String message = "叫号失败";
            log.error(message, e);
            throw new FebsException(message);
        }finally {
            //server处理逻辑
            webSocketService.IndexData(request);
        }
    }

    /**
     * 业务办理-排队叫号
     * 办结
     * @return 办结结果
     */
    @OperLog(operModul = "大厅管理-业务办理-导办取号",operType = "办结",operDesc = "办结业务")
    @PostMapping("/hall/queue/downNum")
    public Map<String, Object> downNum(HttpServletRequest request, HttpServletResponse response ,HttpSession session,@RequestBody String param) throws FebsException, IOException {
        try {
            Map<String ,Object> map = tHallQueueService.downNum(request,session,param);
            return map;
        } catch (Exception e) {
            String message = "办结失败";
            log.error(param);
            log.error(message, e);
            throw new FebsException(message);
        }finally {
            //server处理逻辑
            webSocketService.IndexData(request);
        }
    }
    /**
     * 业务办理-排队叫号
     * 空号
     * @return 空号结果
     */
    @OperLog(operModul = "大厅管理-业务办理-导办取号",operType = "空号",operDesc = "设置空号")
    @PostMapping("/hall/queue/nullNum")
    public Map<String, Object> nullNum(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws FebsException, IOException {
        try {
            //业务方法
            Map<String, Object> map= tHallQueueService.nullNum(request,session);
            return map;
        } catch (Exception e) {
            String message = "设置空号失败";
            log.error(message, e);
            throw new FebsException(message);
        }finally {
            //server处理逻辑
            webSocketService.IndexData(request);
        }
    }

    /**
     * 业务办理-排队叫号
     * 退号
     * @return 退号结果
     */
    @OperLog(operModul = "大厅管理-业务办理-导办取号",operType = "退号",operDesc = "退办业务")
    @PostMapping("/hall/queue/backNum")
    public Map<String, Object> backNum(HttpServletRequest request, HttpServletResponse response ,HttpSession session,@RequestBody String param) throws FebsException, IOException {
        try {
            //业务方法
            Map<String, Object> map = tHallQueueService.backNum(request,session,param);
            return map;
        } catch (Exception e) {
            String message = "退办失败！";
            log.error(param);
            log.error(message, e);
            throw new FebsException(message);
        }finally {
            //server处理逻辑
            webSocketService.IndexData(request);
        }
    }
    /**
     * 业务办理-排队叫号
     * 特呼
     * @return 叫号结果
     */
    @OperLog(operModul = "大厅管理-业务办理-导办取号",operType = "特呼",operDesc = "特护号码")
    @PostMapping("/hall/queue/vipCall")
    public Map<String,Object> queueVipCall(HttpServletRequest request, HttpServletResponse response ,HttpSession session ,@RequestBody THallQueue tHallQueue) throws FebsException, IOException{
        try {
            Map<String,Object> map = tHallQueueService.queueVipCall(request,session,tHallQueue);
            return map;
        } catch (Exception e) {
            String message = "特呼失败";
            log.error(String.valueOf(tHallQueue));
            log.error(message, e);
            throw new FebsException(message);
        }finally {
            //server处理逻辑
            webSocketService.IndexData(request);
        }
    }

    /**
     * 有叫号信息后访问同步库数据
     */
    @PostMapping("/hall/queue/call/getTbkData")
    public Map<String,Object> getTbkData(@RequestBody String param,HttpServletRequest request)throws FebsException{
        Map<String,Object> map = new HashMap<>();
        try {
            /**
             * 先判断是否同步库连接有问题，若有问题，需返回相应提示信息
             */
            map = tHallQueueService.getTbkData(param,request);
            map.put("status", "success");
            map.put("exception",null);
            return map;
        } catch (Exception e) {
            String message = "同步库连接出错，请联系维护人员排查";
            log.error(param);
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 业务办理-排队信息查询，
     * 按时间顺序排序，可通过筛选框对所有数据进行搜索
     * @return 排队数据
     */
    @OperLog(operModul = "大厅管理-业务办理-排队信息查询",operType = "查看",operDesc = "查看当日排队信息列表")
    @RequiresPermissions({"queue:view"})
    @PostMapping("/hall/queue/listToday")
    public CommonResult listToday(@RequestBody String param) throws FebsException {
        try {
            PageResult result = tHallQueueService.selectAllPage(param);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PageResult",result);
            return new CommonResult(200, "success", "查询排队信息数据成功!", jsonObject);
        } catch (Exception e) {
            String message = "查询排队信息数据失败";
            log.error(param);
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 统计分析-受理人员工作量统计
     * 先查询当日办理人员业务量
     * @return 统计结果
     */
    @OperLog(operModul = "大厅管理-统计分析-受理人员工作量统计",operType = "查看",operDesc = "查看当日受理人员工作量统计")
    @RequiresPermissions({"takeNumber:view"})
    @GetMapping("/hall/queue/StatisticsPage")
    public CommonResult StatisticsNumToday() throws FebsException {
        try {
            List<Statistics> statisticsList=tHallQueueService.acceptancePeopleStatistics(null);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("statisticsList",statisticsList);
            List<TSysBusinesstype> businesstypes = tSysBusinesstypeService.selectBusinessNameAndLevel();
            jsonObject.put("businesstype",businesstypes);
            return new CommonResult(200, "success", "查询受理人员工作量统计成功!", jsonObject);
        } catch (Exception e) {
            String message = "查询受理人员工作量统计失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 统计分析-受理人员工作量统计-查询
     * 根据时间查询办理人员业务量统计
     * @return 统计结果
     */
    @OperLog(operModul = "大厅管理-统计分析-受理人员工作量统计",operType = "查询",operDesc = "条件查询当日受理人员工作量统计")
    @PostMapping("/hall/queue/StatisticsNum")
    public CommonResult StatisticsNum(@RequestBody String param) throws FebsException {
        try {
            List<Statistics> statisticsList = tHallQueueService.acceptancePeopleStatistics(param);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("statisticsList",statisticsList);
            List<TSysBusinesstype> businesstypes = tSysBusinesstypeService.selectBusinessNameAndLevel();
            jsonObject.put("businesstype",businesstypes);
            return new CommonResult(200, "success", "查询受理人员工作量统计成功!", jsonObject);
        } catch (Exception e) {
            String message = "查询受理人员工作量统计失败";
            log.error(param);
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 统计分析-值日警官工作量统计
     * 根据时间查询办理人员业务量统计
     * @return 统计结果
     */
    @OperLog(operModul = "大厅管理-统计分析-值日警官工作量统计",operType = "查询",operDesc = "条件查询当日值日警官工作量统计")
    @RequiresPermissions({"duty:statistics"})
    @PostMapping("/hall/queue/dutyStatistics")
    public CommonResult dutyStatistics(@RequestBody String param) throws FebsException {
        try {
            List<THallQueueCount> result = tHallQueueService.dutyStatistics(param);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PageResult",result);
            return new CommonResult(200, "success", "查询值日警官工作量统计数据成功!", jsonObject);
        } catch (Exception e) {
            String message = "查询值日警官工作量统计数据失败";
            log.error(param);
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 统计分析-预警统计
     * @return 查询数据
     */
    @OperLog(operModul = "大厅管理-统计分析-预警统计",operType = "查看",operDesc = "查看当日产生的预警统计列表")
    @PostMapping("/hall/queue/WarningCount")
    public CommonResult WarningCount(@RequestBody String param) throws FebsException {
        try {
            PageResult result = tHallQueueService.WarningCount(param);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PageResult",result);
            return new CommonResult(200, "success", "查询数据成功!", jsonObject);
        } catch (Exception e) {
            String message = "查询数据失败";
            log.error(param);
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 3 删除数据
     * @return 删除结果
     */
    @OperLog(operModul = "大厅管理-业务办理-排队信息查询",operType = "删除",operDesc = "删除排队信息")
    @DeleteMapping("/hall/queue/del")
    public CommonResult tHallQueueDel(@RequestBody String param) throws FebsException {
        JSONObject jsonObject = JSON.parseObject(param);
        String idStr = String.valueOf(jsonObject.get("pkQueueId"));
        try {
            tHallQueueService.deleteById(idStr);
            return new CommonResult(200, "success", "数据删除成功!", null);
        } catch (Exception e) {
            String message = "数据删除失败";
            log.error(param);
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 4 通过主键查询单条数据
     * @return 单条数据
     */
    @OperLog(operModul = "大厅管理-业务办理-排队信息查询",operType = "查看",operDesc = "查看排队信息详情")
    @GetMapping("/hall/queue/getOne")
    public CommonResult tHallQueuegetOne(@RequestBody String param) throws FebsException {
        JSONObject jsonObject = JSON.parseObject(param);
        String idStr = String.valueOf(jsonObject.get("pkQueueId"));
        try {
            //
            THallQueue tHallQueue = tHallQueueService.selectById(idStr);
            return new CommonResult(200, "success", "数据获取成功!", tHallQueue);
        } catch (Exception e) {
            String message = "数据获取失败";
            log.error(param);
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 4 修改数据
     * @param tHallQueue 实体对象
     * @return 修改结果
     */
    @OperLog(operModul = "大厅管理-业务办理-排队信息查询",operType = "修改",operDesc = "修改排队信息")
    @PutMapping("/hall/queue/update")
    public CommonResult tHallQueueUpdate(@RequestBody THallQueue tHallQueue) throws FebsException {
        try {
            tHallQueueService.updateById(tHallQueue);
            return new CommonResult(200, "success", "修改成功!", null);
        } catch (Exception e) {
            String message = "修改失败";
            log.error(String.valueOf(tHallQueue));
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 叫号成功（顺序叫号和特呼）后访问led屏接口
     * @return 叫号结果
     */
    @PostMapping("/hall/queue/call/led")
    public CommonResult led(@RequestBody String param )throws FebsException{
        try {
            CommonResult commonResult = tHallQueueService.led(param);
            return commonResult;
        } catch (Exception e) {
            String message = "叫号成功后访问大厅led和窗口led接口失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 叫号成功（顺序叫号和特呼）后访问led窗口屏接口
     * @return 叫号结果
     */
    @PostMapping("/hall/queue/call/smallLed")
    public CommonResult smallLed(@RequestBody String param )throws FebsException{
        try {
            CommonResult commonResult = tHallQueueService.smallLed(param);
            return commonResult;
        } catch (Exception e) {
            String message = "叫号成功后访问大厅led和窗口led接口失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 叫号成功（顺序叫号和特呼）后访问led大屏接口
     * @return 叫号结果
     */
    @PostMapping("/hall/queue/call/bigLed")
    public CommonResult bigLed(@RequestBody String param )throws FebsException{
        try {
            CommonResult commonResult = tHallQueueService.bigLed(param);
            return commonResult;
        } catch (Exception e) {
            String message = "叫号成功后访问大厅led和窗口led接口失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 业务办理完结后访问，办结、空号、退号
     */
    @PostMapping("/hall/queue/complete")
    public CommonResult complete(@RequestBody String param) throws FebsException {
        try {
            return tHallQueueService.complete(param);
        } catch (Exception e) {
            String message = "办结成功后访问大厅led和窗口led接口失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    @PostMapping("/hall/queue/complete/smallLed")
    public CommonResult completeSmallLed(@RequestBody String param) throws FebsException {
        try {
            return tHallQueueService.completeSmallLed(param);
        } catch (Exception e) {
            String message = "办结成功后访问窗口led接口失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    @PostMapping("/hall/queue/complete/bigLed")
    public CommonResult completeBigLed(@RequestBody String param) throws FebsException {
        try {
            return tHallQueueService.completeBigLed(param);
        } catch (Exception e) {
            String message = "办结成功后访问大厅led接口失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}