package com.hjy.hall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hjy.system.entity.TSysBusinesstype;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * (THallQueue)实体类
 *
 * @author liuchun
 * @since 2020-07-29 14:33:19
 */
@Data
public class THallQueue implements Comparable<THallQueue> {
    private static final long serialVersionUID = 768936314850217156L;
    private String pkQueueId;
    /**
     * 排队号
     */
    private String ordinal;
    /**
     * 窗口
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
     * 业务类型
     */
    private String businessType;
    /**
     * 代理人证件类型
     */
    @JsonProperty
    private String aCertificatesType;
    /**
     * 代理人姓名
     */
    @JsonProperty
    private String aName;
    /**
     * 代理人证件号
     */
    @JsonProperty
    private String aIdcard;
    /**
     * 当事人证件类型
     */
    @JsonProperty
    private String bCertificatesType;
    /**
     * 当事人姓名
     */
    @JsonProperty
    private String bName;
    /**
     * 当事人证件号
     */
    @JsonProperty
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
     * 查询开始时间
     */
    private Date queryStart;
    /**
     * 查询结束时间
     */
    private Date queryEnd;
    /**
     * 备注是否为空号或者退办
     */
    private String remarks;
    /**
     * 办理次数
     */
    private Integer handleNum;
    /**
     * 前方等候人数
     */
    private Integer waitNum;
    /**
     * 代办次数
     */
    private Integer agentNum;
    /**
     * 是否为特呼，0是普通，1是特呼
     */
    private Integer isVip;
    /**
     * 流水号
     */
    private String serialNumber;
    /**
     * 导办人
     */
    private String daobanPeople;
    /**
     * 导办人证件号
     */
    private String daobanIdcard;
    /**
     * 服务评价
     */
    private String evaluate;

    @Override
    public int compareTo(THallQueue tHallQueue) {
        return this.getTime.compareTo(tHallQueue.getGetTime());
    }

}