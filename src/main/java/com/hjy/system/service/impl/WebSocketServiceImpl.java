package com.hjy.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hjy.common.config.websocket.WebSocket;
import com.hjy.common.task.ObjectAsyncTask;
import com.hjy.common.utils.TokenUtil;
import com.hjy.common.utils.typeTransUtil;
import com.hjy.hall.dao.THallQueueMapper;
import com.hjy.hall.dao.THallTakenumberMapper;
import com.hjy.hall.entity.THallQueue;
import com.hjy.hall.entity.THallQueueCount;
import com.hjy.hall.entity.THallTakenumber;
import com.hjy.list.dao.TListAgentMapper;
import com.hjy.list.dao.TListInfoMapper;
import com.hjy.system.dao.TSysBusinesstypeMapper;
import com.hjy.system.dao.TSysParamMapper;
import com.hjy.system.dao.TSysTokenMapper;
import com.hjy.system.dao.TSysWindowMapper;
import com.hjy.system.entity.SysToken;
import com.hjy.system.entity.TSysParam;
import com.hjy.system.entity.TSysWindow;
import com.hjy.system.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WebSocketServiceImpl implements WebSocketService {
    @Autowired
    private TSysTokenMapper tokenMapper;
    @Autowired
    private THallQueueMapper tHallQueueMapper;
    @Autowired
    private THallTakenumberMapper tHallTakenumberMapper;
    @Autowired
    private TSysWindowMapper tSysWindowMapper;
    @Autowired
    private TSysBusinesstypeMapper businesstypeMapper;
    @Autowired
    private TSysParamMapper tSysParamMapper;
    @Autowired
    private WebSocket webSocket;
    @Override
    public void IndexData(HttpServletRequest request) throws IOException {
        JSONObject jsonObject = new JSONObject();
        /**
         * 一、顶部业务类型办理统计
         */
        List<THallQueueCount> queueCountList = ObjectAsyncTask.indexDataBusiness();
        jsonObject.put("map1",queueCountList);
        /**
         * 二、大厅实时等候人数
         */
        Map<String,Object> map = ObjectAsyncTask.indexDataWaitNum();
        jsonObject.put("map2",map);
        /**
         * 三、当日各时段等候量、处理量
         */
        Map<String,Object> map31 = new LinkedHashMap<>();
        Map<String,Object> map32 = new LinkedHashMap<>();
        //预警信息生成时间段 8-18
        String dateSection = tSysParamMapper.selectParamById("YJXXSCSJD");
        String []dateSectionList = dateSection.split("-");
        //开始时间
        int startDAteInt =Integer.parseInt(dateSectionList[0]);
        //结束时间
        int endDateInt =Integer.parseInt(dateSectionList[1]);
        Map<String,String> dateMap = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = new Date();
        String newDateStr = sdf.format(newDate);
        while (startDAteInt<=endDateInt){
            int queryEndInt = startDAteInt+1;
            String queryStartStr = newDateStr+" "+startDAteInt+":00:00";
            String queryEndStr = newDateStr+" "+queryEndInt+":00:00";
            dateMap.put(queryStartStr,queryEndStr);
            startDAteInt++;
        }
        //等候量-遍历map集合
        for (HashMap.Entry<String,String> ma : dateMap.entrySet() ){
            int count = tHallQueueMapper.selectWaitStatisticsTodayDateSection(ma.getKey(),ma.getValue());
            map31.put(ma.getKey(),count);
        }
        //处理量-遍历map集合
        for (HashMap.Entry<String,String> ma : dateMap.entrySet() ){
            int count = tHallQueueMapper.selectHandStatisticsTodayDateSection(ma.getKey(),ma.getValue());
            map32.put(ma.getKey(),count);
        }
        jsonObject.put("map31",map31);
        jsonObject.put("map32",map32);
        /**
         * 四、当日各窗口处理情况
         */
        List<THallQueueCount> queueCountList4 = ObjectAsyncTask.indexDataWindowNumToday();
        jsonObject.put("map4",queueCountList4);
        /**
         * 五、最近办理情况- 默认记录条数为10条
         * 再加一个超时时间戳
         */
        Map<String,Object> map5 = ObjectAsyncTask.indexNearlyToday();
        jsonObject.put("map5",map5);
        /**
         * 六、当日大厅业务统计
         */
        List<THallQueueCount> queueCountList6 = ObjectAsyncTask.indexDataAgentNumToday();
        jsonObject.put("map6",queueCountList6);
        /**
         * 七、预警所需参数值
         */
        List<TSysParam> params = ObjectAsyncTask.warningParam();
        jsonObject.put("map7",params);
        /**
         * 八、将等候人数发送到导办取号电脑
         */
        List<THallQueueCount> daobanList = tHallQueueMapper.daobanData();
        jsonObject.put("map8",daobanList);
        /**
         * 九、各个业务类型等候量
         */
        List<THallQueueCount> map9list = tHallQueueMapper.map9Data();
        jsonObject.put("map9",map9list);
        /**
         * 十、各窗口各个业务类型等候量
         */
        List<String> ips = webSocket.getAllIp();
        Map<String ,Object> map10 = new HashMap();
        for(String s:ips){
            //通过ip查询窗口
            TSysWindow window = tSysWindowMapper.selectWindowByIp(s);
            if(window !=null){
                String businesstypeList = window.getBusinessType();
                if(businesstypeList != null){
                    //获取当前窗口可办理的业务类型
                    //该窗口的各业务类型未处理数量
                    businesstypeList =  businesstypeList.replaceAll(" ","");
                    String[] strings = businesstypeList.split("/");
                    Map<String ,Object> businessMap = new HashMap();
                    for(String s2 :strings){
                        int num = tHallQueueMapper.selectWaitNumByBusiness(s2);
                        if(num != 0){
                            businessMap.put(s2,num);
                        }
                    }
                    //下一张号码
                    String nextOrdinal = this.getNextOrdinal(businesstypeList);
                    map10.put(s,nextOrdinal);
                }
            }
        }
        jsonObject.put("map10",map10);
        /**
         * 统计完成，发送消息给所有客户端
         */
        //给所有用户发送文本消息
        webSocket.sendAllMessage(jsonObject.toJSONString());
    }

    private String getNextOrdinal(String businessType) {
        //通过此工具类可以将该窗口可办理的业务类型转化为字母显示且按先后顺序的List集合
        List<String> typeList = null;
        try {
            typeList = typeTransUtil.typeTrans(businessType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String nextOrdinal = "";
        //mark为判断该窗口所办理的业务类型是否有号的标识
        int mark = 0;
        //通过foreach判断是否有号，如果typeList所包含的所有业务类型都在数据库中查询不到号码,则该窗口已无号可办,即typeList.size()= mark
        for (String typeSingle : typeList) {
            String takenumbers = tHallTakenumberMapper.getMinOrdinal(typeSingle);
            if (takenumbers != null) {
                nextOrdinal = takenumbers;
                break;
            } else {
                mark++;
            }
        }
        if (typeList.size() == mark) {
            return null;
        }
        return nextOrdinal;
    }

}
