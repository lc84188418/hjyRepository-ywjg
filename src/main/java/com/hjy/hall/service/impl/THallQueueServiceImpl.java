package com.hjy.hall.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.config.websocket.WebSocket;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.task.ObjectAsyncTask;
import com.hjy.common.utils.Http.HttpClient4;
import com.hjy.common.utils.*;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * (THallQueue)表服务实现类
 *
 * @author liuchun
 * @since 2020-07-29 14:33:20
 */
@Slf4j
@Service
public class THallQueueServiceImpl implements THallQueueService {

    private static String ERROR = "error";
    private static String SUCCESS = "success";
    private Lock lock = new ReentrantLock();
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
    public Map<String ,Object> downNum(HttpServletRequest request,HttpSession session,String param) throws Exception{
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
            map.put("status", ERROR);
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
        map.put("code", 200);
        map.put("status", SUCCESS);
        map.put("msg", msgBuffer.toString());
        map.put("kzkId", window.getControlCard());
        map.put("ordinal", ordinal);
        return map;
    }

    //退号
    @Transactional()
    @Override
    public Map<String ,Object> backNum(HttpServletRequest request,HttpSession session,String param) throws Exception{
        Map<String,Object> map = new HashMap<>();
        StringBuffer msgBuffer = new StringBuffer();
        //从token中拿到当前窗口号
        String tokenStr = TokenUtil.getRequestToken(request);
        SysToken token = tSysTokenMapper.findByToken(tokenStr);
        String ip = token.getIp();
        TSysWindow window = tSysWindowMapper.selectByIp(ip);
        String windowName = window.getWindowName();
        //查询当前窗口正在办理的业务
        THallQueue nowQueue = tHallQueueMapper.getNowNumByWindowName(windowName+"正在办理");
        if (nowQueue == null) {
            map.put("code", 445);
            map.put("status", ERROR);
            map.put("msg", "该窗口暂无号，退办失败！");
            return map;
        }
        nowQueue.setRemarks("退号");
        nowQueue.setEndTime(new Date());
        int i = tHallQueueMapper.updateById(nowQueue);
        if(i > 0){
            map.put("code", 200);
            map.put("status", SUCCESS);
            map.put("kzkId", window.getControlCard());
            map.put("ordinal", nowQueue.getOrdinal());
            msgBuffer.append("退办成功！");
        }else {
            map.put("code", 444);
            map.put("status", ERROR);
            msgBuffer.append("退办失败！");
        }
        //更新取号表状态flag
        String ordinal = nowQueue.getOrdinal();
        ObjectAsyncTask.updateTakeNumberFlag(ordinal,2);
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
            map.put("status", ERROR);
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
    public Map<String, Object> nullNum(HttpServletRequest request,HttpSession session) throws Exception{
        Map<String, Object> map = new HashMap<>();
        //从token中拿到当前窗口号
        String tokenStr = TokenUtil.getRequestToken(request);
        SysToken token = tSysTokenMapper.findByToken(tokenStr);
        String ip = token.getIp();
        TSysWindow window = tSysWindowMapper.selectByIp(ip);
        String windowName = window.getWindowName();
        //查询当前窗口正在办理的业务
        THallQueue nowQueue = tHallQueueMapper.getNowNumByWindowName(windowName+"正在办理");
        if (nowQueue == null) {
            map.put("code", 445);
            map.put("status", ERROR);
            map.put("msg", "该窗口暂无号，设置空号失败！");
            return map;
        }
        nowQueue.setRemarks("空号");
        nowQueue.setEndTime(new Date());
        int i = this.updateById(nowQueue);
        //更新取号表状态flag
        String ordinal = nowQueue.getOrdinal();
        ObjectAsyncTask.updateTakeNumberFlag(ordinal,2);
        //预警处理
        ObjectAsyncTask.warningDel(nowQueue);
        map.put("code", 200);
        map.put("status", SUCCESS);
        map.put("msg", "设置空号成功！");
        map.put("kzkId", window.getControlCard());
        map.put("ordinal", ordinal);
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
        if(!StringUtils.isEmpty(DDYWBLRSBZXZ)){
            max1 = Integer.parseInt(DDYWBLRSBZXZ);
        }
        String xianzhi1 = String.valueOf(max1);
        if(waitAllNum >= max1){
            map.put("code", 445);
            map.put("status", ERROR);
            map.put("msg", "大厅待办理业务量已达限值("+xianzhi1+")，不予取号，请等待");
            return map;
        }
        JSONObject jsonObject = JSON.parseObject(param);
        String bIdCard = JsonUtil.getStringParam(jsonObject,"bIdCard");
        if(StringUtils.isEmpty(bIdCard)){
            map.put("code", 446);
            map.put("status", ERROR);
            map.put("msg", "受理人证件号为空");
            return map;
        }else {
            int byteLength = bIdCard.getBytes().length;
            if(byteLength > 36){
                log.info("证件号已超过36字节");
                map.put("code", 447);
                map.put("status", ERROR);
                map.put("msg", "证件号已超过36字节，请确认是否正确，或联系开发人员修改");
                return map;
            }
        }
        /**
         * 二、单人单日办理业务空号数超过最大限值（8）超过就不予取号,如果没有就默认为8次
         */
        //查询参数配置中单人单日办理业务空号数超过最大限值（8）超过就不予取号
        String DRDRZDKHS = tSysParamMapper.selectParamById("DRDRZDKHS");
        int max2 = 8;
        if(DRDRZDKHS != null){
            try {
                max2 = Integer.parseInt(DRDRZDKHS);
            }catch (Exception e){
                max2 = 8;
            }
        }
        String xianzhi2 = String.valueOf(max2);
        //查询单人单日办理业务空号数
        int nullNum = tHallQueueMapper.selectNullNumToday(bIdCard);
        if(nullNum >= max2){
            map.put("code", 448);
            map.put("status", ERROR);
            map.put("msg", "单人单日办理业务空号数已达限值("+xianzhi2+")，不予取号，请明天再来！");
            return map;
        }
        /**
         * 三、单人单日办理业务退办数超过最大限值（10次）超过就不予取号
         */
        String DRDRZDTBS = tSysParamMapper.selectParamById("DRDRZDTBS");
        int max3 = 10;
        if(DRDRZDTBS != null){
            try {
                max3 = Integer.parseInt(DRDRZDTBS);
            }catch (Exception e){
                max3 = 10;
            }
        }
        String xianzhi3 = String.valueOf(max3);
        //查询单人单日办理业务退办数
        int backNum = tHallQueueMapper.selectBackNumToday(bIdCard);
        if(backNum >= max3){
            map.put("code", 449);
            map.put("status", ERROR);
            map.put("msg", "单人单日办理业务退办数已达限值("+xianzhi3+")，不予取号，请明天再来！");
            return map;
        }
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        if(activeUser == null){
            map.put("code", 450);
            map.put("status", ERROR);
            map.put("msg", "操作用户session已消失，请登录后重试！");
            return map;
        }
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
            map.put("status", ERROR);
            map.put("msg", "该ip暂无匹配的窗口，请配置后重试!");
            return map;
        }
        String windowName = window.getWindowName();
        resultJson.put("windowName", windowName);
        //获取当前窗口可办理的业务类型
        String businesstypeList = window.getBusinessType();
        if(businesstypeList == null){
            map.put("code",446);
            map.put("status", ERROR);
            map.put("msg", "该窗口暂未配置办理业务类型，请配置后重试!");
            return map;
        }
        //查询当前窗口是否存在正在办理的业务
        THallQueue tHallQueue = tHallQueueMapper.getNowNumByWindowName(windowName+"正在办理");
        if(tHallQueue != null) {
            resultJson = this.getResultJson(tHallQueue);
            map.put("code",201);
            map.put("status", SUCCESS);
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
                map.put("status", ERROR);
                map.put("msg", "该窗口已暂停服务！");
            }else {
                map.put("code",200);
                map.put("status", SUCCESS);
                map.put("msg", "获取数据成功!");
            }
            return map;
        }
    }
    //顺序叫号
    @Transactional()
    @Override
    public synchronized CommonResult orderCall(HttpServletRequest request, HttpSession session) throws Exception{
        int code = 200;
        StringBuffer stringBuffer = new StringBuffer();
        //从token中拿到当前窗口信息
        String tokenStr = TokenUtil.getRequestToken(request);
        SysToken token = tSysTokenMapper.selectIpAndName(tokenStr);
        if(token == null){
            return new CommonResult(445, "error", "用户信息已失效，请重新登录后再试!", null);
        }
        TSysWindow window = tSysWindowMapper.selectByIp(token.getIp());
        if(window == null){
            return new CommonResult(446, "error", "该ip暂无窗口信息，请联系维护人员!", null);
        }
        //查询当前窗口是否存在正在办理的业务
        THallQueue resultQueue = new THallQueue();
        THallQueue tHallQueue = tHallQueueMapper.getNowNumByWindowName(window.getWindowName()+"正在办理");
        if(tHallQueue != null){
            resultQueue = tHallQueue;
            code = 447;
            stringBuffer.append("该窗口还有未办结业务，请先处理!");
        }else {
            //判断该窗口服务是否开启中
            Integer serviceStatus = window.getServiceStatus();
            if(serviceStatus != null && serviceStatus == 0){
                return new CommonResult(448, "error", "该窗口已暂停服务！", serviceStatus);
            }
            //业务处理逻辑
            ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
            String agent = token.getFullName();
            String idCard = null;
            if(activeUser != null ){
                idCard=activeUser.getIDcard();
            }else {
                log.info("顺序叫号时ActiveUser信息已失效!");
                return new CommonResult(445, "error", "用户信息已失效，请重新登录后再试!", null);
            }
            /**
             * 改为线程同步
             */
            Map<String,Object> sxjhMap = new HashMap<>();
            sxjhMap = this.synchronizedCallNum(window,agent,idCard);
            String status = (String) sxjhMap.get("status");
            if("error".equals(status)){
                return new CommonResult(451, "error", (String) sxjhMap.get("msg"), null);
            }else {
                resultQueue = (THallQueue) sxjhMap.get("resultQueue");
                stringBuffer.append(resultQueue.getWindowName()+":叫号成功！"+resultQueue.getOrdinal());
            }
        }
        if(resultQueue != null){
            JSONObject resultJson = new JSONObject();
            resultJson = this.getResultJson(resultQueue);
            //将windowName、ordinal、kzkId传回前端，用于单独访问led控制接口
            resultJson.put("windowName",resultQueue.getWindowName());
            resultJson.put("ordinal",resultQueue.getOrdinal());
            resultJson.put("kzkId",window.getControlCard());
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
            return new CommonResult(code, "success", stringBuffer.toString(), resultJson);
        }else {
            return new CommonResult(452, "error", "该窗口已无号", null);
        }
    }

    /**
     * synchronized
     * @param window
     * @param agent
     * @param idCard
     * @return
     */
    private  Map<String, Object> synchronizedCallNum(TSysWindow window, String agent, String idCard) {
        //异步处理更新排队信息表
        Map<String,Object> sxjhMap = new HashMap<>();
        //通过此工具类可以将该窗口可办理的业务类型转化为字母显示且已经排序的List集合[B,C]
        List<String> typeList = null;
        //如[B,C]
        typeList = typeTransUtil.typeTrans(window.getBusinessType());
        //*****判断该窗口所办理的业务类型是否有号
        String ordinal = "";
        //mark为判断该窗口所办理的业务类型是否有号的标识
        int mark = 0;
        /**
         * 后面这一段就是变更操作数据的
         */
        // 获取锁对象
        lock.lock();
        try{
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
                sxjhMap.put("code",449);
                sxjhMap.put("status","error");
                sxjhMap.put("msg","该窗口"+mark+"种业务均已无号！");
                return sxjhMap;
            }
            //异步处理取号表的flag标识
            int i = ObjectAsyncTask.updateTakeNumberFlag(ordinal,1);
            if(i < 0){
                log.info("顺序叫号时修改取号表的flag标识失败！");
                sxjhMap.put("code",450);
                sxjhMap.put("status","error");
                sxjhMap.put("msg","顺序叫号时修改取号表的flag标识失败！");
                return sxjhMap;
            }
            sxjhMap = ObjectAsyncTask.updateQueue(ordinal,window.getWindowName(),agent,idCard);
            return sxjhMap;
        }catch (Exception e){
            throw new RuntimeException("顺序叫号出差！");
        }finally {
            // 释放锁对象
            lock.unlock();
        }

        /**
         * 这是原来的方式
         */
//        //通过foreach判断是否有号，如果typeList所包含的所有业务类型都在数据库中查询不到号码,则该窗口已无号可办,即typeList.size()= mark
//        for (String typeSingle : typeList) {
//            String takenumbers = tHallTakenumberMapper.getMinOrdinal(typeSingle);
//            if (takenumbers != null) {
//                ordinal = takenumbers;
//                break;
//            } else {
//                mark++;
//            }
//        }
//        if (typeList.size() == mark) {
//            sxjhMap.put("code",449);
//            sxjhMap.put("status","error");
//            sxjhMap.put("msg","该窗口"+mark+"种业务均已无号！");
//            return sxjhMap;
//        }
//        //异步处理取号表的flag标识
//        int i = ObjectAsyncTask.updateTakeNumberFlag(ordinal,1);
//        if(i < 0){
//            log.info("顺序叫号时修改取号表的flag标识失败！");
//            sxjhMap.put("code",450);
//            sxjhMap.put("status","error");
//            sxjhMap.put("msg","顺序叫号时修改取号表的flag标识失败！");
//            return sxjhMap;
//        }
//        sxjhMap = ObjectAsyncTask.updateQueue(ordinal,window.getWindowName(),agent,idCard);
//        return sxjhMap;
    }

    /**
     * 顺序叫号成功后访问led大厅接口
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public CommonResult led(String param) throws Exception {
        JSONObject json = JSON.parseObject(param);
        String windowName = JsonUtil.getStringParam(json,"windowName");
        String ordinal = JsonUtil.getStringParam(json,"ordinal");
        String kzkId = JsonUtil.getStringParam(json,"kzkId");
        if(!StringUtils.isEmpty(windowName) && !StringUtils.isEmpty(ordinal)){
            //led大屏
//            this.callNumHttp(ordinal,windowName);
        }else {
            log.info("未传入窗口名和排队号");
            return new CommonResult(446, "error", "未传入窗口名和排队号", null);
        }
        if(!StringUtils.isEmpty(kzkId) && !StringUtils.isEmpty(ordinal)){
            //led小屏
            this.ledHttp(kzkId,"请"+ordinal+"号办理");
        }else {
            log.info("未传入控制卡和排队号");
            return new CommonResult(445, "error", "未传入控制卡和排队号", null);
        }

        return new CommonResult(200,"success","led屏更新成功!!",null);
    }
    /**
     * 顺序叫号成功后访问led窗口屏接口
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public CommonResult smallLed(String param) throws Exception {
        JSONObject json = JSON.parseObject(param);
        String ordinal = JsonUtil.getStringParam(json,"ordinal");
        String kzkId = JsonUtil.getStringParam(json,"kzkId");
        if(!StringUtils.isEmpty(kzkId) && !StringUtils.isEmpty(ordinal)){
            //led小屏
            this.ledHttp(kzkId,"请"+ordinal+"号办理");
        }else {
            log.info("未传入控制卡和排队号");
            return new CommonResult(445, "error", "未传入控制卡和排队号", null);
        }
        return new CommonResult(200,"success","led小屏更新成功!",null);
    }

    /**
     * 叫号成功后访问led大屏接口
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public CommonResult bigLed(String param) throws Exception {
        List<String> list = tHallQueueMapper.selectHanding();
        this.callNumHttp(list);
        return new CommonResult(200,"success","led大屏更新成功!",null);
    }

    /**
     *业务办理完结后访问，办结、空号、退号
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public CommonResult complete(String param) throws Exception {
        JSONObject json = JSON.parseObject(param);
        String ordinal = JsonUtil.getStringParam(json,"ordinal");
        String kzkId = JsonUtil.getStringParam(json,"kzkId");
        //led大屏
        String otherMsg = ObjectAsyncTask.downNumberHttp(ordinal);
        //led小屏
        this.ledHttp(kzkId,"号窗口");
        return new CommonResult(200,"success","led屏更新成功!",null);
    }
    @Override
    public CommonResult completeSmallLed(String param) throws Exception {
        JSONObject json = JSON.parseObject(param);
        String kzkId = JsonUtil.getStringParam(json,"kzkId");
        //led小屏
        this.ledHttp(kzkId,"号窗口");
        return new CommonResult(200,"success","led小屏更新成功!",null);
    }
    @Override
    public CommonResult completeBigLed(String param) throws Exception {
        JSONObject json = JSON.parseObject(param);
        String ordinal = JsonUtil.getStringParam(json,"ordinal");
        //led大屏
        String otherMsg = ObjectAsyncTask.downNumberHttp(ordinal);
        return new CommonResult(200,"success","led大屏更新成功!",null);
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
        if(token == null){
            map.put("code",444);
            map.put("status", ERROR);
            map.put("msg", "用户信息已失效，请重新登录后再试!");
            return map;
        }
        String ip = token.getIp();
        TSysWindow window = tSysWindowMapper.selectByIp(ip);
        if(window == null){
            map.put("code",445);
            map.put("status", ERROR);
            map.put("msg", "该ip暂无窗口信息，请联系维护人员!");
            return map;
        }
        String windowName = window.getWindowName();
        //判断是否存在输入的号码
        String vip_ordinal = tHallQueue.getOrdinal().toUpperCase();
        if (tHallTakenumberMapper.wetherExist(vip_ordinal) == 0) {
            map.put("code",446);
            map.put("status", ERROR);
            map.put("msg", "特呼的号码不存在!");
            return map;
        }
        List<THallQueue> vipQueues = new ArrayList<>();
        //查询当前窗口是否存在正在办理的业务
        THallQueue handingtHallQueue = tHallQueueMapper.getNowNumByWindowName(windowName+"正在办理");
        if(handingtHallQueue != null){
            map.put("code",447);
            map.put("status", ERROR);
            map.put("msg", "该窗口还有未办结业务，请先处理!");
            resultJson = this.getResultJson(handingtHallQueue);
            map.put("data", resultJson);
            return map;
        }else {
            vipQueues = tHallQueueMapper.vip_ordinalQueue(vip_ordinal);
        }
        if(vipQueues.size() == 0){
            map.put("code",448);
            map.put("status", ERROR);
            //删除取号表有号，但排队信息中无号的取号信息
            String shanchuStr = tHallTakenumberService.deleteByOrdinal(vip_ordinal);
            map.put("msg", "该号码在排队信息中未有记录，已删除，请重新排号");
            return map;
        }
        THallQueue queueVip = vipQueues.get(0);
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
                map.put("code",449);
                map.put("status", ERROR);
                map.put("msg", "特呼的号码已退号!");
                return map;
            }
        }else if(vipQueues.size() >= 2){
            //如果该号码已被多次办理，查询是否存在被退号的信息，如果有被退号，则之后也不能进行特呼
            for(THallQueue entity:vipQueues){
                if("退号".equals(entity.getRemarks())){
                    map.put("code",450);
                    map.put("status", ERROR);
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
//        ObjectAsyncTask.vipcallNumberHttp(vip_ordinal,windowName);
        /**
         * 线程同步发送叫号信息
         */
        String callNumMsg = this.callNumSendMsg(vip_ordinal,window);
        resultJson = this.getResultJson(queueVip);
        //将windowName、ordinal、kzkId传回前端，用于单独访问led控制接口
        resultJson.put("windowName",windowName);
        resultJson.put("ordinal",queueVip.getOrdinal());
        resultJson.put("kzkId",window.getControlCard());
        map.put("code",200);
        map.put("status", SUCCESS);
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
        String windowName = String.valueOf(jsonObject.get("windowName"));
        TSysWindow window = tSysWindowMapper.selectWindowByName(windowName);
        //异步呼叫
        String callNumMsg = this.callNumSendMsg(ordinal,window);
        map.put("code", 200);
        map.put("status", SUCCESS);
        map.put("msg", "请"+ordinal+"到"+window.getWindowName());
        return map;
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
        if(StringUtils.isEmpty(isAgent)){
            map.put("code", 451);
            map.put("status", ERROR);
            map.put("msg", "未传入是否为代理业务！");
            return map;
        }
        /**
         * 开始取号
         */
        String sign = "A";
        if(!StringUtils.isEmpty(businessType)){
            //1拿到该业务类型的标识，如果没有传入就默认为普通车驾管业务，标识为A
            sign = businesstypeMapper.selectTypeLevelByTypeName(businessType);
        }else {
            businessType = "普通车驾管业务";
        }
        //2取得最大的号码
        String ordinal = this.getMaxTackNumber(sign);
        if("该号已达最大数量999".equals(ordinal)){
            log.info(sign+"号已达最大数量999！");
            map.put("code", 452);
            map.put("status", ERROR);
            map.put("msg", "该号已达最大数量999！");
            map.put("data", null);
            return map;
        }
        //创建排队叫号实体
        THallQueue tHallQueue = new THallQueue();
        String a_bIdCard = bIdCard;
        boolean sfcxg = false;
        int agentNum = 0;
        //本人信息
        tHallQueue.setBIdcard(bIdCard);
        tHallQueue.setBusinessType(businessType);
        tHallQueue.setBCertificatesType(bCertificatesType);
        //代理业务isAgent = 0
        if("0".equals(isAgent)) {
            String aIdCard = JsonUtil.getStringParam(jsonObject,"aIdCard");
            String aName = JsonUtil.getStringParam(jsonObject,"aName");
            String aCertificatesType = JsonUtil.getStringParam(jsonObject,"aCertificatesType");
            if(StringUtils.isEmpty(aCertificatesType)){
                aCertificatesType = "居民身份证";
            }
            if(StringUtils.isEmpty(aIdCard)){
                log.info("代理业务需传入代理人证件号!");
                map.put("code", 453);
                map.put("status", ERROR);
                map.put("msg", "代理业务需传入代理人证件号");
                map.put("data", null);
                return map;
            }
            a_bIdCard = aIdCard;
            agentNum = tHallQueueMapper.agentNum(a_bIdCard);
            sfcxg = true;
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
                    try {
                        max = Integer.parseInt(value);
                    }catch (Exception e){
                        max = 5;
                    }
                }
                if (agentNum >= max) {
                    map.put("code", 453);
                    map.put("status", ERROR);
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
                    //4直接指定默认名字
                    bName = "未知";
                    if(StringUtils.isEmpty(bName)){
                        bName = "未知";
                        flag =false;
                        break;
                    }
                    //5从同步库去查
                }
                flag =false;
                break;
            }
        }
        //查询今年办理次数
        int handleNum = tHallQueueMapper.handleNum(bIdCard);
        if(!sfcxg){
            //查询今年代理次数
            agentNum = tHallQueueMapper.agentNum(bIdCard);
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
        int i = tHallTakenumberMapper.insertSelective(takenumber);
        if(i != 1){
            log.info("录入takeNumber信息失败!"+takenumber);
            map.put("code", 454);
            map.put("status", ERROR);
            map.put("msg", "录入取号信息失败，取号失败!");
            return map;
        }
        //查询前方等候人数
        int waitNum = tHallTakenumberMapper.selectWaitNum(ordinal);
        tHallQueue.setGetTime(nowdate);
        tHallQueue.setOrdinal(ordinal);
        tHallQueue.setIsVip(0);
        tHallQueue.setPkQueueId(pk_id);
        tHallQueue.setWaitNum(waitNum);
        tHallQueue.setDaobanPeople(activeUser.getFullName());
        tHallQueue.setDaobanIdcard(activeUser.getIDcard());
        //异步录入排队信息
        int j = ObjectAsyncTask.insertQueue(tHallQueue);
        if(j != 1){
            log.info("录入tHallQueue信息失败!"+tHallQueue);
            map.put("code", 455);
            map.put("status", ERROR);
            map.put("msg", "录入排队信息失败，取号失败!");
            return map;
        }
        //
        map.put("code", 200);
        map.put("status", SUCCESS);
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
            //删除当日前的数据
            tHallTakenumberMapper.deteleBeforeData();
            tHallQueueMapper.deteleBeforeData();
            warningService.systemMaintain();
            return sign+"001";
        }else {
            int ordinal_num = 0;
            //去掉业务类型标识 ,num3 = 010
            String num3 = maxNum.substring(1,maxNum.length());
            if("999".equals(num3)){
                return "该号已达最大数量999";
            }
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
    //synchronized-通过前端websocket接收消息播放有
    public synchronized String callNumSendMsg(String ordinal,TSysWindow window) throws Exception{
        /**
         * 通过前端websocket接收消息播放
         */
        JSONObject json = new JSONObject();
        String sendTextMessage = "请"+ordinal+"到"+window.getWindowName();
        json.put("call",sendTextMessage);
        webSocket.sendTextMessageTo(json.toJSONString());
        return "成功！";
    }

    private String ledHttp(String kzkId, String msg) throws Exception {
        String turnon = PropertiesUtil.getValue("test.whether.turn.on.ledHttpClient");
        if(!turnon.equals("true")){
            return "成功！";
        }
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("kzk",kzkId);
        paramMap.put("msg",msg);
        String url = PropertiesUtil.getValue("httpClient.led.url");
        msg = HttpClient4.sendPost(url+"/ledHttp",paramMap);
        if(msg.contains("失败")){
            return "失败！";
        }else {
            return "成功！";
        }
    }

    private synchronized String callNumHttp(List<String> list)throws Exception {
        String turnon = PropertiesUtil.getValue("test.whether.turn.on.httpClient");
        if(!turnon.equals("true")){
            return "成功！";
        }

        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("list",list);
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