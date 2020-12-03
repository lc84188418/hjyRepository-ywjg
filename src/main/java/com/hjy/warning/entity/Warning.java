package com.hjy.warning.entity;

import com.hjy.hall.entity.THallQueue;
import com.hjy.tbk.entity.TbkDrivinglicense;
import com.hjy.tbk.entity.TbkVehicle;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Warning implements Comparable<Warning>{
    /**
     * 预警id
     */
    private String pkWarningId;
    /**
     * 预警类别
     * 取号预警、叫号预警、办理预警
     */
    private String warningCategory;
    /**
     * 预警分类
     *
     * 一、取号预警：
     * 二、叫号预警：4等待超时预警（60分）、1本人机动车异常、2本人驾驶证异常 、3注册/转移登记年龄超过限值（80）、
     *              7.代办次数超过限值、10.本人属于黑名单人员、9.代办非授权单位
     * 三、办理预警：5办理超时预警（15分）、6.退办预警（流水号还未录）
     * 四、空号：删除预警
     *
     * 单人单日办理业务空号数超过最大限值（8）、已完成
     * 单人单日办理业务退办数超过最大限值（10次）、流水号输入不正确、流水号未按时输入、代理人属于黑名单、
     * 同步库业务流水信息未在本系统找到对应办理记录
     *
     *未完成的
     * 不太清楚的-
     * 8.临牌核发超过次数
     *
     *
     */
    private String warningType;
    /**
     * 预警原因
     * 展示具体的预警分类详情，如等待超时预警
     * 预警原因：等待70分钟超时10分钟
     */
    private String warningReason;
    /**
     * 预警时间
     */
    private Date warningDate;
    /**
     * 预警流水号
     */
    private String warningSerial;
    //--------------------本系统
    /**
     * 导办人
     */
    private String daobanPeople;
    /**
     * 办理类型
     * 本人业务、代理业务
     */
    private String isAgent;
    /**
     * 办理业务类型
     */
    private String businessType;
    /**
     * 排队号码
     */
    private String ordinal;
    /**
     * 办理窗口
     */
    private String windowName;
    /**
     * 经办人
     */
    private String agent;
    /**
     * 经办人身份证
     */
    private String idCard;
    /**
     * 代理人证件类型
     */
    private String aCertificatesType;
    /**
     * 代理人姓名
     */
    private String aName;
    /**
     * 代理人证件号
     */
    private String aIdcard;
    /**
     * 当事人证件类型
     */
    private String bCertificatesType;
    /**
     * 当事人姓名
     */
    private String bName;
    /**
     * 当事人证件号
     */
    private String bIdcard;
    /**
     * 取号时间
     */
    private Date getTime;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 等待时长
     */
    private String waitTime;
    /**
     * 办理时长
     */
    private String handleTime;
    /**
     * 驾驶证关联流水号
     */
    private String serialNumberA;
    /**
     * 机动车关联流水号
     */
    private String serialNumberB;

    //---------------------------核查
    /**
     * 核查状态
     *待核查、已核查
     */
    private String checkStatus;
    /**
     * 核查结果
     * 正常、异常
     */
    private String checkResult;
    /**
     * 核查说明
     */
    private String checkDesc;
    /**
     * 核查时间
     */
    private Date checkDate;
    /**
     * 核查人
     */
    private String checkPeople;
    /**
     * 核查附件
     */
    private String checkFile;
    /**
     * 处理状态
     */
    private String handleStatus;
    /**
     *车主姓名
     */
    private String chezhuXm;
    /**
     *车主证件号
     */
    private String chezhuCard;
    /**
     * 号牌种类
     */
    private String hpzl;
    /**
     * 号牌号码
     */
    private String hphm;
    /**
     * meiyou
     * 使用性质
     */
    private String syxz;
    /**
     * 车架号/车辆识别代号
     */
    private String clsbdh;
    //---------------------同步库信息、主要有机动车和驾驶证信息
    //机动车
    private List<TbkVehicle> vehicleInfo;
    //驾驶证信息
    private List<TbkDrivinglicense> drivingLicenseInfo;
    /**
     * 预警返回信息
     */
    private String resultMsg;

    private int startRow;
    private int endRow;
    /**
     * 查询开始时间
     */
    private Date queryStart;
    /**
     * 查询结束时间
     */
    private Date queryEnd;

    @Override
    public int compareTo(Warning warning) {
        return this.warningDate.compareTo(warning.getWarningDate());
    }
}
