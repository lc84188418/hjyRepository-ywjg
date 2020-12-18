package com.hjy.hall.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.config.websocket.WebSocket;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.task.ObjectAsyncTask;
import com.hjy.common.utils.Http.HttpClient4;
import com.hjy.common.utils.*;
import com.hjy.common.utils.led.CharReference;
import com.hjy.common.utils.led.MD5Encoder;
import com.hjy.common.utils.led.PD101Ctrl_RZC2;
import com.hjy.common.utils.page.PageResult;
import com.hjy.common.utils.page.PageUtil;
import com.hjy.hall.dao.THallJiashizhengMapper;
import com.hjy.hall.dao.THallJidongcheMapper;
import com.hjy.hall.dao.THallQueueMapper;
import com.hjy.hall.dao.THallTakenumberMapper;
import com.hjy.hall.entity.*;
import com.hjy.hall.service.THallQueueService;
import com.hjy.hall.service.THallTakenumberService;
import com.hjy.list.dao.TListAgentMapper;
import com.hjy.list.dao.TListInfoMapper;
import com.hjy.list.entity.TListAgent;
import com.hjy.list.entity.TListInfo;
import com.hjy.synthetical.entity.TSyntheticalRecord;
import com.hjy.synthetical.service.TSyntheticalRecordService;
import com.hjy.system.dao.TSysBusinesstypeMapper;
import com.hjy.system.dao.TSysParamMapper;
import com.hjy.system.dao.TSysTokenMapper;
import com.hjy.system.dao.TSysWindowMapper;
import com.hjy.system.entity.ActiveUser;
import com.hjy.system.entity.SysToken;
import com.hjy.system.entity.TSysWindow;
import com.hjy.tbk.entity.TbkDrivinglicense;
import com.hjy.tbk.entity.TbkVehicle;
import com.hjy.tbk.service.TbkDrivinglicenseService;
import com.hjy.tbk.service.TbkVehicleService;
import com.hjy.tbk.statusCode.HPStatus;
import com.hjy.tbk.statusCode.SYXZStatus;
import com.hjy.tbk.statusCode.VehicleStatus;
import com.hjy.warning.entity.Warning;
import com.hjy.warning.manager.TbkManager;
import com.hjy.warning.service.TWarnLnfoService;
import com.sun.jna.WString;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * (THallQueue)表服务实现类
 *
 * @author liuchun
 * @since 2020-07-29 14:33:20
 */
@Service
public class THallQueueServiceImpl implements THallQueueService {

    @Autowired
    private THallQueueMapper tHallQueueMapper;
    @Autowired
    private TSysTokenMapper tSysTokenMapper;
    @Autowired
    private TListInfoMapper tListInfoMapper;
    @Autowired
    private THallTakenumberMapper tHallTakenumberMapper;
    @Autowired
    private THallTakenumberService tHallTakenumberService;
    @Autowired
    private THallJiashizhengMapper tHallJiashizhengMapper;
    @Autowired
    private THallJidongcheMapper tHallJidongcheMapper;
    @Autowired
    private TSysWindowMapper tSysWindowMapper;
    @Autowired
    private TSysBusinesstypeMapper businesstypeMapper;
    @Autowired
    private TListAgentMapper tListAgentMapper;
    @Autowired
    private TSysParamMapper tSysParamMapper;
    @Autowired
    private WebSocket webSocket;
    @Resource
    private TbkVehicleService tbkVehicleService;
    @Resource
    private TbkDrivinglicenseService tbkDrivinglicenseService;
    @Autowired
    private TWarnLnfoService warningService;
    @Autowired
    private TSyntheticalRecordService tSyntheticalRecordService;

    /**
     * 通过ID查询单条数据
     */
    @Override
    public THallQueue selectById(String pkQueueId) {
        return this.tHallQueueMapper.selectById(pkQueueId);
    }

    /**
     * 新增数据
     */
    @Override
    public int insert(THallQueue tHallQueue) {
        return tHallQueueMapper.insertSelective(tHallQueue);
    }

    /**
     * 修改数据
     */
    @Override
    public int updateById(THallQueue tHallQueue) {
        return tHallQueueMapper.updateById(tHallQueue);
    }

    /**
     * 通过主键删除数据
     */
    @Override
    public int deleteById(String pkQueueId) {
        return tHallQueueMapper.deleteById(pkQueueId);
    }

    /**
     * 查询多条数据
     */
    @Override
    public List<THallQueue> selectAll() {
        return this.tHallQueueMapper.selectAll();
    }

    //查询当前窗口现在正在办理的号码
    @Override
    public THallQueue getNowNumByWindowName(String remarks) {
        return this.tHallQueueMapper.getNowNumByWindowName(remarks);
    }

    @Override
    public List<Statistics> acceptancePeopleStatistics(String param) throws ParseException {
        /**
         *  默认查询当日统计
         */
        Date queryStart = null;
        Date queryEnd = null;
        String queryStartStr = null;
        String queryEndStr = null;
        if(param != null){
            JSONObject json = JSON.parseObject(param);
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            queryStartStr = JsonUtil.getStringParam(json,"queryStart");
            if(queryStartStr != null && queryStartStr.length()>8){
                queryStart = ft.parse(queryStartStr);
            }
            queryEndStr = JsonUtil.getStringParam(json,"queryEnd");
            if(queryEndStr != null && queryEndStr.length()>8){
                queryEnd = ft.parse(queryEndStr);
            }
        }
        //实体数据
        THallQueue tHallQueue = new THallQueue();
        tHallQueue.setQueryStart(queryStart);
        tHallQueue.setQueryEnd(queryEnd);
        List<Statistics> statisticsList = new ArrayList<>();
        //当日所有办理人员证件号
        List<THallQueue> idCards = tHallQueueMapper.selectAllIdCardOneday(tHallQueue);
        if(idCards != null){
            for(THallQueue entity : idCards){
                Statistics statistics = new Statistics();
                statistics.setAgentIdCard(entity.getIdCard());
                tHallQueue.setIdCard(entity.getIdCard());
                //实际业务量统计-remarks=办结
                tHallQueue.setRemarks("办结");
                int trueNum = tHallQueueMapper.statisticsNum(tHallQueue);
                statistics.setTrueNum(trueNum);
                //空号统计-remarks=空号
                tHallQueue.setRemarks("空号");
                int nullNum = tHallQueueMapper.statisticsNum(tHallQueue);
                statistics.setNullNum(nullNum);
                //退办统计-remarks=退号
                tHallQueue.setRemarks("退号");
                int backNum = tHallQueueMapper.statisticsNum(tHallQueue);
                statistics.setBackNum(backNum);
                //总业务量统计
                statistics.setTotalNum(trueNum+nullNum+backNum);
                /**
                 * 各业务类型统计
                 */
                //当日某办理人员所办的根据业务类型统计
                List<Statistics> list = tHallQueueMapper.acceptancePeopleStatistics(tHallQueue);
                statistics.setAgentName(entity.getAgent());
                for(Statistics obj : list){
                    if(obj.getAgentIdCard().equals(entity.getIdCard())){
                        //业务类型标识ABCDEFG
                        String biaoshi = businesstypeMapper.selectTypeLevelByTypeName(obj.getBusinessType());
                        if(biaoshi.equals("A")){
                            statistics.setA(obj.getTrueNum());
                        } else if(biaoshi.equals("B")){
                            statistics.setB(obj.getTrueNum());
                        } else if(biaoshi.equals("C")){
                            statistics.setC(obj.getTrueNum());
                        } else if(biaoshi.equals("D")){
                            statistics.setD(obj.getTrueNum());
                        } else if(biaoshi.equals("E")){
                            statistics.setE(obj.getTrueNum());
                        } else if(biaoshi.equals("F")){
                            statistics.setF(obj.getTrueNum());
                        } else if(biaoshi.equals("G")){
                            statistics.setG(obj.getTrueNum());
                        } else if(biaoshi.equals("H")){
                            statistics.setH(obj.getTrueNum());
                        } else if(biaoshi.equals("I")){
                            statistics.setI(obj.getTrueNum());
                        }
                    }
                }

                statisticsList.add(statistics);
            }
        }
        return statisticsList;
    }

    //办结
    @Transactional()
    @Override
    public Map<String ,Object> downNum(HttpServletRequest request,HttpSession session,String param) {
        JSONObject jsonObject = JSON.parseObject(param);
        Map<String ,Object> map = new HashMap<>();
        StringBuffer msgBuffer = new StringBuffer();
        //流水号
        String serialNumber = JsonUtil.getStringParam(jsonObject,"serialNumber");
        //从token中拿到当前窗口号
        String tokenStr = TokenUtil.getRequestToken(request);
        SysToken token = tSysTokenMapper.findByToken(tokenStr);
        String ip = token.getIp();
        TSysWindow window = tSysWindowMapper.selectByIp(ip);
        String windowName = window.getWindowName();
        //是否制证标识whether
        String whether = JsonUtil.getStringParam(jsonObject,"whether");
        //代办次数agentNum
        String agentNumstr = JsonUtil.getStringParam(jsonObject,"agentNum");
        if(agentNumstr == null){
            agentNumstr = "0";
        }
        int agentNum = Integer.parseInt(agentNumstr);
        //查询当前窗口正在办理的业务
        THallQueue nowQueue = tHallQueueMapper.getNowNumByWindowName(windowName+"正在办理");
        if (nowQueue == null) {
            map.put("code", 444);
            map.put("status", "error");
            map.put("msg", "该窗口无号可办结！");
            return map;
        }
        nowQueue.setRemarks("办结");
        Date downDate = new Date();
        nowQueue.setEndTime(downDate);
        nowQueue.setSerialNumber(serialNumber);
        int banjieNum = ObjectAsyncTask.updateQueueByEntity(nowQueue);
        if(banjieNum > 0){
            msgBuffer.append(nowQueue.getOrdinal()+"办结成功！");
        }
        //更新取号表状态flag，办结为2
        String ordinal = nowQueue.getOrdinal();
        ObjectAsyncTask.updateTakeNumberFlag(ordinal,2);
        //查询是否已有预警信息
        Warning warning0 = warningService.selectByWindow(windowName+"正在办理");
        /**
         * 预警处理-办结超时
         */
        String warningMsg = ObjectAsyncTask.warning5(nowQueue,warning0);
        /**
         * 异步处理-led大屏文件信息-办结
         */
        String otherMsg = ObjectAsyncTask.downNumberHttp(ordinal);
        if(otherMsg.contains("失败")){
            msgBuffer.append(otherMsg);
        }
        //是否制证
        if(!StringUtils.isEmpty(whether)){
            if("1".equals(whether)){
                int i =ObjectAsyncTask.insertMakeCard(nowQueue);
                if(i>0){
                    msgBuffer.append("录入制证信息成功!");
                }else {
                    msgBuffer.append("录入制证信息失败!");
                }
            }
        }
        //是否录入代办信息
        if(nowQueue.getAIdcard()!= null){
            //录入代办信息
            msgBuffer = this.insertListAgent(nowQueue,msgBuffer);
            //是否自动录入黑名单
            msgBuffer = ObjectAsyncTask.insertBlackList(nowQueue.getAName(),nowQueue.getAIdcard(),agentNum,msgBuffer);
        }
        //将窗口led的原显示内容变为相应窗口号
//        PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(Integer.parseInt(window.getControlCard()),new WString(windowName),0);
//        PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(Integer.parseInt(window.getControlCard()),windowName,0);
        map.put("code", 200);
        map.put("status", "success");
        map.put("msg", msgBuffer.toString());
        return map;
    }

    //退号
    @Transactional()
    @Override
    public Map<String ,Object> backNum(HttpServletRequest request,HttpSession session,String param) {
        Map<String,Object> map = new HashMap<>();
        StringBuffer msgBuffer = new StringBuffer();
        //从token中拿到当前窗口号
        String tokenStr = TokenUtil.getRequestToken(request);
        SysToken token = tSysTokenMapper.findByToken(tokenStr);
        String ip = token.getIp();
        String windowName = tSysWindowMapper.selectWindowNameByIp(ip);
        //查询当前窗口正在办理的业务
        THallQueue nowQueue = tHallQueueMapper.getNowNumByWindowName(windowName+"正在办理");
        if (nowQueue == null) {
            map.put("code", 445);
            map.put("status", "error");
            map.put("msg", "该窗口暂无号，退办失败！");
            return map;
        }
        nowQueue.setRemarks("退号");
        nowQueue.setEndTime(new Date());
        int i = tHallQueueMapper.updateById(nowQueue);
        if(i > 0){
            map.put("code", 200);
            map.put("status", "success");
            msgBuffer.append("退办成功！");
        }else {
            map.put("code", 444);
            map.put("status", "error");
            msgBuffer.append("退办失败！");
        }
        //更新取号表状态flag
        String ordinal = nowQueue.getOrdinal();
        ObjectAsyncTask.updateTakeNumberFlag(ordinal,2);
        /**
         * 异步处理-led大屏文件信息-退号
         */
        ObjectAsyncTask.downNumberHttp(ordinal);
        /**
         * 同步处理，生成告知书
         */
        map = this.insertInform(param,nowQueue,session,msgBuffer,map);
        String warningSerial = (String) map.get("other");
        //预警处理
        Warning warning = ObjectAsyncTask.warning6(nowQueue,warningSerial,param);
        //将预警信息发送到所有窗口
        if(warning != null){
            //新-统一发送预警信息方法
            this.sendAllMessage(warning);
        }
        map.remove("other");
        return map;
    }

    private synchronized Map<String, Object> insertInform(String param, THallQueue nowQueue, HttpSession session, StringBuffer msgBuffer, Map<String, Object> map) {
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        String lack = null;
        JSONObject jsonObject = JSON.parseObject(param);
        String withdrawType = JsonUtil.getStringParam(jsonObject,"withdrawType");
        if(StringUtils.isEmpty(withdrawType)){
            map.put("code", 447);
            map.put("status", "error");
            msgBuffer.append("未选择退办类型！");
            map.put("msg",msgBuffer.toString());
            return map;
        }
        //退办单号
        String backNum = "";
        String inform = String.valueOf(jsonObject.get("inform"));
        //业务类型
        JSONArray jsonArray = jsonObject.getJSONArray("lacks");
        String lacks = jsonArray.toString();
        List<THallJidongche> lackList = JSONArray.parseArray(lacks, THallJidongche.class);
        StringBuffer lackCheck = new StringBuffer();
        if(lackList != null){
            for(THallJidongche obj :lackList){
                lackCheck.append(obj.getLack()+"。");
            }
        }
        lack = lackCheck.toString();
        String otherLack = JsonUtil.getStringParam(jsonObject,"otherLack");
        //档案编号√
        String fileNum = JsonUtil.getStringParam(jsonObject,"dabh");
        //机动车告知书
        if("car".equals(inform)){
            backNum = LSHUtil.getLSH("机动车");
            //号牌号码√
            String numberPlate = JsonUtil.getStringParam(jsonObject,"hphm");
            //号牌种类√
            String numberType = JsonUtil.getStringParam(jsonObject,"hpzl");
            //车辆识别代号√
            String identifyCode = JsonUtil.getStringParam(jsonObject,"clsbdh");
            //机动车业务
            String carBusiness = JsonUtil.getStringParam(jsonObject,"carBusiness");
            THallJidongche tHallJidongche = new THallJidongche();
            String pkId = IDUtils.getUUID();
            tHallJidongche.setPkJidongcheId(pkId);
            tHallJidongche.setDeptName(activeUser.getUnit());
            tHallJidongche.setWithdrawType(withdrawType);
            tHallJidongche.setWithdrawTime(new Date());
            tHallJidongche.setApplicant(nowQueue.getBName());
            tHallJidongche.setIdcard(nowQueue.getBIdcard());
            tHallJidongche.setBusinessType(nowQueue.getBusinessType());
            tHallJidongche.setLack(lack);
            tHallJidongche.setOtherLack(otherLack);
            tHallJidongche.setHandlePeople(activeUser.getFullName());
            tHallJidongche.setOrdinal(nowQueue.getOrdinal());
            tHallJidongche.setAssociationNumber(backNum);
            tHallJidongche.setNumberType(numberType);
            tHallJidongche.setNumberPlate(numberPlate);
            tHallJidongche.setFileNum(fileNum);
            tHallJidongche.setIdentifyCode(identifyCode);
            tHallJidongche.setCarBusiness(carBusiness);
            int i = tHallJidongcheMapper.insertSelective(tHallJidongche);
            if(i > 0){
                msgBuffer.append("生成机动车告知书成功！");
                THallJidongche data = tHallJidongcheMapper.selectById(pkId);
                map.put("data", data);
                map.put("other", backNum);
            }else {
                msgBuffer.append("生成机动车告知书失败！");
            }
            map.put("msg",msgBuffer.toString());
        }else {
            backNum = LSHUtil.getLSH("驾驶证");
            //驾驶证告知书
            //准驾车型
            String drivingModel = JsonUtil.getStringParam(jsonObject,"zjcx");
            //驾驶证办理业务类型
            String licenseBusiness = JsonUtil.getStringParam(jsonObject,"licenseBusiness");
            THallJiashizheng tHallJiashizheng = new THallJiashizheng();
            String pkJiashiId = IDUtils.getUUID();
            tHallJiashizheng.setPkJiashiId(pkJiashiId);
            tHallJiashizheng.setApplicant(nowQueue.getBName());
            tHallJiashizheng.setIdcard(nowQueue.getBIdcard());
            tHallJiashizheng.setHandlePeople(activeUser.getFullName());
            tHallJiashizheng.setBusinessType(nowQueue.getBusinessType());
            tHallJiashizheng.setDeptName(activeUser.getUnit());
            tHallJiashizheng.setWithdrawType(withdrawType);
            tHallJiashizheng.setWithdrawTime(new Date());
            tHallJiashizheng.setLack(lack);
            tHallJiashizheng.setOtherLack(otherLack);
            String remarks = JsonUtil.getStringParam(jsonObject,"remarks");
            tHallJiashizheng.setRemarks(remarks);
            tHallJiashizheng.setOrdinal(nowQueue.getOrdinal());
            tHallJiashizheng.setAssociationNumber(backNum);
            tHallJiashizheng.setFileNum(fileNum);
            tHallJiashizheng.setDrivingModel(drivingModel);
            tHallJiashizheng.setLicenseBusiness(licenseBusiness);
            int j = tHallJiashizhengMapper.insertSelective(tHallJiashizheng);
            if(j > 0){
                msgBuffer.append("生成驾驶证告知书成功！");
                THallJiashizheng data = tHallJiashizhengMapper.selectById(pkJiashiId);
                map.put("data", data);
                map.put("other", backNum);
            }else {
                msgBuffer.append("生成驾驶证告知书失败！");
            }
            map.put("msg",msgBuffer.toString());

        }
        return map;
    }

    //统一发送预警信息接口
    private void sendAllMessage(Warning warning) {
        JSONObject warningJson = new JSONObject();
        warningJson.put("warning",warning.getResultMsg());
        webSocket.sendWarningMessage(warningJson.toJSONString());
    }

    //空号
    @Transactional()
    @Override
    public Map<String, Object> nullNum(HttpServletRequest request,HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        //从token中拿到当前窗口号
        String tokenStr = TokenUtil.getRequestToken(request);
        SysToken token = tSysTokenMapper.findByToken(tokenStr);
        String ip = token.getIp();
        String windowName = tSysWindowMapper.selectWindowNameByIp(ip);
        //查询当前窗口正在办理的业务
        THallQueue nowQueue = tHallQueueMapper.getNowNumByWindowName(windowName+"正在办理");
        if (nowQueue == null) {
            map.put("code", 445);
            map.put("status", "error");
            map.put("msg", "该窗口暂无号，设置空号失败！");
            return map;
        }
        nowQueue.setRemarks("空号");
        nowQueue.setEndTime(new Date());
        this.updateById(nowQueue);
        //预警处理
        ObjectAsyncTask.warningDel(nowQueue);
        //更新取号表状态flag
        String ordinal = nowQueue.getOrdinal();
        ObjectAsyncTask.updateTakeNumberFlag(ordinal,2);
        /**
         * 异步处理-led大屏文件信息-空号
         */
        String resultStr = ObjectAsyncTask.downNumberHttp(ordinal);
        if(resultStr.equals("失败")){
            map.put("code", 444);
            map.put("status", "error");
            map.put("msg", "设置空号失败！");
            return map;
        }
        map.put("code", 200);
        map.put("status", "success");
        map.put("msg", "设置空号成功！");
        return map;
    }

    //导办取号
    @Transactional()
    @Override
    public Map<String, Object> getOrdinal(String param,HttpSession session) throws Exception {
        Map<String, Object> map = new HashMap<>();
        /**
         * 一、大厅是否待办理业务量过多，太多不予取号-
         */
        //查询大厅共等候人数
        int waitAllNum = tHallTakenumberMapper.selectWaitNum("ordinal");
        //查询参数配置中等待业务办理人数标准限值（单位：个），如果没有就默认为100次
        String DDYWBLRSBZXZ = tSysParamMapper.selectParamById("DDYWBLRSBZXZ");
        int max1 = 100;
        if(DDYWBLRSBZXZ != null){
            max1 = Integer.parseInt(DDYWBLRSBZXZ);
        }
        String xianzhi1 = String.valueOf(max1);
        if(waitAllNum>= max1){
            map.put("code", 447);
            map.put("status", "error");
            map.put("msg", "大厅待办理业务量已达限值("+xianzhi1+")，不予取号，请等待");
            return map;
        }
        /**
         * 二、单人单日办理业务空号数超过最大限值（8）超过就不予取号
         */
        JSONObject jsonObject = JSON.parseObject(param);
        String bIdCard = JsonUtil.getStringParam(jsonObject,"bIdCard");
        if(StringUtils.isEmpty(bIdCard)){
            map.put("code", 446);
            map.put("status", "error");
            map.put("msg", "受理人证件号为空");
            return map;
        }
        //查询参数配置中单人单日办理业务空号数超过最大限值（8）超过就不予取号，如果没有就默认为8次
        String DRDRZDKHS = tSysParamMapper.selectParamById("DRDRZDKHS");
        int max2 = 8;
        if(DRDRZDKHS != null){
            max2 = Integer.parseInt(DRDRZDKHS);
        }
        String xianzhi2 = String.valueOf(max2);
        //查询单人单日办理业务空号数
        int nullNum = tHallQueueMapper.selectNullNumToday(bIdCard);
        if(nullNum>= max2){
            map.put("code", 448);
            map.put("status", "error");
            map.put("msg", "单人单日办理业务空号数已达限值("+xianzhi2+")，不予取号，请明天再来！");
            return map;
        }
        /**
         * 三、单人单日办理业务退办数超过最大限值（10次）超过就不予取号
         */
        String DRDRZDTBS = tSysParamMapper.selectParamById("DRDRZDTBS");
        int max3 = 10;
        if(DRDRZDTBS != null){
            max3 = Integer.parseInt(DRDRZDTBS);
        }
        String xianzhi3 = String.valueOf(max3);
        //查询单人单日办理业务退办数
        int backNum = tHallQueueMapper.selectBackNumToday(bIdCard);
        if(backNum>= max3){
            map.put("code", 449);
            map.put("status", "error");
            map.put("msg", "单人单日办理业务退办数已达限值("+xianzhi3+")，不予取号，请明天再来！");
            return map;
        }
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        /**
         * 一、线程同步方法-取号，存入取号
         */
        map = this.getOrdinalThread(map,jsonObject,bIdCard,activeUser);
        return map;
    }


    //通过排队号获取排队信息表中距离当前时间最近的排队信息
    @Override
    public THallQueue getCallNum(String ordinal) {
        return tHallQueueMapper.getCallNum(ordinal);
    }

    //排队信息查询
    @Override
    public PageResult selectAllPage(String param) throws ParseException {
        JSONObject json = JSON.parseObject(param);
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ordinal = JsonUtil.getStringParam(json,"ordinal");
        String windowName = JsonUtil.getStringParam(json,"windowName");
        String agent = JsonUtil.getStringParam(json,"agent");
        String businessType = JsonUtil.getStringParam(json,"businessType");
        String remarks = JsonUtil.getStringParam(json,"remarks");
        String bName = JsonUtil.getStringParam(json,"bName");
        String bIdcard = JsonUtil.getStringParam(json,"bIdcard");
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
        THallQueue tHallQueue = new THallQueue();
        tHallQueue.setOrdinal(ordinal);
        tHallQueue.setWindowName(windowName);
        tHallQueue.setAgent(agent);
        tHallQueue.setBusinessType(businessType);
        tHallQueue.setRemarks(remarks);
        tHallQueue.setRemarks(remarks);
        tHallQueue.setBName(bName);
        tHallQueue.setBIdcard(bIdcard);
        tHallQueue.setQueryStart(queryStart);
        tHallQueue.setQueryEnd(queryEnd);
        //分页记录条数
        int total = tHallQueueMapper.selectSize(tHallQueue);
        PageResult result = PageUtil.getPageResult(param,total);
        List<THallQueue> queues = tHallQueueMapper.selectAllPage(result.getStartRow(),result.getEndRow(),ordinal,windowName,agent,businessType,remarks,bName,bIdcard,queryStart,queryEnd);
        //先排序
        if(queues !=null){
            Collections.sort(queues, new Comparator<THallQueue>() {
                @Override
                public int compare(THallQueue o1, THallQueue o2) {
                    //升序
                    return o1.getGetTime().compareTo(o2.getGetTime());
                }
            });
            List<THallQueue> queueList = queues;
            result.setContent(queueList);
        }
        return result;
    }

    //预警统计
    @Override
    public PageResult WarningCount(String param) {
        JSONObject json = JSON.parseObject(param);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String queryStartStr = String.valueOf(json.get("queryStart"));
        String queryEndStr = String.valueOf(json.get("queryEnd"));
        if(queryStartStr != null && queryStartStr.length()<8){
            queryStartStr = null;
        }
        if(queryEndStr != null && queryEndStr.length()<8){
            queryEndStr = null;
        }
        String orderBy = JsonUtil.getStringParam(json,"orderBy");
        if(orderBy == null){
            orderBy = "按日期统计";
        }
        String paramServiceStr= tSysParamMapper.selectParamById("PTCJGYWCS");
        String paramWaitStr= tSysParamMapper.selectParamById("DDCSSJXZ");
        int serviceOverTime = 900;
        int waitOverTime = 1800;
        if(paramServiceStr != null){
            serviceOverTime=Integer.parseInt(paramServiceStr);
        }
        if(paramWaitStr != null){
            waitOverTime=Integer.parseInt(paramWaitStr);
        }
        //分页记录条数
        int total = 0;
        if(orderBy.equals("按日期统计")){
            total = tHallQueueMapper.selectSizeWarningCount();
            PageResult result = PageUtil.getPageResult(param,total);
            List<THallQueueCount> list = tHallQueueMapper.selectWarningCountAllPage(result.getStartRow(),result.getEndRow(),queryStartStr,queryEndStr,serviceOverTime,waitOverTime);
            result.setContent(list);
            return result;
        }
        return null;
    }
    //叫号页面
    @Override
    public Map<String,Object> callPage(HttpServletRequest request) {
        Map<String,Object> map = new HashMap<>();
        JSONObject resultJson = new JSONObject();
        //从请求中获取tokenid
        String tokenStr = TokenUtil.getRequestToken(request);
        //通过tokenId获取token信息
        SysToken token = tSysTokenMapper.findByToken(tokenStr);
        String ip = token.getIp();
        //通过ip查询窗口信息
        TSysWindow window = tSysWindowMapper.selectByIp(ip);
        if(window == null){
            map.put("code",445);
            map.put("status", "error");
            map.put("msg", "该ip暂无匹配的窗口，请配置后重试!");
            return map;
        }
        String windowName = window.getWindowName();
        resultJson.put("windowName", windowName);
        //获取当前窗口可办理的业务类型
        String businesstypeList = window.getBusinessType();
        if(businesstypeList == null){
            map.put("code",446);
            map.put("status", "error");
            map.put("msg", "该窗口暂未配置办理业务类型，请配置后重试!");
            return map;
        }
        //查询当前窗口是否存在正在办理的业务
        THallQueue tHallQueue = tHallQueueMapper.getNowNumByWindowName(windowName+"正在办理");
        if(tHallQueue != null) {
            resultJson = this.getResultJson(tHallQueue);
            map.put("code",201);
            map.put("status", "success");
            map.put("msg", "当前还有正在处理的号码!");
            map.put("data", resultJson);
            return map;
        }else {
            //判断该窗口服务是否开启中
            Integer serviceStatus = window.getServiceStatus();
            resultJson.put("serviceStatus", serviceStatus);
            map.put("data", resultJson);
            if(serviceStatus != null && serviceStatus == 0){
                map.put("code",447);
                map.put("status", "error");
                map.put("msg", "该窗口已暂停服务！");
            }else {
                map.put("code",200);
                map.put("status", "success");
                map.put("msg", "获取数据成功!");
            }
            return map;
        }
    }
    //顺序叫号
    @Transactional()
    @Override
    public CommonResult orderCall(HttpServletRequest request, HttpSession session) throws Exception{
        JSONObject resultJson = new JSONObject();
        CommonResult commonResult = new CommonResult();
        //从token中拿到当前窗口信息
        String tokenStr = TokenUtil.getRequestToken(request);
        SysToken token = tSysTokenMapper.selectIpAndName(tokenStr);
        String ip = token.getIp();
        TSysWindow window = tSysWindowMapper.selectByIp(ip);
        String windowName = window.getWindowName();
        String businessType = window.getBusinessType();
        //查询当前窗口是否存在正在办理的业务
        THallQueue resultQueue = new THallQueue();
        THallQueue tHallQueue = tHallQueueMapper.getNowNumByWindowName(windowName+"正在办理");
        if(tHallQueue != null){
            resultQueue = tHallQueue;
            commonResult.setCode(446);
            commonResult.setStatus("success");
            commonResult.setMsg("该窗口还有未办结业务，请先处理!");
        }else {
            //判断该窗口服务是否开启中
            Integer serviceStatus = window.getServiceStatus();
            if(serviceStatus != null && serviceStatus == 0){
                commonResult.setCode(447);
                commonResult.setStatus("success");
                commonResult.setMsg("该窗口已暂停服务！");
                commonResult.setData(serviceStatus);
                return commonResult;
            }
            //业务处理逻辑
            ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
            String agent = token.getFullName();
            String idCard = null;
            if(activeUser != null ){
                idCard=activeUser.getIDcard();
            }
            //通过此工具类可以将该窗口可办理的业务类型转化为字母显示且已经排序的List集合[B,C]
            List<String> typeList = null;
            //如[B,C]
            typeList = typeTransUtil.typeTrans(businessType);
            //*****判断该窗口所办理的业务类型是否有号
            String ordinal = "";
            //mark为判断该窗口所办理的业务类型是否有号的标识
            int mark = 0;
            //通过foreach判断是否有号，如果typeList所包含的所有业务类型都在数据库中查询不到号码,则该窗口已无号可办,即typeList.size()= mark
            for (String typeSingle : typeList) {
                String takenumbers = tHallTakenumberMapper.getMinOrdinal(typeSingle);
                if (takenumbers != null) {
                    ordinal = takenumbers;
                    break;
                } else {
                    mark++;
                }
            }
            if (typeList.size() == mark) {
                return new CommonResult(444, "error", "该窗口已无号", null);
            }
            //异步处理取号表的flag标识
            ObjectAsyncTask.updateTakeNumberFlag(ordinal,1);
            /**
             * 线程同步处理-led大屏文件信息-顺序叫号
             */
            String callMsg = this.callNumHttp(ordinal,windowName);
            //异步处理更新排队信息表
            resultQueue = ObjectAsyncTask.updateQueue(ordinal,windowName,agent,idCard);
        }
        if(resultQueue != null){
            resultJson = this.getResultJson(resultQueue);
            commonResult.setCode(200);
            commonResult.setStatus("success");
            commonResult.setMsg(resultQueue.getWindowName()+":叫号成功！"+resultQueue.getOrdinal());
            commonResult.setData(resultJson);
            /**
             * 预警处理-叫号预警
             */
            StringBuffer resultMsgBuffer = new StringBuffer();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowDateStr = simpleDateFormat.format(new Date());
            resultMsgBuffer.append(nowDateStr);
            Warning warning347910 = ObjectAsyncTask.warning347910(resultQueue,resultMsgBuffer, (TListInfo) resultJson.get("BList"));
            //先不发送预警信息到窗口，获取同步库数据后在发送
            /**
             * 线程同步发送叫号信息
             */
            String callNumMsg = this.callNumSendMsg(resultQueue.getOrdinal(),window);
            return commonResult;
        }else {
            return new CommonResult(445, "error", "该窗口已无号", null);
        }
    }

    //特呼
    @Transactional()
    @Override
    public Map<String, Object> queueVipCall(HttpServletRequest request,HttpSession session,THallQueue tHallQueue)throws Exception{
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        Map<String, Object> map = new HashMap<>();
        JSONObject resultJson = new JSONObject();
        //从token中拿到当前窗口信息
        String tokenStr = TokenUtil.getRequestToken(request);
        SysToken token = tSysTokenMapper.findByToken(tokenStr);
        String ip = token.getIp();
        TSysWindow window = tSysWindowMapper.selectWindowByIp(ip);
        String windowName = window.getWindowName();
        //判断是否存在输入的号码
        String vip_ordinal = tHallQueue.getOrdinal();
        if (tHallTakenumberMapper.wetherExist(vip_ordinal) == 0) {
            map.put("code",444);
            map.put("status", "error");
            map.put("msg", "特呼的号码不存在!");
            return map;
        }
        List<THallQueue> vipQueues = new ArrayList<>();
        //查询当前窗口是否存在正在办理的业务
        THallQueue handingtHallQueue = tHallQueueMapper.getNowNumByWindowName(windowName+"正在办理");
        if(handingtHallQueue != null){
            map.put("code",446);
            map.put("status", "error");
            map.put("msg", "该窗口还有未办结业务，请先处理!");
            resultJson = this.getResultJson(handingtHallQueue);
            map.put("data", resultJson);
            return map;
        }else {
            vipQueues = tHallQueueMapper.vip_ordinalQueue(vip_ordinal);
        }
        if(vipQueues.size() == 0){
            map.put("code",447);
            map.put("status", "error");
            //删除取号表有号，但排队信息中无号的取号信息
            String shanchuStr = tHallTakenumberService.deleteByOrdinal(vip_ordinal);
            map.put("msg", "该号码在排队信息中未有记录，已删除，请重新排号");
            return map;
        }
        THallQueue queueVip = vipQueues.get(0);
        String tempBIdCard = null;
        String tempAIdCard = null;
        if(vipQueues.size() == 1){
            //如果remarks为空，则说明未办理过
            if(queueVip.getRemarks() == null){
                //修改为特呼号码
                //修改,就不新增排队信息
                //异步处理取号表的flag标识
                ObjectAsyncTask.updateTakeNumberFlag(vip_ordinal,1);
                queueVip.setWindowName(windowName);
                queueVip.setAgent(activeUser.getFullName());
                queueVip.setStartTime(new Date());
                queueVip.setRemarks(windowName+"正在办理");
                queueVip.setIsVip(1);
                queueVip.setIdCard(activeUser.getIDcard());
                this.updateById(queueVip);
            }else if("空号".equals(queueVip.getRemarks()) || "办结".equals(queueVip.getRemarks())) {
                ObjectAsyncTask.updateTakeNumberFlag(queueVip.getOrdinal(),1);
                //说明该号码已经办理过一次，则重新添加
                //重新生成
                THallQueue queueinsert = new THallQueue();
                //需要将endtime置空，queueVIP.setEndTime(null)无用，故新建个对象
                queueinsert.setPkQueueId(IDUtils.getUUID());
                queueinsert.setOrdinal(queueVip.getOrdinal());
                queueinsert.setWindowName(queueVip.getWindowName());
                queueinsert.setAgent(queueVip.getAgent());
                queueinsert.setBusinessType(queueVip.getBusinessType());
                if (queueVip.getAIdcard() != null) {
                    queueinsert.setAIdcard(queueVip.getAIdcard());
                    queueinsert.setAName(queueVip.getAName());
                    queueinsert.setACertificatesType(queueVip.getACertificatesType());
                }
                queueinsert.setBIdcard(queueVip.getBIdcard());
                queueinsert.setBName(queueVip.getBName());
                queueinsert.setBCertificatesType(queueVip.getBCertificatesType());
                queueinsert.setGetTime(queueVip.getGetTime());
                queueinsert.setStartTime(new Date());
                queueinsert.setRemarks(windowName+"正在办理");
                queueinsert.setIdCard(queueVip.getIdCard());
                queueinsert.setIsVip(1);
                queueinsert.setDaobanPeople(queueVip.getDaobanPeople());
                queueinsert.setDaobanIdcard(queueVip.getDaobanIdcard());
                this.insert(queueinsert);
            }else if("退号".equals(queueVip.getRemarks())){
                map.put("code",445);
                map.put("status", "error");
                map.put("msg", "特呼的号码已退号!");
                return map;
            }
        }else if(vipQueues.size() >= 2){
            //如果该号码已被多次办理，查询是否存在被退号的信息，如果有被退号，则之后也不能进行特呼
            for(THallQueue entity:vipQueues){
                if("退号".equals(entity.getRemarks())){
                    map.put("code",445);
                    map.put("status", "error");
                    map.put("msg", "特呼的号码已退号!");
                    return map;
                }
            }
            ObjectAsyncTask.updateTakeNumberFlag(queueVip.getOrdinal(),1);
            //重新生成
            THallQueue queueinsert = new THallQueue();
            queueinsert.setPkQueueId(IDUtils.getUUID());
            queueinsert.setOrdinal(queueVip.getOrdinal());
            queueinsert.setWindowName(windowName);
            queueinsert.setAgent(activeUser.getFullName());
            queueinsert.setBusinessType(queueVip.getBusinessType());
            if (queueVip.getAIdcard() != null) {
                queueinsert.setAIdcard(queueVip.getAIdcard());
                queueinsert.setAName(queueVip.getAName());
                queueinsert.setACertificatesType(queueVip.getACertificatesType());
            }
            queueinsert.setBIdcard(queueVip.getBIdcard());
            queueinsert.setBName(queueVip.getBName());
            queueinsert.setBCertificatesType(queueVip.getBCertificatesType());
            queueinsert.setGetTime(queueVip.getGetTime());
            queueinsert.setStartTime(new Date());
            queueinsert.setRemarks(windowName+"正在办理");
            queueinsert.setIdCard(activeUser.getIDcard());
            queueinsert.setIsVip(1);
            queueinsert.setDaobanPeople(queueVip.getDaobanPeople());
            this.insert(queueinsert);
        }
        /**
         * 异步处理-led大屏文件信息-特呼
         */
        ObjectAsyncTask.vipcallNumberHttp(vip_ordinal,windowName);
        /**
         * 同步处理-led窗口屏信息
         */
        String callNumMsg = this.callNumSendMsg(vip_ordinal,window);
        resultJson = this.getResultJson(queueVip);
        map.put("code",200);
        map.put("status", "success");
        map.put("msg", windowName + "特呼:" + vip_ordinal+"成功");
        map.put("data", resultJson);
        return map;
    }

    //重播
    @Override
    public Map<String, Object> repaly(HttpServletRequest request,String param)  throws Exception{
        Map<String, Object> map = new HashMap<>();
        JSONObject jsonObject = JSON.parseObject(param);
        String ordinal = String.valueOf(jsonObject.get("ordinal"));
        //从token中拿到当前窗口信息
        String tokenStr = TokenUtil.getRequestToken(request);
        SysToken token = tSysTokenMapper.selectIpAndName(tokenStr);
        String ip = token.getIp();
        TSysWindow window = tSysWindowMapper.selectWindowByIp(ip);
        //异步呼叫
        String callNumMsg = this.callNumSendMsg(ordinal,window);
        if(callNumMsg.contains("成功")){
            map.put("code", 200);
            map.put("status", "success");
            map.put("msg", "请"+ordinal+"到"+window.getWindowName());
            return map;
        }else {
            map.put("code", 444);
            map.put("status", "error");
            map.put("msg", "重新呼叫失败");
            return map;
        }

    }
    //主页-各业务类型统计
    @Override
    public List<THallQueueCount> indexDataBusinessToday() {
        return tHallQueueMapper.indexDataBusinessToday();
    }
    //主页-各窗口统计
    @Override
    public List<THallQueueCount> indexDataWindowNumToday() {
        return tHallQueueMapper.indexDataWindowNumToday();
    }
    //主页-最近办理业务统计
    @Override
    public List<THallQueue> selectNearlyToday(int i) {
        return tHallQueueMapper.selectNearlyToday(i);
    }
    //主页-各受理人员办理量统计
    @Override
    public List<THallQueueCount> indexDataAgentNumToday(String startStr, String endStr, int serviceOverTime) {
        return tHallQueueMapper.indexDataAgentNumToday(startStr,endStr,serviceOverTime);
    }
    @Override
    public void deteleBeforeData() {
        tHallQueueMapper.deteleBeforeData();
    }
    //定时任务，处理未办结的业务
    @Override
    public void deteleNoHandBeforeData() {
        tHallQueueMapper.deteleNoHandBeforeData();
    }
    //统计分析-值日警官工作量统计
    @Override
    public List<THallQueueCount> dutyStatistics(String param)throws Exception {
        /**
         *  默认查询当日统计
         */
        Date queryDate = null;
        String queryDateStr = null;
        if(param != null){
            JSONObject json = JSON.parseObject(param);
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
            queryDateStr = JsonUtil.getStringParam(json,"queryDate");
            if(queryDateStr != null && queryDateStr.length()>8){
                queryDate = ft.parse(queryDateStr);
            }
        }
        //实体数据
        THallQueue tHallQueue = new THallQueue();
        tHallQueue.setQueryStart(queryDate);
        List<THallQueueCount> resultList = tHallQueueMapper.dutyStatistics(tHallQueue);
        return resultList;
    }

    //获取同步库数据
    @Override
    public Map<String, Object> getTbkData(String param,HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        JSONObject resultJson = new JSONObject();
        JSONObject json = JSON.parseObject(param);
        String bIdCard = JsonUtil.getStringParam(json,"bIdcard");
        String callType = JsonUtil.getStringParam(json,"callType");
        /**
         * 查询是否需要获取同步库数据
         */
        //查询参数配置中加入黑名单代办次数限值，如果没有就默认为5次
        String value = tSysParamMapper.selectParamById("SFHQTBKSJ");
        String SFHQTBKSJ = "否";
        if(value != null){
            SFHQTBKSJ = value;
        }
        if (SFHQTBKSJ.equals("否")) {
            map.put("code",202);
            map.put("data",resultJson);
            map.put("msg","系统参数配置中已设置为不获取同步库数据！");
            return map;
        }else {
            if(bIdCard != null){
                List<TbkVehicle> brclList =new ArrayList<>();
                List<TbkDrivinglicense> brjszList =new ArrayList<>();
                //是否使用测试数据
                String turnon = PropertiesUtil.getValue("test.whether.brcl.data.info");
                if(turnon.equals("true")) {
                    brclList = TbkManager.getTbkVehicleList(bIdCard);
                    brclList = this.getBRCLQK(brclList);
                    resultJson.put("car",brclList);
//                    brjszList = TbkManager.getTbkDrivinglicenseList(bIdCard);
//                    resultJson.put("license",brjszList);
                }else {
                    //若不使用测试数据，就直接查同步库数据
                    //本人所有车辆信息
                    brclList = tbkVehicleService.selectByIdCard(bIdCard);
                    brclList = this.getBRCLQK(brclList);
                    resultJson.put("car",brclList);
//                    //本人驾驶证信息
//                    brjszList = tbkDrivinglicenseService.selectByIdCard(bIdCard);
//                    brjszList = this.getBRJSZQK(brjszList);
//                    resultJson.put("license",brjszList);
                }
                /**
                 * 生成预警信息,只在特呼一顺序叫号时，callType为1
                 * 访问叫号页面接口后访问同步库接口时，callType为0
                 */
                if("1".equals(callType)){
                    //从token中拿到当前窗口信息
                    String tokenStr = TokenUtil.getRequestToken(request);
                    SysToken token = tSysTokenMapper.findByToken(tokenStr);
                    String ip = token.getIp();
                    TSysWindow window = tSysWindowMapper.selectWindowByIp(ip);
                    String windowName = window.getWindowName();
                    //查询是否当前业务是否已有预警信息
                    Warning warning = warningService.selectByWindow(windowName+"正在办理");
                    Warning warning12 = new Warning();
                    if(warning != null){
                        warning12 = warningService.warning12_2(warning,brclList,brjszList);
                        //修改
                        warningService.update(warning12);
                    }else {
                        //重新生成预警信息
                        //查询排队信息
                        THallQueue queue = tHallQueueMapper.getNowNumByWindowName(windowName+"正在办理");
                        warning12 = ObjectAsyncTask.warning12_1(queue,brclList,brjszList);
                        //添加-是否存在机动车或驾驶证异常
                        if(warning12 !=null){
                            warningService.insert(warning12);
                        }
                    }
                    //将预警信息发送到所有窗口
                    if(warning12 != null){
                        //统一发送预警信息方法
                        this.sendAllMessage(warning12);
                    }
                }
                map.put("code",200);
                map.put("msg","同步库数据获取成功！");
            }else {
                map.put("code",201);
                map.put("msg","受理人证件号未传递，请联系维护人员");
            }
        }
        map.put("data",resultJson);
        return map;
    }

    //将本人车辆信息做修饰
    private List<TbkVehicle> getBRCLQK(List<TbkVehicle> brclList) {
        if(brclList == null){
            return null;
        }
        Iterator<TbkVehicle> iterator = brclList.iterator();
        while(iterator.hasNext()){
            TbkVehicle obj = iterator.next();
            if(obj.getZt().contains("E") || obj.getZt().equals("A")|| obj.getZt().contains("C")){
                iterator.remove();
                continue;
            }
            StringBuffer cllxBuffer = new StringBuffer();
            StringBuffer ztBuffer = new StringBuffer();
            StringBuffer syxzBuffer = new StringBuffer();
//            StringBuffer tempBuffer1 = new StringBuffer();
//            //准驾车型
//            String [] zjcxstrings = obj.getZjcx().split("");
//            int i =1;
//            //翻译
//            for(String zjcx:zjcxstrings){
//                String msg = CLLXStatus.getDesc(zjcx);
//                tempBuffer1.append(i+"."+msg);
//                i++;
//            }
//            if(!StringUtils.isEmpty(cllxBuffer.toString())){
//                cllxBuffer.append("/"+tempBuffer1.toString());
//            }else {
//                cllxBuffer.append(tempBuffer1.toString());
//            }
            StringBuffer tempBuffer2 = new StringBuffer();
            //机动车状态
            String [] ztstrings = obj.getZt().split("");
            int j =1;
            //翻译
            for(String zt:ztstrings){
                String msg = VehicleStatus.getDesc(zt);
                tempBuffer2.append(j+"."+msg);
                j++;
            }
            if(!StringUtils.isEmpty(ztBuffer.toString())){
                ztBuffer.append("/"+tempBuffer2.toString());
            }else {
                ztBuffer.append(tempBuffer2.toString());
            }
            //号牌种类
            obj.setHpzl(HPStatus.getDesc(obj.getHpzl()));
            StringBuffer tempBuffer4 = new StringBuffer();
            //使用性质
            String [] syxzstrings = obj.getSyxz().split("");
            int m =1;
            //翻译
            for(String syxz:syxzstrings){
                String msg = SYXZStatus.getDesc(syxz);
                tempBuffer4.append(m+"."+msg);
                m++;
            }
            if(!StringUtils.isEmpty(syxzBuffer.toString())){
                syxzBuffer.append("/"+tempBuffer4.toString());
            }else {
                syxzBuffer.append(tempBuffer4.toString());
            }
            //---------
            obj.setExceptionReason(ztBuffer.toString());
            obj.setSyxz(syxzBuffer.toString());
        }
        return brclList;
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
            //ACEFG的不要
            if(status.contains("A") ||status.contains("C")||status.contains("E")||status.contains("F")||status.contains("G")){
                iterator.remove();
                continue;
            }
        }
        return brjszList;
    }

    //录入代办信息
    private StringBuffer insertListAgent(THallQueue nowQueue, StringBuffer msgBuffer) {
        TListAgent agent = new TListAgent();
        agent.setPkAgentId(IDUtils.getUUID());
        agent.setBusinessType(nowQueue.getBusinessType());
        agent.setACertificatesType(nowQueue.getACertificatesType());
        agent.setAName(nowQueue.getAName());
        agent.setAIdcard(nowQueue.getAIdcard());
        agent.setBCertificatesType(nowQueue.getBCertificatesType());
        agent.setBName(nowQueue.getBName());
        agent.setBIdcard(nowQueue.getBIdcard());
        agent.setAddTime(new Date());
        agent.setAgent(nowQueue.getAgent());
        agent.setAgent("系统添加");
        int i = ObjectAsyncTask.insertListAgent(agent);
        if(i>0){
            msgBuffer.append("录入代办信息成功!");
        }else {
            msgBuffer.append("录入代办信息失败!");
        }
        return msgBuffer;
    }

    //当拿到排队信息表的时候，获取相应信息
    public JSONObject getResultJson(THallQueue resultQueue){
        JSONObject resultJson = new JSONObject();
        int handleNum = 0;
        int agentNum = 0;
        String bidcard = resultQueue.getBIdcard();
        //查询受理人是否在黑红名单中
        TListInfo infoB = tListInfoMapper.selectByIdCard(bidcard);
        if (infoB != null) {
            resultJson.put("BList",infoB);
        }else {
            resultJson.put("BList",null);
        }
        if(resultQueue.getAIdcard()== null){
            handleNum = tHallQueueMapper.handleNum(resultQueue.getBIdcard());
            agentNum = tHallQueueMapper.agentNum(resultQueue.getBIdcard());
        }else {
            handleNum = tHallQueueMapper.handleNum(resultQueue.getAIdcard());
            agentNum = tHallQueueMapper.agentNum(resultQueue.getAIdcard());
            //查询代理人是否在黑名单中
            TListInfo infoA = tListInfoMapper.selectByIdCard(resultQueue.getAIdcard());
            if (infoA != null) {
                resultJson.put("AList",infoA);
            }else {
                resultJson.put("AList",null);
            }
        }
        resultQueue.setAgentNum(agentNum);
        resultQueue.setHandleNum(handleNum);
        resultJson.put("queue",resultQueue);
        //办结时是否需要输入流水号
        String businessType1 = resultQueue.getBusinessType();
        String whether = businesstypeMapper.selectWhetherNullByBusinessType(businessType1);
        resultJson.put("serialNumber",whether);
        return resultJson;
    }
    //线程同步取号
    private synchronized Map<String, Object> getOrdinalThread(Map<String, Object> map, JSONObject jsonObject,String bIdCard,ActiveUser activeUser) {
        String businessType = JsonUtil.getStringParam(jsonObject,"businessType");
        String bCertificatesType = JsonUtil.getStringParam(jsonObject,"bCertificatesType");
        String bName = JsonUtil.getStringParam(jsonObject,"bName");
        String isAgent = JsonUtil.getStringParam(jsonObject,"isAgent");
        /**
         * 开始取号
         */
        //1拿到该业务类型的标识
        String sign = businesstypeMapper.selectTypeLevelByTypeName(businessType);
        //2取得最大的号码
        String ordinal = this.getMaxTackNumber(sign);
        THallQueue tHallQueue = new THallQueue();
        //查询办理次数
        int handleNum = tHallQueueMapper.handleNum(bIdCard);
        //查询代理次数
        int agentNum = tHallQueueMapper.agentNum(bIdCard);
        //本人信息
        tHallQueue.setBIdcard(bIdCard);
        tHallQueue.setBusinessType(businessType);
        tHallQueue.setBCertificatesType(bCertificatesType);
        //代理业务
        if(!isAgent.equals("1")) {
            String aIdCard = JsonUtil.getStringParam(jsonObject,"aIdCard");
            String aName = JsonUtil.getStringParam(jsonObject,"aName");
            String aCertificatesType = JsonUtil.getStringParam(jsonObject,"aCertificatesType");
            //查询办理次数
            handleNum = tHallQueueMapper.handleNum(aIdCard);
            //查询代理次数
            agentNum = tHallQueueMapper.agentNum(aIdCard);
            tHallQueue.setAName(aName);
            tHallQueue.setAIdcard(aIdCard);
            tHallQueue.setACertificatesType(aCertificatesType);
            //如果受理人不是红名单，代理次数受限制
            TListInfo infoA = tListInfoMapper.selectByIdCard(aIdCard);
            TListInfo infoB = tListInfoMapper.selectByIdCard(bIdCard);
            if(!(infoA != null && "红名单".equals(infoA.getListType()) || infoB != null && "红名单".equals(infoB.getListType()))){
                //查询参数配置中加入黑名单代办次数限值，如果没有就默认为5次
                String value = tSysParamMapper.selectParamById("JRHMDDBCSXZ");
                int max = 5;
                if(value != null){
                    max = Integer.parseInt(value);
                }
                if (agentNum >= max) {
                    map.put("code", 446);
                    map.put("status", "error");
                    map.put("msg", "该代办人代理次数已达"+max+"次！不予取号");
                    return map;
                }
            }
            boolean flag = true;
            while (flag){
                //如果本人姓名没在取号时录入，就从其他地方录入
                if(StringUtils.isEmpty(bName)){
                    //1从黑红名单找
                    if(infoB != null){
                        bName = infoB.getFullName();
                        flag =false;
                        break;
                    }
                    //2从备案信息去找
                    List<TSyntheticalRecord> tSyntheticalRecords = tSyntheticalRecordService.selectByZzjgdm(bIdCard);
                    if(tSyntheticalRecords.size()>=1){
                        bName = tSyntheticalRecords.get(0).getDwMc();
                        flag =false;
                        break;
                    }
                    //3.1从排队信息去查受理人姓名
                    String resultBName = tHallQueueMapper.selectBNameByIdCard(bIdCard);
                    if(!StringUtils.isEmpty(resultBName)){
                        bName = resultBName;
                        flag =false;
                        break;
                    }
                    //3.2从排队信息去查代理人姓名
                    String resultAName = tHallQueueMapper.selectANameByIdCard(bIdCard);
                    if(!StringUtils.isEmpty(resultAName)){
                        bName = resultAName;
                        flag =false;
                        break;
                    }
                    //4从同步库去查

                }
                flag =false;
                break;
            }
        }
        //录入处理后的本人名称
        tHallQueue.setBName(bName);
        tHallQueue.setHandleNum(handleNum);
        tHallQueue.setAgentNum(agentNum);
        /**
         * 存储取号信息
         */
        THallTakenumber takenumber = new THallTakenumber();
        String pk_id = IDUtils.getUUID();
        takenumber.setPkTakenumId(pk_id);
        takenumber.setOrdinal(ordinal);
        takenumber.setFlag(0);
        Date nowdate = new Date();
        takenumber.setGetTime(nowdate);
        tHallTakenumberMapper.insertSelective(takenumber);
        //查询前方等候人数
        int waitNum = tHallTakenumberMapper.selectWaitNum(ordinal);
        /**EVALUATE
         * 存储排队信息
         */
        tHallQueue.setGetTime(nowdate);
        tHallQueue.setOrdinal(ordinal);
        tHallQueue.setIsVip(0);
        tHallQueue.setPkQueueId(pk_id);
        tHallQueue.setWaitNum(waitNum);
        tHallQueue.setDaobanPeople(activeUser.getFullName());
        tHallQueue.setDaobanIdcard(activeUser.getIDcard());
        ObjectAsyncTask.insertQueue(tHallQueue);
        //
        map.put("code", 200);
        map.put("status", "success");
        map.put("msg", "取号成功!");
        map.put("ordinalQueue", tHallQueue);
        //查询配置文件中，导办取号电脑的ip地址
        String getOrdinalIp = PropertiesUtil.getValue("webSocket.getqueue.ip");
        if(getOrdinalIp.contains(activeUser.getIp())){
            map.put("msg", "取号成功!");
        }else {
            map.put("msg", "取号成功!非取号电脑，无法打印");
        }
        return map;
    }

    //取得最大的号码
    private String getMaxTackNumber(String sign) {
        //如F010
        String maxNum = tHallTakenumberMapper.getMaxTackNumber(sign);
        if(maxNum == null){
            //此次为第一次取号
            return sign+"001";
        }else {
            int ordinal_num = 0;
            //去掉业务类型标识 ,num3 = 010
            String num3 = maxNum.substring(1,maxNum.length());
            String num2 = maxNum.substring(2,maxNum.length());
            String num1 = maxNum.substring(3,maxNum.length());
            //第一个字符 0
            String firstChar = maxNum.substring(1,2);
            if(firstChar.equals("0")){
                //第二个字符 1
                String secondChar = maxNum.substring(2,3);
                if(secondChar.equals("0")){
                    //转换为int类型
                    ordinal_num = Integer.parseInt(num1)+1;
                }else {
                    //转换为int类型
                    ordinal_num = Integer.parseInt(num2)+1;
                }
            }else {
                //转换为int类型
                ordinal_num = Integer.parseInt(num3)+1;
            }
            String ordinal = "";
            //三位A001
            if (ordinal_num <= 9) {
                ordinal = sign + "00" + ordinal_num;
            } else if (ordinal_num <= 99) {
                ordinal = sign + "0" + ordinal_num;
            } else {
                ordinal = sign + ordinal_num;
            }
            return ordinal;
        }
    }

    public synchronized String callNumSendMsg(String ordinal,TSysWindow window) throws Exception{
        //同步处理发送叫号信息
        JSONObject json = new JSONObject();
        String sendTextMessage = "请"+ordinal+"到"+window.getWindowName();
        json.put("call",sendTextMessage);
        webSocket.sendTextMessageTo(json.toJSONString());
        //调用LED控制卡发送消息到屏幕上
        String msg = "请"+ordinal+"号办理0";
        System.err.println("发送单一颜色的字串:"+msg);
        byte[] bytes2 = new byte[1024];
        bytes2 = msg.getBytes("gb2312");
//        int nCardId = Integer.parseInt(window.getControlCard());
        PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(1,msg.getBytes("gb2312"),0);
        PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(2,msg.getBytes("gbk"),0);
        PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(3,msg.getBytes("gbk"),0);
        PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(4,msg.getBytes("gb2312"),0);
        PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(5,msg.getBytes("gb2312"),0);
        PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(6,bytes2,0);
        PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(7,bytes2,0);
        PD101Ctrl_RZC2.instanceDll.pd101a_rzc2_SendSingleColorText(7,bytes2,0);
        return "成功！";
    }
    private synchronized String callNumHttp(String ordinal, String windowName)throws Exception {
        String turnon = PropertiesUtil.getValue("test.whether.turn.on.httpClient");
        if(!turnon.equals("true")){
            return "成功！";
        }

        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("ordinal",ordinal);
        paramMap.put("windowName",windowName);
        String msg = null;
        String url = PropertiesUtil.getValue("httpClient.request.url");
        msg = HttpClient4.sendPost(url+"/callNumHttp",paramMap);
        if(msg.contains("失败")){
            return "失败！";
        }else {
            return "成功！";
        }
    }

}