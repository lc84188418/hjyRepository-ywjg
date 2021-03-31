package com.hjy.hall.dao;

import com.hjy.hall.entity.Statistics;
import com.hjy.hall.entity.THallQueue;
import com.hjy.hall.entity.THallQueueCount;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * (THallQueue)表数据库访问层
 *
 * @author liuchun
 * @since 2020-07-29 14:33:19
 */
public interface THallQueueMapper {

    /**
     * 通过ID查询单条数据
     */
    THallQueue selectById(String pkQueueId);

    /**
     * 新增数据
     */

    int insertSelective(THallQueue tHallQueue);

    /**
     * 修改数据
     */
    int updateById(THallQueue tHallQueue);

    /**
     * 通过主键删除数据
     */
    int deleteById(String pkQueueId);

    /**
     * 查询所有行数据
     */
    List<THallQueue> selectAll();
    //根据时间和号码查询特呼的排队信息
    THallQueue getByOrdinalAndDatestr(@Param("Ordinal") String Ordinal);

    List<THallQueue> queryByTime(@Param("startTime") String startTime,@Param("endTime") String endTime);
    //查询当前窗口现在正在办理的号码
    THallQueue getNowNumByWindowName(@Param("windowName") String remarks);
    //大厅管理-统计分析-受理人员工作量统计-总业务量
    List<THallQueueCount> totalCount(@Param("queryStart") String queryStart, @Param("queryEnd") String queryEnd);
    //大厅管理-统计分析-受理人员工作量统计-实际业务量
    List<THallQueueCount> realCount(@Param("queryStart") String queryStart, @Param("queryEnd") String queryEnd);
    //大厅管理-统计分析-受理人员工作量统计-空号统计
    List<THallQueueCount> nullCount(@Param("queryStart")String queryStart, @Param("queryEnd")String queryEnd);
    //大厅管理-统计分析-受理人员工作量统计-退办统计
    List<THallQueueCount> backCount(@Param("queryStart")String queryStart, @Param("queryEnd")String queryEnd);
    //大厅管理-统计分析-预警统计
    List<THallQueueCount> WarningCount(@Param("queryStart") String startTimeStr,  @Param("queryEnd")String endTimeStr, @Param("serviceOverTime")int serviceOverTime,@Param("waitOverTime") int waitOverTime);
    //当日窗口办结业务统计
    List<THallQueueCount> windowNumToday(@Param("queryStart") String startTimeStr,  @Param("queryEnd")String endTimeStr);
    //主页-各窗口的办理统计
    List<THallQueueCount> indexDataWindowNumToday();
    //主页-各业务类型的办理统计
    List<THallQueueCount> indexDataBusinessToday();
    //各办理人员业务量统计
    List<THallQueueCount> agentNumToday(@Param("queryStart") String startTimeStr,  @Param("queryEnd")String endTimeStr,@Param("serviceOverTime") int serviceOverTime);
    //查询今年办理次数
    int handleNum(@Param("idCard")String idCard);
    //查询今年代理次数
    int agentNum(@Param("idCard")String idCard);
    //顺序叫号时通过排队号获取排队信息表中距离当前时间最近的排队信息
    THallQueue getCallNum(@Param("ordinal")String ordinal);
    //分页+条件查询记录条数
    int selectSize(THallQueue tHallQueue);
    //分页+条件查询
    List<THallQueue> selectAllPage(int startRow, int endRow, @Param("ordinal")String ordinal,@Param("windowName") String windowName,@Param("agent") String agent,@Param("businessType") String businessType, @Param("remarks") String remarks,@Param("bName")String bName,@Param("bIdcard")String bIdcard, Date queryStart, Date queryEnd);
    //特呼号码是否已退号
    int vip_ordinalwhetherBackNumToday(@Param("vip_ordinal")String vip_ordinal);
    //主页-各窗口、各业务类型办理统计
    int selectStatisticsTodayGroupBy(@Param("businessType") String businessType,@Param("windowName") String windowName);
    //主页-最近办理业务情况统计
    List<THallQueue> selectNearlyToday(int endRow);
    //主页-各时段等候量统计
    int selectWaitStatisticsTodayDateSection(@Param("queryStart") String queryStart, @Param("queryEnd") String queryEnd);
    //主页-各时段处理量统计
    int selectHandStatisticsTodayDateSection(@Param("queryStart") String queryStart, @Param("queryEnd") String queryEnd);
    //主页-各办理人员业务量统计
    List<THallQueueCount> indexDataAgentNumToday(@Param("queryStart") String startTimeStr,  @Param("queryEnd")String endTimeStr,@Param("serviceOverTime") int serviceOverTime);
    //当日所有办理人员证件号
    List<THallQueue> selectAllIdCardOneday(THallQueue tHallQueue);
    //实际业务量统计-remarks=办结
    int statisticsNum(THallQueue tHallQueue);
    //当日某办理人员所办的根据业务类型统计
    List<Statistics> acceptancePeopleStatistics(THallQueue tHallQueue);
    //大厅管理-统计分析-预警统计-分页总记录条数
    int selectSizeWarningCount();
    //大厅管理-统计分析-预警统计-分页记录
    List<THallQueueCount> selectWarningCountAllPage(int startRow, int endRow, @Param("queryStart") String queryStart, @Param("queryEnd") String queryEnd,@Param("serviceOverTime")int serviceOverTime,@Param("waitOverTime") int waitOverTime);
    //查询单人单日办理业务空号数
    int selectNullNumToday(@Param("bIdCard")String bIdCard);
    //查询单人单日办理业务退办数
    int selectBackNumToday(@Param("bIdCard")String bIdCard);

    List<THallQueueCount> daobanData();
    //查询当日某业务类型未处理数量
    int selectWaitNumByBusiness(@Param("businessType") String businessType);
    //启动项目时删除之前未处理的数据
    void deteleBeforeData();
    //定时任务，处理未办结的业务
    void deteleNoHandBeforeData();

    //特呼的号码信息
    List<THallQueue> vip_ordinalQueue(String vip_ordinal);
    //当日各个业务类型等候量
    List<THallQueueCount> map9Data();
    //值日警官工作量统计
    List<THallQueueCount> dutyStatistics(THallQueue tHallQueue);

    void systemMaintain();
    //删除多余的重复数据
    String deleteDuplicateData();
    //3.1从排队信息去查受理人姓名
    String selectBNameByIdCard(String bIdCard);
    //3.2从排队信息去查代理人姓名
    String selectANameByIdCard(String bIdCard);
    //查询代理人代办信息
    List<THallQueue> selectAB_Card_date(@Param("aIdCard")String aIdCard);

    List<String> selectHanding();
    //当日某办理人员所办的业务类型
//    List<String> selectAllBusinessTypeOneday(@Param("idCard")String idCard,@Param("queryStart") String queryStart, @Param("queryEnd") String queryEnd);
}