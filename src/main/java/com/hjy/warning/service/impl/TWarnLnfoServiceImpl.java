package com.hjy.warning.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.task.ObjectAsyncTask;
import com.hjy.common.utils.DateUtil;
import com.hjy.common.utils.JsonUtil;
import com.hjy.common.utils.StrBufferUtil;
import com.hjy.common.utils.page.PageResult;
import com.hjy.common.utils.page.PageUtil;
import com.hjy.hall.entity.THallJiashizheng;
import com.hjy.hall.entity.THallJidongche;
import com.hjy.hall.service.THallJiashizhengService;
import com.hjy.hall.service.THallJidongcheService;
import com.hjy.list.entity.TListInfo;
import com.hjy.list.service.TListInfoService;
import com.hjy.system.dao.TSysParamMapper;
import com.hjy.system.entity.ActiveUser;
import com.hjy.tbk.entity.TbkDrivinglicense;
import com.hjy.tbk.entity.TbkDrvFlow;
import com.hjy.tbk.entity.TbkVehFlow;
import com.hjy.tbk.entity.TbkVehicle;
import com.hjy.tbk.service.TbkDrivinglicenseService;
import com.hjy.tbk.service.TbkDrvFlowService;
import com.hjy.tbk.service.TbkVehFlowService;
import com.hjy.tbk.service.TbkVehicleService;
import com.hjy.tbk.statusCode.DrivingStatus;
import com.hjy.tbk.statusCode.HPStatus;
import com.hjy.tbk.statusCode.VehicleStatus;
import com.hjy.tbk.statusCode.YWLXStatus;
import com.hjy.warning.dao.TWarnLnfoMapper;
import com.hjy.warning.entity.Warning;
import com.hjy.warning.service.TWarnLnfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * (TWarnLnfo)表服务实现类
 *
 * @author liuchun
 * @since 2020-09-22 18:22:32
 */
@Service("tWarnLnfoService")
public class TWarnLnfoServiceImpl implements TWarnLnfoService {
    @Autowired
    private TWarnLnfoMapper tWarnLnfoMapper;
    @Autowired
    private TbkVehFlowService tbkVehFlowService;
    @Autowired
    private TbkDrvFlowService tbkDrvFlowService;
    @Autowired
    private TbkVehicleService tbkVehicleService;
    @Autowired
    private TbkDrivinglicenseService tbkDrivinglicenseService;
    @Autowired
    private THallJiashizhengService tHallJiashizhengService;
    @Autowired
    private THallJidongcheService tHallJidongcheService;
    @Autowired
    private TSysParamMapper tSysParamMapper;
    @Autowired
    private TListInfoService listInfoService;

    @Override
    public PageResult selectAllPage(String param) throws ParseException {
        JSONObject json = JSON.parseObject(param);
        //缺单位
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String businessType = JsonUtil.getStringParam(json,"businessType");
        String warningCategory = JsonUtil.getStringParam(json,"warningCategory");
        String warningType = JsonUtil.getStringParam(json,"warningType");
        String checkStatus = JsonUtil.getStringParam(json,"checkStatus");
        String checkResult = JsonUtil.getStringParam(json,"checkResult");
        String bName = JsonUtil.getStringParam(json,"bName");
        String bIdcard = JsonUtil.getStringParam(json,"bIdcard");
        String ordinal = JsonUtil.getStringParam(json,"ordinal");
        String windowName = JsonUtil.getStringParam(json,"windowName");
        String agent = JsonUtil.getStringParam(json,"agent");
        String queryStartStr = JsonUtil.getStringParam(json,"queryStart");
        Date queryStart = null;
        Date queryEnd = null;
        if(queryStartStr != null && queryStartStr.length()>8){
            queryStart = ft.parse(queryStartStr);
        }
        String queryEndStr = JsonUtil.getStringParam(json,"queryEnd");
        if(queryEndStr != null && queryEndStr.length()>8){
            queryEnd = ft.parse(queryEndStr);
        }
        //实体数据
        Warning warning = new Warning();
        warning.setBusinessType(businessType);
        warning.setWarningCategory(warningCategory);
        warning.setWarningType(warningType);
        warning.setCheckStatus(checkStatus);
        warning.setCheckResult(checkResult);
        warning.setBName(bName);
        warning.setBIdcard(bIdcard);
        warning.setOrdinal(ordinal);
        warning.setWindowName(windowName);
        warning.setAgent(agent);
        warning.setQueryStart(queryStart);
        warning.setQueryEnd(queryEnd);
        //分页记录条数
        int total = tWarnLnfoMapper.selectSize(warning);
        PageResult result = PageUtil.getPageResult(param,total);
        warning.setStartRow(result.getStartRow());
        warning.setEndRow(result.getEndRow());
        List<Warning> warnings = tWarnLnfoMapper.selectAllPage(warning);
        //先排序
        if(warnings !=null){
            Collections.sort(warnings, new Comparator<Warning>() {
                @Override
                public int compare(Warning o1, Warning o2) {
                    //升序
                    return o1.getWarningDate().compareTo(o2.getWarningDate());
                }
            });
            List<Warning> warningList = warnings;
            result.setContent(warningList);
        }
        return result;
    }

    @Transactional()
    @Override
    public Map<String, Object> warningCheck(String param, HttpSession session) {
        JSONObject json = JSON.parseObject(param);
        Map<String, Object> map = new HashMap<>();
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        String checkResult = JsonUtil.getStringParam(json,"checkResult");
        String checkDesc = JsonUtil.getStringParam(json,"checkDesc");
        //预警信息主键
        String pkWarningId = JsonUtil.getStringParam(json,"pkWarningId");
        Warning warning = new Warning();
        warning.setPkWarningId(pkWarningId);
        warning.setCheckStatus("已核查");
        warning.setCheckDate(new Date());
        warning.setCheckPeople(activeUser.getFullName());
        warning.setCheckResult(checkResult);
        warning.setCheckDesc(checkDesc);
        int i = ObjectAsyncTask.warningUpdate(warning);
        if(i>0){
            map.put("code",200);
            map.put("status", "success");
            map.put("msg", "核查成功1");
            return map;
        }else {
            map.put("code",444);
            map.put("status", "error");
            map.put("msg", "核查失败!");
            return map;
        }
    }
    //预警详情
    @Override
    public CommonResult details(String param) {
        JSONObject json = JSON.parseObject(param);
        //预警信息主键
        String pkWarningId = JsonUtil.getStringParam(json,"pkWarningId");
        Warning warning = tWarnLnfoMapper.selectById(pkWarningId);
        //转化一下等待时长
        long waitTime = (warning.getStartTime().getTime()-warning.getGetTime().getTime())/1000;
        warning.setWaitTime(DateUtil.translateLong(waitTime));
        //结束时间
        if(warning.getEndTime() != null){
            //计算处理时间
            long handleTime = (warning.getEndTime().getTime()-warning.getStartTime().getTime())/1000;
            warning.setHandleTime(DateUtil.translateLong(handleTime));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("details",warning);
        //退办单信息
        if(warning.getWarningType().equals("退办预警")){
            //202001021001
            String lsh = warning.getWarningSerial();
            if(!StringUtils.isEmpty(lsh)){
                //第9位为机动车和驾驶证告知书的标识
                String sign = lsh.substring(8,9);
                if("1".equals(sign)){
                    //机动车
                    THallJidongche entity = tHallJidongcheService.selectByAssociationNumber(lsh);
                    jsonObject.put("carInfo",entity);
                }else {
                    //驾驶证
                    THallJiashizheng entity = tHallJiashizhengService.selectByAssociationNumber(lsh);
                    jsonObject.put("licenseInfo",entity);
                }
            }
        }
        return new CommonResult(200, "success", "查询预警信息数据成功!", jsonObject);
    }
    //获取同步库数据
    @Override
    public Map<String, Object> getTbkData(String param, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        JSONObject resultJson = new JSONObject();
        JSONObject json = JSON.parseObject(param);
        String warningSerial = JsonUtil.getStringParam(json, "warningSerial");
        String pkWarningId = JsonUtil.getStringParam(json, "pkWarningId");
        /**
         * 查询是否需要获取同步库数据
         */
        //查询参数配置中加入黑名单代办次数限值，如果没有就默认为5次
        String value = tSysParamMapper.selectParamById("SFHQTBKSJ");
        String SFHQTBKSJ = "否";
        if (value != null) {
            SFHQTBKSJ = value;
        }
        if (SFHQTBKSJ.equals("否")) {
            map.put("code", 202);
            map.put("data", resultJson);
            map.put("msg", "系统参数配置中已设置为不获取同步库数据！");
            return map;
        } else {
            //机动车流水信息
            List<TbkVehFlow> resultA = new ArrayList<>();
            //驾驶证流水信息
            List<TbkDrvFlow> resultB = new ArrayList<>();
            //第一步，先去查询机动车的流水信息
            resultA = tbkVehFlowService.selectByLsh(warningSerial);
            /**
             * 判断是否是注册登记
             */
            String flag = "否";
            flag = this.whether_G_YW(resultA);
            if(flag.equals("是")){
                //删除预警
                Warning delwarning = new Warning();
                delwarning.setPkWarningId(pkWarningId);
                int t = tWarnLnfoMapper.deleteById(delwarning);
                map.put("code", 204);
                if(t > 0){
                    map.put("msg","该机动车业务为注销登记，预警已被删除！");
                }else {
                    map.put("msg","该机动车业务为注销登记，预警删除失败！");
                }
                return map;
            }
            //第二步，判断该流水号办理的是哪种业务
            if(resultA.size() > 0){
                //说明办理的是机动车业务，那么驾驶证信息就不要
                //将机动车信息做修饰
                resultA = this.getBRCLLSQK(resultA);
                resultJson.put("car",resultA);
                map.put("code", 200);
                map.put("msg","同步库机动车业务流水数据获取成功！");
            }else {
                //说明办理的是驾驶证业务，或者同步库还未更新
                resultB = tbkDrvFlowService.selectByLsh(warningSerial);
                /**
                 * 判断是否是注册登记
                 */
                flag = this.whether_G_YW2(resultB);
                if(flag.equals("是")){
                    //删除预警
                    Warning delwarning = new Warning();
                    delwarning.setPkWarningId(pkWarningId);
                    int t = tWarnLnfoMapper.deleteById(delwarning);
                    map.put("code", 205);
                    if(t > 0){
                        map.put("msg","该驾驶证业务为注销登记，预警已被删除！");
                    }else {
                        map.put("msg","该驾驶证业务为注销登记，预警删除失败！");
                    }
                    return map;
                }
                if(resultB.size() > 0){
                    //说明办理的是驾驶证业务，那么机动车信息不需要返回
//                    resultB = this.getBRJSZLSQK(resultB);
                    resultJson.put("car",null);
                    map.put("code", 201);
                    map.put("msg","同步库驾驶证业务流水数据获取成功！");
                }else {
                    //说明同步库还未更新,或者流水号输入有误，同步库中不存在该流水号
                    resultJson.put("license",null);
                    resultJson.put("car",null);
                    map.put("code", 203);
                    map.put("msg","请核对同步库数据是否更新或流水号输入是否有误！");
                }
            }
            map.put("data",resultJson);
        }
        return map;
    }

    //判断是否为注销登记
    private String whether_G_YW(List<TbkVehFlow> brclList) {
        if(brclList == null){
            return "否";
        }
        Iterator<TbkVehFlow> iterator = brclList.iterator();
        while(iterator.hasNext()){
            TbkVehFlow obj = iterator.next();
            if(obj.getYwlx().contains("G")){
                return "是";
            }
        }
        return "否";
    }
    private String whether_G_YW2(List<TbkDrvFlow> brjszList) {
        if(brjszList == null){
            return "否";
        }
        Iterator<TbkDrvFlow> iterator = brjszList.iterator();
        while(iterator.hasNext()){
            TbkDrvFlow obj = iterator.next();
            if(obj.getYwlx().contains("G")){
                return "是";
            }
        }
        return "否";
    }

    @Override
    public CommonResult getListInfo(String param) {
        JSONObject jsonObject = JSON.parseObject(param);
        JSONObject resultJson = new JSONObject();
        String bIdCard = JsonUtil.getStringParam(jsonObject,"bIdCard");
        String aIdCard = JsonUtil.getStringParam(jsonObject,"aIdCard");
        //查询本人机动车信息
        if(bIdCard != null) {
            TListInfo tListInfoB = listInfoService.selectByIdCard(bIdCard);
            resultJson.put("BList",tListInfoB);
            //本人所有车辆信息
            List<TbkVehicle> brclList = tbkVehicleService.selectByIdCard(bIdCard);
            brclList = this.getBRCLQK(brclList);
            resultJson.put("BCar", brclList);
            //本人驾驶证信息
            List<TbkDrivinglicense> brjszList = tbkDrivinglicenseService.selectByIdCard(bIdCard);
            brjszList = this.getBRJSZQK(brjszList);
            resultJson.put("BLicense", brjszList);
        }
        //查询代理人机动车信息
        if(aIdCard != null) {
            TListInfo tListInfoA = listInfoService.selectByIdCard(aIdCard);
            resultJson.put("AList",tListInfoA);
            //本人所有车辆信息
            List<TbkVehicle> brclList = tbkVehicleService.selectByIdCard(aIdCard);
            brclList = this.getBRCLQK(brclList);
            resultJson.put("ACar", brclList);
            //本人驾驶证信息
            List<TbkDrivinglicense> brjszList = tbkDrivinglicenseService.selectByIdCard(aIdCard);
            brjszList = this.getBRJSZQK(brjszList);
            resultJson.put("ALicense", brjszList);
        }
        return new CommonResult(200, "success", "获取数据成功!", resultJson);

    }

    @Override
    public void systemMaintain() {
        tWarnLnfoMapper.systemMaintain();
    }

    //将本人驾驶证信息做筛选修饰
    private List<TbkDrivinglicense> getBRJSZQK(List<TbkDrivinglicense> brjszList) {
        if(brjszList == null){
            return null;
        }
        Iterator<TbkDrivinglicense> iterator = brjszList.iterator();
        while(iterator.hasNext()){
            TbkDrivinglicense obj = iterator.next();
            String status = obj.getZt();
//            //ACEFG的不要
//            if(status.contains("A") ||status.contains("C")||status.contains("E")||status.contains("F")||status.contains("G")){
//                iterator.remove();
//                continue;
//            }
            StringBuffer tempBuffer2 = new StringBuffer();
            //驾驶证状态
            String [] ztstrings = status.split("");
            int j =1;
            //翻译
            for(String zt:ztstrings){
                String msg = DrivingStatus.getDesc(zt);
                tempBuffer2.append(j+"."+msg+".");
                j++;
            }
            obj.setZt(tempBuffer2.toString());
        }
        return brjszList;
    }

    //对机动车信息加以修饰
    private List<TbkVehicle> getBRCLQK(List<TbkVehicle> brclList) {
        if(brclList == null){
            return null;
        }
        Iterator<TbkVehicle> iterator = brclList.iterator();
        while(iterator.hasNext()){
            TbkVehicle obj = iterator.next();
            if(obj.getZt().contains("A") || obj.getZt().contains("B")){
                iterator.remove();
                continue;
            }
            StringBuffer ztBuffer = new StringBuffer();
//            StringBuffer syxzBuffer = new StringBuffer();
            StringBuffer tempBuffer = new StringBuffer();
            //机动车状态
            String [] ztstrings = obj.getZt().split("");
            int j =1;
            //翻译
            for(String zt:ztstrings){
                String msg = VehicleStatus.getDesc(zt);
                tempBuffer.append(j+"."+msg);
                j++;
            }
            if(!StringUtils.isEmpty(ztBuffer.toString())){
                ztBuffer.append("/"+tempBuffer.toString());
            }else {
                ztBuffer.append(tempBuffer.toString());
            }
            obj.setExceptionReason(ztBuffer.toString());

//            StringBuffer tempBuffer4 = new StringBuffer();
//            //使用性质
//            String [] syxzstrings = obj.getSyxz().split("");
//            int m =1;
//            //翻译
//            for(String syxz:syxzstrings){
//                String msg = SYXZStatus.getDesc(syxz);
//                tempBuffer4.append(m+"."+msg );
//                m++;
//            }
//            if(!StringUtils.isEmpty(syxzBuffer.toString())){
//                syxzBuffer.append("/"+tempBuffer4.toString());
//            }else {
//                syxzBuffer.append(tempBuffer4.toString());
//            }
//            //---------
//            obj.setSyxz(syxzBuffer.toString());
        }
        return brclList;
    }

    @Override
    public JSONObject selectGaozhidan(String param) {
        JSONObject jsonObject = JSON.parseObject(param);
        String associationNumber = JsonUtil.getStringParam(jsonObject,"associationNumber");
        THallJidongche obj1 = tHallJidongcheService.selectByAssociationNumber(associationNumber);
        THallJiashizheng obj2 = tHallJiashizhengService.selectByAssociationNumber(associationNumber);
        jsonObject.put("obj1",obj1);
        jsonObject.put("obj2",obj2);
        return jsonObject;
    }

    @Override
    public Warning warning12_2(Warning warning, List<TbkVehicle> brclList, List<TbkDrivinglicense> brjszList) {
        //机动车中状态为MOQ的需要做预警和核查，但一旦遇到E或C就不做预警
        if(brclList != null){
            Iterator<TbkVehicle> iterator = brclList.iterator();
            while(iterator.hasNext()) {
                TbkVehicle obj = iterator.next();
                if ( !((obj.getZt().contains("M") && (!obj.getZt().contains("C") || !obj.getZt().contains("E"))) ||
                        (obj.getZt().contains("O") && (!obj.getZt().contains("C") || !obj.getZt().contains("E")) )||
                        (obj.getZt().contains("Q") && (!obj.getZt().contains("C") || !obj.getZt().contains("E")))) ) {
                    iterator.remove();
                    continue;
                }
            }
        }
        StringBuffer warningTypeBuffer = new StringBuffer();
        warningTypeBuffer.append(warning.getWarningType());
        StringBuffer reasonBuffer = new StringBuffer();
        reasonBuffer.append(warning.getWarningReason());
        StringBuffer chezhuXmBuffer = new StringBuffer();
        StringBuffer chezhuCardBuffer = new StringBuffer();
        StringBuffer hphmBuffer = new StringBuffer();
        StringBuffer hpzlBuffer = new StringBuffer();
        StringBuffer alshBuffer = new StringBuffer();
        StringBuffer blshBuffer = new StringBuffer();
        StringBuffer syxzBuffer = new StringBuffer();
        StringBuffer clsbdhBuffer = new StringBuffer();
        StringBuffer resultMsgBuffer = new StringBuffer();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDateStr = simpleDateFormat.format(new Date());
        resultMsgBuffer.append(nowDateStr+"叫号预警："+warning.getOrdinal());
        resultMsgBuffer.append(" "+warning.getWarningType());
        /**
         * 预警-1.本人机动车异常
         */
        if(brclList != null && brclList.size() != 0){
            boolean flag =true;
            for(TbkVehicle obj:brclList){
                String status = obj.getZt();
                if(status != null){
                    if(!status.contains("A")){
                        while (flag){
                            //预警分类
                            warningTypeBuffer.append("/本人机动车异常");
                            //预警返回信息
                            resultMsgBuffer.append("-本人机动车异常");
                            //车主姓名
                            chezhuXmBuffer.append(obj.getSyr());
                            //车主证件号
                            chezhuCardBuffer.append(obj.getSfzmhm());
                            //核查信息
                            warning.setCheckStatus("待核查");
                            flag = false;
                        }
                        //预警原因
                        //将所有的异常信息放入TbkVehicle对象里的exceptionReason
                        StringBuffer statuStringBuffer = new StringBuffer();
                        String [] strings = status.split("");
                        int j =1;
                        //将所有的异常信息放入reason
                        for(String s:strings){
                            String msg = VehicleStatus.getDesc(s);
                            statuStringBuffer.append(j+"."+msg+".");
                            j++;
                        }
                        if(!StringUtils.isEmpty(reasonBuffer.toString())){
                            reasonBuffer.append("/"+statuStringBuffer);
                        }else {
                            reasonBuffer.append(statuStringBuffer);
                        }
                        obj.setExceptionReason(statuStringBuffer.toString());
                        //号牌号码
                        if(!StringUtils.isEmpty(hphmBuffer.toString())){
                            hphmBuffer.append("/"+obj.getHphm());
                        }else {
                            hphmBuffer.append(obj.getHphm());
                        }
                        //号牌种类
                        if(!StringUtils.isEmpty(hpzlBuffer.toString())){
                            hpzlBuffer.append("/"+obj.getHpzl());
                        }else {
                            hpzlBuffer.append(obj.getHpzl());
                        }
                        //流水号
                        if(!StringUtils.isEmpty(blshBuffer.toString())){
                            blshBuffer.append("/"+obj.getLsh());
                        }else {
                            blshBuffer.append(obj.getLsh());
                        }
                        //使用性质
                        if(!StringUtils.isEmpty(syxzBuffer.toString())){
                            syxzBuffer.append("/"+obj.getSyxz());
                        }else {
                            syxzBuffer.append(obj.getSyxz());
                        }
//                        //车辆类型
//                        if(!StringUtils.isEmpty(cllxBuffer.toString())){
//                            cllxBuffer.append("/"+obj.getCllx());
//                        }else {
//                            cllxBuffer.append(obj.getCllx());
//                        }
//                        //车辆型号
//                        if(!StringUtils.isEmpty(clxhBuffer.toString())){
//                            clxhBuffer.append("/"+obj.getClxh());
//                        }else {
//                            clxhBuffer.append(obj.getClxh());
//                        }
                        //车辆识别代号
                        if(!StringUtils.isEmpty(clsbdhBuffer.toString())){
                            clsbdhBuffer.append("/"+obj.getClsbdh());
                        }else {
                            clsbdhBuffer.append(obj.getClsbdh());
                        }
                        //机动车异常信息
//                        result.add(obj);
                    }
                }
            }
//            warning.setVehicleInfo(result);
        }

        /**
         * 预警-2.本人驾驶证异常
         */
//        if(brjszList != null){
//            boolean flag =true;
//            for(TbkDrivinglicense obj:brjszList){
//                String status = obj.getZt();
//                if(status != null){
//                    if(!(status.contains("A") || status.contains("E") ||status.contains("F") ||status.contains("G"))){
//                        while (flag){
//                            //预警分类
//                            if(!StringUtils.isEmpty(warningTypeBuffer.toString())){
//                                warningTypeBuffer.append("/本人驾驶证异常");
//                            }else {
//                                warningTypeBuffer.append("本人驾驶证异常");
//                            }
//                            //预警返回信息
//                            resultMsgBuffer.append("-本人驾驶证异常");
//                            flag = false;
//                        }
//                        //将所有的异常信息放入TbkDrivinglicense对象里的exceptionReason
//                        StringBuffer statuStringBuffer = new StringBuffer();
//                        String [] strings = status.split("");
//                        int j =1;
//                        //将所有的异常信息放入reason
//                        for(String s:strings){
//                            String msg = DrivingStatus.getDesc(s);
//                            statuStringBuffer.append(j+"."+msg+".");
//                            j++;
//                        }
//                        if(!StringUtils.isEmpty(reasonBuffer.toString())){
//                            reasonBuffer.append("/"+statuStringBuffer);
//                        }else {
//                            reasonBuffer.append(statuStringBuffer);
//                        }
//                        obj.setExceptionReason(statuStringBuffer.toString());
//                        //流水号
//                        if(!StringUtils.isEmpty(alshBuffer.toString())){
//                            alshBuffer.append("/"+obj.getLsh());
//                        }else {
//                            alshBuffer.append(obj.getLsh());
//                        }
//                        //驾驶证异常信息
////                        result2.add(obj);
//                    }
//                }
//            }
////            warning.setDrivingLicenseInfo(result2);
//        }
        //更新预警
        warning.setResultMsg(resultMsgBuffer.toString());
        warning.setWarningType(warningTypeBuffer.toString());
        warning.setWarningReason(reasonBuffer.toString());
        //机动车关联流水号-防止过长
        warning.setSerialNumberB(StrBufferUtil.handTooLengthStrBuffer(blshBuffer.toString()));
        warning.setSerialNumberA(alshBuffer.toString());
        warning.setClsbdh(clsbdhBuffer.toString());
        warning.setHpzl(StrBufferUtil.handTooLengthStrBuffer(hpzlBuffer.toString()));
        warning.setHphm(StrBufferUtil.handTooLengthStrBuffer(hphmBuffer.toString()));
        warning.setChezhuXm(chezhuXmBuffer.toString());
        warning.setChezhuCard(chezhuCardBuffer.toString());
        warning.setSyxz(syxzBuffer.toString());
        return warning;
    }
    //对机动车信息加以修饰
    private List<TbkVehFlow> getBRCLLSQK(List<TbkVehFlow> brclList) {
        if(brclList == null){
            return null;
        }
        Iterator<TbkVehFlow> iterator = brclList.iterator();
        while(iterator.hasNext()){
            TbkVehFlow obj = iterator.next();
            //业务类型
            obj.setYwlx(YWLXStatus.getDesc(obj.getYwlx()));
            //业务原因
            obj.setYwyy(YWLXStatus.getDesc(obj.getYwyy()));
            //号牌种类
            String hpzl = obj.getHpzl();
            obj.setHpzl(hpzl+HPStatus.getDesc(hpzl));
            //车辆类型,暂不翻译
//            obj.setCllx(CLLXStatus.getDesc(obj.getCllx()));
        }
        return brclList;
    }

    private List<TbkDrvFlow> getBRJSZLSQK(List<TbkDrvFlow> resultB) {
        if(resultB == null){
            return null;
        }
        Iterator<TbkDrvFlow> iterator = resultB.iterator();
        while(iterator.hasNext()){
            TbkDrvFlow obj = iterator.next();
            //业务类型
            obj.setYwlx(YWLXStatus.getDesc(obj.getYwlx()));
            //业务原因
            obj.setYwyy(YWLXStatus.getDesc(obj.getYwyy()));
            //准驾车型,暂不翻译
            String zjcx = obj.getZjcx();
//            obj.setZjcx(zjcx+CLLXStatus.getDesc(zjcx));
        }
        return resultB;
    }
    @Override
    public Warning selectByWindow(String handleStatus) {
        return tWarnLnfoMapper.selectByWindow(handleStatus);
    }

    @Override
    public void deleteById(Warning warning) {
        tWarnLnfoMapper.deleteById(warning);
    }

    @Override
    public int update(Warning warning) {
        return tWarnLnfoMapper.update(warning);
    }

    @Override
    public void insert(Warning warning) {
        tWarnLnfoMapper.insertSelective(warning);
    }


}