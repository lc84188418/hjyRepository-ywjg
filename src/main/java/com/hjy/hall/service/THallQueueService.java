package com.hjy.hall.service;

import com.alibaba.fastjson.JSONObject;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.utils.page.PageResult;
import com.hjy.hall.entity.Statistics;
import com.hjy.hall.entity.THallQueue;
import com.hjy.hall.entity.THallQueueCount;
import com.hjy.system.entity.SysToken;
import com.hjy.system.entity.TSysWindow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.ConnectException;
import java.net.SocketException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * (THallQueue)表服务接口
 *
 * @author liuchun
 * @since 2020-07-29 14:33:20
 */
public interface THallQueueService {

    //通过ID查询单条数据
    THallQueue selectById(String pkQueueId);
    //新增数据
    int insert(THallQueue tHallQueue);
    //修改数据
    int updateById(THallQueue tHallQueue);
    //通过主键删除数据
    int deleteById(String pkQueueId);
    //查询所有数据
    List<THallQueue> selectAll();
    //查询当前窗口现在正在办理的业务
    THallQueue getNowNumByWindowName(String remarks);



    //导办取号
    Map<String, Object> getOrdinal(String param,HttpSession session) throws Exception;
    //叫号页面
    Map<String,Object> callPage(HttpServletRequest request);
    //顺序叫号
    CommonResult orderCall(HttpServletRequest request, HttpSession session) throws Exception;
    //设置空号
    Map<String, Object> nullNum(HttpServletRequest request,HttpSession session);
    //退号
    Map<String, Object> backNum(HttpServletRequest request,HttpSession session,String param);
    //办结
    Map<String ,Object> downNum(HttpServletRequest request,HttpSession session,String param);
    //特呼
    Map<String, Object> queueVipCall(HttpServletRequest request,HttpSession session,THallQueue tHallQueue);
    //重播
    Map<String, Object> repaly(HttpServletRequest request,String param);
    //获取同步库数据
    Map<String, Object> getTbkData(String param,HttpServletRequest request)throws SQLException, ConnectException;


    //大厅管理-统计分析-受理人员工作量统计
    List<Statistics> acceptancePeopleStatistics(String param) throws ParseException;
    //通过排队号获取排队信息表中距离当前时间最近的排队信息
    THallQueue getCallNum(String num);
    //排队信息查询
    PageResult selectAllPage(String param) throws ParseException;
    //大厅管理-统计分析-预警统计
    PageResult WarningCount(String param);
    //主页-各业务类型统计
    List<THallQueueCount> indexDataBusinessToday();
    //主页-各窗口统计
    List<THallQueueCount> indexDataWindowNumToday();
    //主页-最近办理业务统计
    List<THallQueue> selectNearlyToday(int i);
    //主页-各受理人员办理量统计
    List<THallQueueCount> indexDataAgentNumToday(String startStr, String endStr, int serviceOverTime);
    //删除之前未叫号的数据
    void deteleBeforeData();
    //定时任务，处理未办结的业务
    void deteleNoHandBeforeData();

    //统计分析-值日警官工作量统计
    List<THallQueueCount> dutyStatistics(String param)throws Exception;

}