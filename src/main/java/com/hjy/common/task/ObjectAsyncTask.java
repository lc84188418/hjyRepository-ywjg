package com.hjy.common.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.config.websocket.WebSocket;
import com.hjy.common.utils.*;
import com.hjy.common.utils.Http.HttpClient4;
import com.hjy.common.utils.file.CardFileUtil;
import com.hjy.common.utils.file.SmbFileUtil;
import com.hjy.hall.entity.THallQueue;
import com.hjy.hall.entity.THallQueueCount;
import com.hjy.hall.entity.THallTakenumber;
import com.hjy.hall.service.THallJiashizhengService;
import com.hjy.hall.service.THallJidongcheService;
import com.hjy.hall.service.THallQueueService;
import com.hjy.hall.service.THallTakenumberService;
import com.hjy.list.entity.TListAgent;
import com.hjy.list.entity.TListInfo;
import com.hjy.list.service.TListAgentService;
import com.hjy.list.service.TListInfoService;
import com.hjy.synthetical.entity.TSyntheticalMakecard;
import com.hjy.synthetical.service.TSyntheticalMakecardService;
import com.hjy.synthetical.service.TSyntheticalRecordService;
import com.hjy.system.entity.ReUserRole;
import com.hjy.system.entity.TSysParam;
import com.hjy.system.entity.TSysWindow;
import com.hjy.system.service.TSysParamService;
import com.hjy.system.service.TSysRoleService;
import com.hjy.system.service.TSysUserService;
import com.hjy.tbk.entity.TbkDrivinglicense;
import com.hjy.tbk.entity.TbkVehicle;
import com.hjy.tbk.service.TbkDrivinglicenseService;
import com.hjy.tbk.service.TbkVehicleService;
import com.hjy.tbk.statusCode.DrivingStatus;
import com.hjy.tbk.statusCode.VehicleStatus;
import com.hjy.warning.entity.Warning;
import com.hjy.warning.service.TWarnLnfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hjy.common.utils.led.util.sendMsg;

@Component
@Async
public class ObjectAsyncTask {
    @Autowired
    private THallTakenumberService tHallTakenumberService;
    @Autowired
    private THallQueueService tHallQueueService;
    @Autowired
    private THallJidongcheService tHallJidongcheService;
    @Autowired
    private THallJiashizhengService tHallJiashizhengService;
    @Autowired
    private TListAgentService tListAgentService;
    @Autowired
    private TListInfoService tListInfoService;
    @Autowired
    private TSysParamService tSysParamService;
    @Autowired
    private TSyntheticalMakecardService tHallMakecardService;
    @Autowired
    private TSyntheticalRecordService tSyntheticalRecordService;
    @Autowired
    private TSysUserService tSysUserService;
    @Autowired
    private TSysRoleService tSysRoleService;
    @Autowired
    private WebSocket webSocket;
    @Autowired
    private TbkVehicleService tbkVehicleService;
    @Autowired
    private TbkDrivinglicenseService tbkDrivinglicenseService;
    @Autowired
    private TWarnLnfoService warningService;

    private static ObjectAsyncTask ntClient;

    /**
     * 一、顶部业务类型办理统计
     *
     */
    public static List<THallQueueCount> indexDataBusiness(){
        List<THallQueueCount> queueCountList = ntClient.tHallQueueService.indexDataBusinessToday();
        return queueCountList;
    }
    /**
     * 二、大厅实时等候人数
     */
    public static Map<String,Object> indexDataWaitNum(){
        Map<String,Object> map = new HashMap<>();
        int num = ntClient.tHallTakenumberService.indexDataWaitNum();
        map.put("waitNum",num);
        return map;
    }
    /**
     * 四、当日各窗口处理情况
     */
    public static List<THallQueueCount> indexDataWindowNumToday(){
        List<THallQueueCount> queueCountList = ntClient.tHallQueueService.indexDataWindowNumToday();
        if(queueCountList != null){
            //按窗口名称排序
            Collections.sort(queueCountList, new Comparator<THallQueueCount>() {
                @Override
                public int compare(THallQueueCount o1, THallQueueCount o2) {
                    //升序
                    return Integer.valueOf(o1.getWindowName().replace("号窗口","")).compareTo(Integer.valueOf(o2.getWindowName().replace("号窗口","")));
                }
            });
            List<THallQueueCount> queueCountList2 = queueCountList;
            return queueCountList2;
        }
        return queueCountList;
    }
    /**
     * 五、最近办理情况- 默认记录条数为10条
     */
    public static Map<String,Object> indexNearlyToday(){
        Map<String,Object> map = new HashMap<>();
        String value = ntClient.tSysParamService.selectParamById("ZJBLQKJLTS");
        int endRow = 10;
        if(value!=null){
            endRow = Integer.parseInt(value);
        }
        List<THallQueue> queueList= ntClient.tHallQueueService.selectNearlyToday(endRow+1);
        map.put("nearly",queueList);
        //超时限值:单位秒
        String overTime = ntClient.tSysParamService.selectParamById("PTCJGYWCS");
        int serviceOverTime = 900;
        if(overTime != null){
            serviceOverTime = Integer.parseInt(overTime);
        }
        map.put("overTime",serviceOverTime);
        return map;
    }
    /**
     * 六、当日大厅业务统计
     */
    public static List<THallQueueCount> indexDataAgentNumToday(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String startStr=sdf.format(new Date());
        String endStr=sdf.format(new Date());
        String value = ntClient.tSysParamService.selectParamById("PTCJGYWCS");
        int serviceOverTime=900;
        if(value!=null){
            serviceOverTime=Integer.parseInt(value);
        }
        List<THallQueueCount> queueCountList6 = ntClient.tHallQueueService.indexDataAgentNumToday(startStr,endStr,serviceOverTime);
        return queueCountList6;
    }
    /**
     * 七、预警所需参数值
     */
    public static List<TSysParam> warningParam(){
        List<TSysParam> params = ntClient.tSysParamService.selectWarningParam();
        return params;
    }

    public static void addUserRoleByUserRole(String pkUserId, String roleId) {
        ReUserRole userRole = new ReUserRole();
        userRole.setPk_userRole_id(IDUtils.getUUID());
        userRole.setFk_user_id(pkUserId);
        userRole.setFk_role_id(roleId);
        ntClient.tSysUserService.addUserRoleByUserRole(userRole);
    }

    public static void deleteRolePermsByRoleId(String fk_role_id) {
        ntClient.tSysRoleService.deleteRolePermsByRoleId(fk_role_id);
    }
    //添加角色默认的权限-即主页的3个
    public static void addDefultRoelPerms(String fk_role_id) {
        List<String> idList = new ArrayList<>();
        idList.add("1596706636946");
        idList.add("1596706882298");
        idList.add("1596707062416");
        ntClient.tSysRoleService.distributeMenu(fk_role_id,idList);
    }

    //更新取号表状态
    public static void updateTakeNumberFlag(String ordinal,int flag){
        THallTakenumber tHallTakenumber = ntClient.tHallTakenumberService.getByOrdinal(ordinal);
        if(tHallTakenumber != null){
            tHallTakenumber.setFlag(flag);
            ntClient.tHallTakenumberService.updateById(tHallTakenumber);
        }else {
            //添加一个
            THallTakenumber insertEntity = new THallTakenumber();
            insertEntity.setPkTakenumId(IDUtils.getUUID());
            insertEntity.setGetTime(new Date());
            insertEntity.setFlag(flag);
            insertEntity.setOrdinal(ordinal);
            ntClient.tHallTakenumberService.insertSelective(insertEntity);
        }

    }
    //异步处理-特呼写入
    public static String vipcallNumberHttp(String ordinal,String windowName){
        String turnon = PropertiesUtil.getValue("test.whether.turn.on.httpClient");
        if(!turnon.equals("true")){
            return "成功";
        }

        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("ordinal",ordinal);
        paramMap.put("windowName",windowName);
        String msg = null;
        try {
            String url = PropertiesUtil.getValue("httpClient.request.url");
            msg = HttpClient4.sendPost(url+"/vipcallNumberHttp",paramMap);
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    //异步处理-办结删除led文件
    public static String downNumberHttp(String ordinal) {
        String turnon = PropertiesUtil.getValue("test.whether.turn.on.httpClient");
        if(!turnon.equals("true")){
            return "成功";
        }

        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("ordinal",ordinal);
        String msg = null;
        try {
            String url = PropertiesUtil.getValue("httpClient.request.url");
            msg = HttpClient4.sendPost(url+"/downNumberHttp",paramMap);
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();

        }
    }
    //异步处理-维护led文件
    public static String systemMaintain() {
        String turnon = PropertiesUtil.getValue("test.whether.turn.on.httpClient");
        if(!turnon.equals("true")){
            return "成功";
        }

        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("ordinal",null);
        String msg = null;
        try {
            String url = PropertiesUtil.getValue("httpClient.request.url");
            msg = HttpClient4.sendPost(url+"/systemMaintain",paramMap);
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    //异步处理更新排队信息表
    public static THallQueue updateQueue(String ordinal,String windowName,String agent,String idCard){
        THallQueue queueUpdate = ntClient.tHallQueueService.getCallNum(ordinal);
        queueUpdate.setStartTime(new Date());
        queueUpdate.setWindowName(windowName);
        queueUpdate.setAgent(agent);
        queueUpdate.setIdCard(idCard);
        queueUpdate.setRemarks(windowName+"正在办理");
        ntClient.tHallQueueService.updateById(queueUpdate);
        return queueUpdate;
    }
    //办结业务
    public static int updateQueueByEntity(THallQueue nowQueue) {
        return ntClient.tHallQueueService.updateById(nowQueue);
    }
    //录入代办信息
    public static int insertListAgent(TListAgent agent){
        return ntClient.tListAgentService.insert(agent);
    }
    //代办次数过多添加黑名单
    public static StringBuffer insertBlackList(String aName, String aIdcard,int agentNum,StringBuffer msgBuffer){
        TListInfo redList = ntClient.tListInfoService.selectByIdCard(aIdcard);
        if(redList != null){
            return msgBuffer;
        }
        String tSysParam = ntClient.tSysParamService.selectParamById("JRHMDDBCSXZ");
        int maxNum = 5;
        if(tSysParam != null){
            maxNum = Integer.parseInt(tSysParam);
        }
        if (agentNum >= maxNum) {
            TListInfo blackList = new TListInfo();
            blackList.setListType("黑名单");
            blackList.setFullName(aName);
            blackList.setIdCard(aIdcard);
            blackList.setExplain("代办次数过多");
            blackList.setReason("代办次数过多");
            blackList.setWhetherPass("通过");
            blackList.setApprovalPeople("系统添加");
            blackList.setOperator("系统");
            blackList.setCreateTime(new Date());
            blackList.setApprovalTime(new Date());
            int i = ntClient.tListInfoService.insert(blackList);
            if(i > 0){
                msgBuffer.append("录入黑名单成功！");
            }else {
                msgBuffer.append("录入黑名单失败！");
            }
        }
        return msgBuffer;
    }
    //制证添加-直接改为制作中
    public static int insertMakeCard(THallQueue nowQueue) {
        TSyntheticalMakecard tHallMakecard=new TSyntheticalMakecard();
        tHallMakecard.setPkCardId(IDUtils.getUUID());
        if(nowQueue.getAIdcard()!= null){
            tHallMakecard.setAIdcard(nowQueue.getAIdcard());
            String aname = nowQueue.getAName();
            if(aname == null){
                aname = "XXX";
            }
            tHallMakecard.setAName(aname);
            tHallMakecard.setACertificatesType(nowQueue.getACertificatesType());
        }
        tHallMakecard.setBIdcard(nowQueue.getBIdcard());
        String bname = nowQueue.getBName();
        if(bname == null){
            bname = "XXX";
        }
        tHallMakecard.setBName(bname);
        tHallMakecard.setBCertificatesType(nowQueue.getBCertificatesType());
        tHallMakecard.setCreateTime(new Date());
        tHallMakecard.setStartTime(new Date());
        tHallMakecard.setStatus("制作中");
        return ntClient.tHallMakecardService.insert(tHallMakecard);
    }
    //异步处理-修改制证共享文件夹
    public static void updateMakeCardShareFile(TSyntheticalMakecard tHallMakecard) {
        String flag = CardFileUtil.MakeCardShareFileComplet(tHallMakecard);
        if(flag != null){
            String whether = PropertiesUtil.getValue("test.whether.update.share.file");
            if(whether.equals("true")){
                //本地文件添加完成后上传到共享文件
                String shareDir = PropertiesUtil.getValue("share.file.directory");
                String localFilePath = "d://hjy//ywjg//makeCard//左边.txt";
                try {
                    SmbFileUtil.smbPut(shareDir,localFilePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
//    //异步处理-删除制证共享文件夹
//    public static String deleteMakeCardShareFile(TSyntheticalMakecard tHallMakecard) {
//        //删除制证共享文件中该制证信息的内容
//        CardFileUtil.MakeCardShareFileDel(tHallMakecard);
//        String whether = PropertiesUtil.getValue("test.whether.update.share.file");
//        if(whether.equals("true")){
//            //本地文件添加完成后上传到共享文件
//            String shareDir = PropertiesUtil.getValue("share.file.directory");
//            String localFilePath = "d://hjy//ywjg//makeCard//左边.txt";
//            try {
//                SmbFileUtil.smbPut(shareDir,localFilePath);
//                return "制证文件修改成功";
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException();
//            }
//        }
//        return "制证文件修改成功";
//    }
    //异步处理-添加排队信息
    public static void insertQueue(THallQueue queueUpdate){
        ntClient.tHallQueueService.insert(queueUpdate);
    }
    //顺序叫号预警
    public static Warning warning347910(THallQueue queue,StringBuffer resultMsgBuffer,TListInfo infoB) {
        //如果排队叫号类型为咨询查询、满分恢复业务，就不做预警
        if("业务咨询查询".equals(queue.getBusinessType()) || "满分、恢复驾驶资格考试预约、取消预约".equals(queue.getBusinessType())){
            return null;
        }
        StringBuffer warningTypeBuffer = new StringBuffer();
        StringBuffer reasonBuffer = new StringBuffer();
        //生成预警信息
        Warning warning = new Warning();
        String aIdCard = queue.getAIdcard();
        String bIdCard = queue.getBIdcard();
        resultMsgBuffer.append("叫号预警："+queue.getOrdinal()+"     ");
        /**
         * 预警-3.注册/转移登记年龄超过限值（80）
         * 还需要改动，目前是所有业务都有年龄限制
         */
        //验证是否符合身份证规范
        if (IdCardUtil.isValidatedAllIdcard(bIdCard)){
            String tSysParam = ntClient.tSysParamService.selectParamById("ZCZYDJNLXZ");
            int maxAge = 80;
            if(tSysParam != null){
                maxAge = Integer.parseInt(tSysParam);
            }
            //获取身份证上的年月日
            String dates = bIdCard.substring(6, 10) + "-" + bIdCard.substring(10, 12) + "-" + bIdCard.substring(12, 14);
            Date nowDate = new Date();
            //获取当前时间
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date birthDate = null;
            try {
                birthDate = df.parse(dates);
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
            //格式化出生日期
            long diff = nowDate.getTime() - birthDate.getTime();
            long ages = diff / (1000 * 60 * 60 * 24) / 365;
            //获取用户年龄
            if(ages>=maxAge){
                //生成预警信息
                //预警分类
                //如果同时还有等待超时预警/机动车/驾驶证异常，就在后面追加
                warningTypeBuffer.append("年龄超限预警");
                //预警原因
                String msg = String.valueOf(maxAge);
                reasonBuffer.append("年龄超过限值("+msg+")-"+String.valueOf(ages-maxAge)+"岁");
                //预警返回信息
                resultMsgBuffer.append("-年龄超过限值预警");
            }
        }

        /**
         * 预警-4.等待超时预警
         */
        String tSysParam = ntClient.tSysParamService.selectParamById("DDCSSJXZ");
        long maxNum = 3600;
        if(tSysParam != null){
            maxNum = Long.parseLong(tSysParam);
        }
        //等待时长  单位秒
        Long waitTime = (queue.getStartTime().getTime()-queue.getGetTime().getTime())/1000;
        if(waitTime>=maxNum){
            //如果同时还有机动车/驾驶证异常，就在后面追加
            //预警分类
            if(!StringUtils.isEmpty(warningTypeBuffer.toString())){
                warningTypeBuffer.append("/等待超时预警");
            }else {
                warningTypeBuffer.append("等待超时预警");
            }
            //预警原因
            String waitTimeStr =DateUtil.translateLong(waitTime);
            if(!StringUtils.isEmpty(reasonBuffer.toString())){
                reasonBuffer.append("/"+"等待超时"+waitTimeStr);
            }else {
                reasonBuffer.append("等待超时"+waitTimeStr);
            }
            //预警返回信息
            resultMsgBuffer.append("-等待超时预警");
        }

        /**
         * 预警10.本人黑名单预警
         */
        if(infoB != null){
            if("黑名单".equals(infoB.getListType())){
                resultMsgBuffer.append("-本人黑名单！");
            }
        }

        if(aIdCard != null){
            TListInfo infoA = ntClient.tListInfoService.selectByIdCard(aIdCard);
            if(!(infoA != null && "红名单".equals(infoA.getListType()))){
                /**
                 * 预警-7.代办次数过多预警
                 */
                //查询参数配置中加入黑名单代办次数限值，如果没有就默认为5次
                String value = ntClient.tSysParamService.selectParamById("JRHMDDBCSXZ");
                int max = 5;
                if(value != null){
                    max = Integer.parseInt(value);
                }
                int warnNum = max - 1;
                if(queue.getAgentNum() >= warnNum){
                    String warningStr = String.valueOf(warnNum);
                    //预警分类
                    if(!StringUtils.isEmpty(warningTypeBuffer.toString())){
                        warningTypeBuffer.append("/代办次数超过限制");
                    }else {
                        warningTypeBuffer.append("代办次数超过限制");
                    }
                    //预警原因
                    if(!StringUtils.isEmpty(reasonBuffer.toString())){
                        reasonBuffer.append("/"+"代办人：-"+queue.getAName()+" 代理次数已达"+warningStr+"次！");
                    }else {
                        reasonBuffer.append("代办人：-"+queue.getAName()+" 代理次数已达"+warningStr+"次！");
                    }
                    //预警返回信息
                    resultMsgBuffer.append("-"+queue.getAName()+" 代理次数已达"+warningStr+"次！");
                }

            }else if(infoA != null && "红名单".equals(infoA.getListType())) {
                /**
                 * 预警-9.代办非授权单位
                 */
                if(infoA.getOrganizationCode() != null){
                    //红名单的人和他所代理的单位是否相匹配
                    if(!infoA.getOrganizationCode().equals(queue.getBIdcard())){
                        //不匹配时，看是否能代理所有业务，即listScope=1,如果不能则预警listScope=0
                        if(infoA.getListScope() == 0){
                            //预警分类
                            if(!StringUtils.isEmpty(warningTypeBuffer.toString())){
                                warningTypeBuffer.append("/代办非授权单位");
                            }else {
                                warningTypeBuffer.append("代办非授权单位");
                            }
                            //预警原因
                            if(!StringUtils.isEmpty(reasonBuffer.toString())){
                                reasonBuffer.append("/"+infoA.getOrganizationCode()+"业务代理"+queue.getBIdcard()+"业务");
                            }else {
                                reasonBuffer.append(infoA.getOrganizationCode()+"业务代理"+queue.getBIdcard()+"业务");
                            }
                            //说明该单位还未有备案记录
                            resultMsgBuffer.append("-"+"代办非授权单位");
                        }
                    }
                }
            }
        }

        if(!StringUtils.isEmpty(warningTypeBuffer.toString())){
            warning.setPkWarningId(UUID.randomUUID().toString());
            //预警类别
            warning.setWarningCategory("叫号预警");
            warning.setWarningType(warningTypeBuffer.toString());
            //预警原因-防止超过数据库最大字符长度
            warning.setWarningReason(StrBufferUtil.handTooLengthStrBuffer(reasonBuffer.toString()));
            warning.setWarningDate(new Date());
            warning.setDaobanPeople(queue.getDaobanPeople());
            warning.setBusinessType(queue.getBusinessType());
            warning.setOrdinal(queue.getOrdinal());
            warning.setWindowName(queue.getWindowName());
            //本人信息
            warning.setBName(queue.getBName());
            warning.setBIdcard(queue.getBIdcard());
            warning.setBCertificatesType(queue.getBCertificatesType());
            //代理人信息
            if(queue.getAIdcard() != null){
                warning.setAName(queue.getAName());
                warning.setAIdcard(queue.getAIdcard());
                warning.setACertificatesType(queue.getACertificatesType());
                warning.setIsAgent("代理业务");
            }else {
                warning.setIsAgent("本人业务");
            }
            warning.setAgent(queue.getAgent());
            warning.setIdCard(queue.getIdCard());
            warning.setGetTime(queue.getGetTime());
            warning.setStartTime(queue.getStartTime());
            warning.setHandleStatus(queue.getWindowName()+"正在办理");
            //预警信息
            ObjectAsyncTask.insertWarning(warning);
            return warning;
        }else {
            resultMsgBuffer = null;
            return null;
        }

    }

    //办理超时预警
    public static String warning5(THallQueue nowQueue, Warning warning0) {
        if(warning0 != null){
            //先办结原有的预警
            warning0.setWarningSerial(nowQueue.getSerialNumber());
            warning0.setEndTime(nowQueue.getEndTime());
            warning0.setHandleStatus("办结");
            ntClient.warningService.update(warning0);
        }
        String value = ntClient.tSysParamService.selectParamById("PTCJGYWCS");
        long max = 900;
        if(value != null){
            max = Long.parseLong(value);
        }
        Long handleTime = (nowQueue.getEndTime().getTime()-nowQueue.getStartTime().getTime())/1000;
        if(handleTime >= max){
            StringBuffer resultMsgBuffer = new StringBuffer();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowDateStr = simpleDateFormat.format(new Date());
            resultMsgBuffer.append(nowDateStr+"办理预警："+nowQueue.getOrdinal()+"-办理超时预警!");
            String handletime = DateUtil.translateLong(handleTime-max);
            //超时，判断是否已存在预警信息
            if(warning0 != null){
                //在原有的预警基础上添加信息
                warning0.setPkWarningId(UUID.randomUUID().toString());
                warning0.setWarningCategory("办理预警");
                warning0.setWarningType("办理超时预警");
                warning0.setWarningReason("办理超时"+handletime);
                warning0.setWarningDate(new Date());
                warning0.setWarningSerial(nowQueue.getSerialNumber());
                warning0.setEndTime(nowQueue.getEndTime());
                warning0.setCheckStatus("");
                warning0.setHandleStatus("办结");
                warning0.setResultMsg(resultMsgBuffer.toString());
                ntClient.warningService.insert(warning0);
            }else {
                //不存在就重新录入
                Warning warning1 = new Warning();
                warning1.setPkWarningId(UUID.randomUUID().toString());
                warning1.setWarningCategory("办理预警");
                warning1.setWarningType("办理超时预警");
                warning1.setWarningReason("办理超时"+handletime);
                warning1.setWarningDate(new Date());
                warning1.setWarningSerial(nowQueue.getSerialNumber());
                warning1.setDaobanPeople(nowQueue.getDaobanPeople());
                warning1.setOrdinal(nowQueue.getOrdinal());
                //本人信息
                warning1.setBName(nowQueue.getBName());
                warning1.setBIdcard(nowQueue.getBIdcard());
                warning1.setBCertificatesType(nowQueue.getBCertificatesType());
                //代理人信息
                if(nowQueue.getAIdcard() != null){
                    warning1.setAName(nowQueue.getAName());
                    warning1.setAIdcard(nowQueue.getAIdcard());
                    warning1.setACertificatesType(nowQueue.getACertificatesType());
                    warning1.setIsAgent("代理业务");
                }else {
                    warning1.setIsAgent("本人业务");
                }
                warning1.setAgent(nowQueue.getAgent());
                warning1.setIdCard(nowQueue.getIdCard());
                warning1.setGetTime(nowQueue.getGetTime());
                warning1.setStartTime(nowQueue.getStartTime());
                warning1.setResultMsg(resultMsgBuffer.toString());
                ObjectAsyncTask.insertWarning(warning1);
            }
            //将预警信息发送到所有窗口
            JSONObject warningJson = new JSONObject();
            warningJson.put("warning",resultMsgBuffer.toString());
            ntClient.webSocket.sendWarningMessage(warningJson.toJSONString());
            return resultMsgBuffer.toString();
        }else {
            //没有超时直接返回
            return null;
        }

    }

    public static void updateWarning(Warning warning) {
        ntClient.warningService.update(warning);
    }

    public static void insertWarning(Warning warning) {
        ntClient.warningService.insert(warning);
    }

    //预警-空号删除预警
    public static void warningDel(THallQueue nowQueue) {
        Warning warning = ntClient.warningService.selectByWindow(nowQueue.getWindowName()+"正在办理");
        if(warning != null){
            ntClient.warningService.deleteById(warning);
        }
    }
    //预警-退号预警
    public static Warning warning6(THallQueue nowQueue,String warningSerial,String param) {
        //先删除原有的预警
        ObjectAsyncTask.warningDel(nowQueue);
        //重新生成退办预警
        JSONObject jsonObject = JSON.parseObject(param);
        String withdrawType = JsonUtil.getStringParam(jsonObject,"withdrawType");
        //录入退办预警
        Warning warning1 = new Warning();
        warning1.setPkWarningId(UUID.randomUUID().toString());
        warning1.setWarningSerial(warningSerial);
        warning1.setWarningCategory("办理预警");
        warning1.setWarningType("退办预警");
        warning1.setWarningReason(withdrawType);
        warning1.setWarningDate(new Date());
        warning1.setDaobanPeople(nowQueue.getDaobanPeople());
        //本人信息
        warning1.setBName(nowQueue.getBName());
        warning1.setBIdcard(nowQueue.getBIdcard());
        warning1.setBCertificatesType(nowQueue.getBCertificatesType());
        //代理人信息
        if(nowQueue.getAIdcard() != null){
            warning1.setAName(nowQueue.getAName());
            warning1.setAIdcard(nowQueue.getAIdcard());
            warning1.setACertificatesType(nowQueue.getACertificatesType());
            warning1.setIsAgent("代理业务");
        }else {
            warning1.setIsAgent("本人业务");
        }
        warning1.setBusinessType(nowQueue.getBusinessType());
        warning1.setOrdinal(nowQueue.getOrdinal());
        warning1.setWindowName(nowQueue.getWindowName());
        warning1.setEndTime(nowQueue.getEndTime());
        warning1.setSerialNumberA(nowQueue.getSerialNumber());
        warning1.setHandleStatus("办结");
        warning1.setAgent(nowQueue.getAgent());
        warning1.setIdCard(nowQueue.getIdCard());
        warning1.setGetTime(nowQueue.getGetTime());
        warning1.setStartTime(nowQueue.getStartTime());
        StringBuffer resultMsgBuffer = new StringBuffer();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDateStr = simpleDateFormat.format(new Date());
        resultMsgBuffer.append(nowDateStr+"办理预警："+nowQueue.getOrdinal()+"-退办预警!");
        warning1.setResultMsg(resultMsgBuffer.toString());
        ObjectAsyncTask.insertWarning(warning1);
        return warning1;
    }

    public static int warningUpdate(Warning warning) {
        return ntClient.warningService.update(warning);
    }

    //本人机动车和驾驶证预警
    public static Warning warning12_1(THallQueue queue,List<TbkVehicle> brclList,List<TbkDrivinglicense> brjszList) {
        //如果排队叫号类型为咨询查询、满分恢复业务，就不做预警
        if("业务咨询查询".equals(queue.getBusinessType()) || "满分、恢复驾驶资格考试预约、取消预约".equals(queue.getBusinessType())){
            return null;
        }
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
        //生成预警信息
        Warning warning = new Warning();
        StringBuffer warningTypeBuffer = new StringBuffer();
        StringBuffer reasonBuffer = new StringBuffer();
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
        resultMsgBuffer.append(nowDateStr+"叫号预警："+queue.getOrdinal());
        /**
         * 预警-1.本人机动车异常
         */
        List<TbkVehicle> result = new ArrayList<>();
        if(brclList != null && brclList.size() != 0){
            boolean flag =true;
            for(TbkVehicle obj:brclList){
                String status = obj.getZt();
                if(status != null){
                    if(!status.contains("A")){
                        while (flag){
                            //预警分类
                            warningTypeBuffer.append("本人机动车异常");
                            //预警返回信息
                            resultMsgBuffer.append(" 本人机动车异常");
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
                            statuStringBuffer.append(j+"."+msg);
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
                        result.add(obj);
                    }
                }
            }
            warning.setVehicleInfo(result);
        }

        /**
         * 预警-2.本人驾驶证异常
         */
//        List<TbkDrivinglicense> result2 = new ArrayList<>();
//        if(brjszList != null){
//            boolean flag =true;
//            for(TbkDrivinglicense obj:brjszList){
//                String status = obj.getZt();
//                if(status != null){
//                    if(!status.contains("A")){
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
//                            statuStringBuffer.append(j+"."+msg);
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
//                        result2.add(obj);
//                    }
//                }
//            }
//            warning.setDrivingLicenseInfo(result2);
//        }
        if(!StringUtils.isEmpty(warningTypeBuffer.toString())){
            warning.setPkWarningId(IDUtils.getUUID());
            //预警类别
            warning.setWarningCategory("叫号预警");
            warning.setWarningDate(new Date());
            warning.setDaobanPeople(queue.getDaobanPeople());
            warning.setBusinessType(queue.getBusinessType());
            warning.setOrdinal(queue.getOrdinal());
            warning.setWindowName(queue.getWindowName());
            //本人信息
            warning.setBName(queue.getBName());
            warning.setBIdcard(queue.getBIdcard());
            warning.setBCertificatesType(queue.getBCertificatesType());
            //代理人信息
            if(queue.getAIdcard() != null){
                warning.setAName(queue.getAName());
                warning.setAIdcard(queue.getAIdcard());
                warning.setACertificatesType(queue.getACertificatesType());
                warning.setIsAgent("代理业务");
            }else {
                warning.setIsAgent("本人业务");
            }
            warning.setAgent(queue.getAgent());
            warning.setIdCard(queue.getIdCard());
            warning.setGetTime(queue.getGetTime());
            warning.setStartTime(queue.getStartTime());
            warning.setHandleStatus(queue.getWindowName()+"正在办理");
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
        }else {
            return null;
        }
    }

    /**
     * 异步处理-窗口led屏显示信息换为几号窗口
     */
    public static String ledHttp(String kzkId, String msg) throws Exception {
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

    public static void callNumSendMsg(String ordinal, TSysWindow window) throws Exception {
        /**
         * 不通过前端websocket接收消息播放
         */
        //调用LED控制卡发送消息到屏幕上
        String msg = "请"+ordinal+"号到"+window.getWindowName();
        String kzkId = window.getControlCard();
        /**
         * led屏窗口信息+语音播放
         */
        String turnon = PropertiesUtil.getValue("test.whether.turn.on.ledHttpClient");
        if(turnon.equals("true")){
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("kzk",kzkId);
            paramMap.put("msg",msg);
            String url = PropertiesUtil.getValue("httpClient.led.url");
            msg = HttpClient4.sendPost(url+"/ledHttp",paramMap);
        }

    }

    //初始化所有服务
    @PostConstruct
    public void init() {
        ntClient = this;
        ntClient.tHallTakenumberService = this.tHallTakenumberService;
        ntClient.tHallQueueService = this.tHallQueueService;
        ntClient.tHallJidongcheService = this.tHallJidongcheService;
        ntClient.tHallJiashizhengService = this.tHallJiashizhengService;
        ntClient.tHallMakecardService = this.tHallMakecardService;
        ntClient.tSyntheticalRecordService = this.tSyntheticalRecordService;
        ntClient.tListAgentService = this.tListAgentService;
        ntClient.tListInfoService = this.tListInfoService;
        ntClient.tSysParamService = this.tSysParamService;
        ntClient.tSysUserService = this.tSysUserService;
        ntClient.tSysRoleService = this.tSysRoleService;
        ntClient.webSocket = this.webSocket;
        ntClient.tbkVehicleService = this.tbkVehicleService;
        ntClient.tbkDrivinglicenseService = this.tbkDrivinglicenseService;
        ntClient.warningService = this.warningService;
    }
}
