package com.hjy.hall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (THallJiashizheng)实体类
 *
 * @author liuchun
 * @since 2020-07-27 14:17:46
 */
@Data
public class THallJiashizheng implements Serializable {
    private static final long serialVersionUID = -35077458231588864L;
    private String pkJiashiId;
    /**
     * 申请人
     */
    private String applicant;
    /**
     * 身份证件号
     */
    private String idcard;
    /**
     * 经办人
     */
    private String handlePeople;
    /**
     * 业务类型
     */
    private String businessType;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 退办类型
     */
    private String withdrawType;
    /**
     * 退办时间
     */
    private Object withdrawTime;
    /**
     * 缺少项
     */
    private String lack;
    /**
     * 其他缺少项
     */
    private String otherLack;
    /**
     * 备齐项
     */
//    private String ready;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 退办单号
     */
    private String associationNumber;
    /**
     * 排队号
     */
    private String ordinal;
    /**
     * 档案编号
     */
    private String fileNum;
    /**
     * 准驾车型
     */
    private String drivingModel;
    /**
     * 驾驶证办理业务类型
     */
    private String licenseBusiness;
    /**
     * 查询开始时间
     */
    private Date startTime;
    /**
     * 查询结束时间
     */
    private Date endTime;
    /**
     * 查询开始位置
     */
    private int startRow;
    /**
     * 查询结束位置
     */
    private int endRow;
}