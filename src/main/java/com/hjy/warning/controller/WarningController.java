package com.hjy.warning.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.annotation.OperLog;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.exception.FebsException;
import com.hjy.common.utils.JsonUtil;
import com.hjy.common.utils.page.PageResult;
import com.hjy.list.entity.TListInfo;
import com.hjy.list.service.TListInfoService;
import com.hjy.system.dao.TSysParamMapper;
import com.hjy.system.entity.TSysBusinesstype;
import com.hjy.system.entity.TSysParam;
import com.hjy.system.service.TSysBusinesstypeService;
import com.hjy.system.service.TSysParamService;
import com.hjy.warning.entity.Warning;
import com.hjy.warning.service.TWarnLnfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class WarningController {

    /**
     * 服务对象
     */
    @Autowired
    private TWarnLnfoService warningService;
    @Autowired
    private TSysBusinesstypeService tSysBusinesstypeService;

    /**
     * 综合查询
     * 按时间顺序排序，可通过筛选框对所有数据进行搜索
     * @return 排队数据
     */
    @OperLog(operModul = "预警管理-预警监督-预警查看",operType = "查看",operDesc = "查看预警列表")
    @RequiresPermissions({"warning:view"})
    @PostMapping("/warning/warning/listToday")
    public CommonResult listToday(@RequestBody String param) throws FebsException {
        try {
            PageResult result = warningService.selectAllPage(param);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PageResult",result);
            List<String> businessTypes = tSysBusinesstypeService.selectBusinessName();
            jsonObject.put("businessTypes",businessTypes);
            return new CommonResult(200, "success", "查询预警信息数据成功!", jsonObject);
        } catch (Exception e) {
            String message = "查询预警信息数据失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    //核查
    @OperLog(operModul = "预警管理-预警监督-预警查看",operType = "核查",operDesc = "核查机动车异常")
//    @RequiresPermissions({"warning:check"})
    @PostMapping("/warning/warning/check")
    public Map<String,Object> check(@RequestBody String param, HttpSession session) throws FebsException {
        try {
            Map<String,Object> map = warningService.warningCheck(param,session);
            return map;
        } catch (Exception e) {
            String message = "核查失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    //详情
    @OperLog(operModul = "预警管理-预警监督-预警查看",operType = "查看",operDesc = "查看预警详情")
//    @RequiresPermissions({"warning:details"})
    @PostMapping("/warning/warning/details")
    public CommonResult details(@RequestBody String param) throws FebsException {
        try {
            return warningService.details(param);
        } catch (Exception e) {
            String message = "核查失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 预警中的通过证件号查看
     *
     */
    @OperLog(operModul = "预警管理-预警监督-预警查询",operType = "查看",operDesc = "预警中查看黑红名单信息")
    @PostMapping("/warning/listInfo/listInfoGetOne")
    public CommonResult getListInfo(@RequestBody String param) throws FebsException{
        try {
            CommonResult commonResult = warningService.getListInfo(param);
            return commonResult;
        } catch (Exception e) {
            String message = "数据获取失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     * 有驾驶证异常或机动车异常时访问同步库数据
     */
    @PostMapping("/warning/warning/getTbkData")
    public Map<String,Object> getTbkData(@RequestBody String param, HttpServletRequest request)throws SQLException {
        Map<String,Object> map = new HashMap<>();
        try {
            /**
             * 先判断是否同步库连接有问题，若有问题，需返回相应提示信息
             */
            map = warningService.getTbkData(param,request);
            map.put("status", "success");
            map.put("exception",null);
            return map;
        } catch (SQLRecoverableException e) {
            map.put("code",445);
            map.put("status", "error");
            TSysParam tSysParam = new TSysParam();
            tSysParam.setPkParamId("SFHQTBKSJ");
            tSysParam.setParamValue("否");
            tSysParam.setOperatorPeople("warning445");
            tSysParam.setOperatorTime(new Date());
//            tSysParamMapper.updateById(tSysParam);
            map.put("exception","445同步库连接出错，请联系维护人员排查，系统已自动更改参数配置：是否获取同步库数据改为否");
            return map;
        }catch (MyBatisSystemException e) {
            map.put("code",446);
            map.put("status", "error");
            TSysParam tSysParam = new TSysParam();
            tSysParam.setPkParamId("SFHQTBKSJ");
            tSysParam.setParamValue("否");
            tSysParam.setOperatorPeople("warning446");
            tSysParam.setOperatorTime(new Date());
//            tSysParamMapper.updateById(tSysParam);
            map.put("exception","446同步库连接出错，请联系维护人员排查，系统已自动更改参数配置：是否获取同步库数据改为否");
            return map;
        } catch (CannotGetJdbcConnectionException e) {
            map.put("code",447);
            map.put("status", "error");
            TSysParam tSysParam = new TSysParam();
            tSysParam.setPkParamId("SFHQTBKSJ");
            tSysParam.setParamValue("否");
            tSysParam.setOperatorPeople("warning447");
            tSysParam.setOperatorTime(new Date());
//            tSysParamMapper.updateById(tSysParam);
            map.put("exception","447同步库连接出错，请联系维护人员排查，系统已自动更改参数配置：是否获取同步库数据改为否");
            return map;
        } catch (PersistenceException e) {
            map.put("code",448);
            map.put("status", "error");
            TSysParam tSysParam = new TSysParam();
            tSysParam.setPkParamId("SFHQTBKSJ");
            tSysParam.setParamValue("否");
            tSysParam.setOperatorPeople("warning448");
            tSysParam.setOperatorTime(new Date());
//            tSysParamMapper.updateById(tSysParam);
            map.put("exception","448同步库连接出错，请联系维护人员排查，系统已自动更改参数配置：是否获取同步库数据改为否");
            return map;
        }
        catch (Exception e) {
            map.put("code",449);
            map.put("status", "error");
            TSysParam tSysParam = new TSysParam();
            tSysParam.setPkParamId("SFHQTBKSJ");
            tSysParam.setParamValue("否");
            tSysParam.setOperatorPeople("warning449");
            tSysParam.setOperatorTime(new Date());
//            tSysParamMapper.updateById(tSysParam);
            map.put("exception","449同步库连接出错，请联系维护人员排查，系统已自动更改参数配置：是否获取同步库数据改为否");
            return map;
        }
    }
    //查看告知单
    @OperLog(operModul = "预警管理-预警监督-预警查看",operType = "查看",operDesc = "通过预警流水号查看告知单")
    @PostMapping("/warning/warning/gaozhidan")
    public CommonResult gaozhidan(@RequestBody String param, HttpSession session) throws FebsException {
        try {
            JSONObject jsonObject = warningService.selectGaozhidan(param);
            return new CommonResult(200, "success", "查询预警信息数据成功!", jsonObject);
        } catch (Exception e) {
            String message = "核查失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

}
