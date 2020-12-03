package com.hjy.hall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * (THallJidongche)实体类
 *
 * @author liuchun
 * @since 2020-07-27 15:51:25
 */
@Data
public class THallJidongche implements Serializable {
    private static final long serialVersionUID = 406215436233562884L;
    private String pkJidongcheId;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 退办类型
     */
    private String withdrawType;
    /**
     * 申请人
     */
    private String applicant;
    /**
     * 申请人证件号
     */
    private String idcard;

    /**
     * 业务类型
     */
    private String businessType;
    /**
     * 号牌种类
     */
    private String numberType;
    /**
     * 号牌号码
     */
    private String numberPlate;
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
     * 经办人
     */
    private String handlePeople;
    /**
     * 退办时间
     */
    private Date withdrawTime;
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
     * 车辆识别代码
     */
    private String identifyCode;
    /**
     * 机动车业务
     */
    private String carBusiness;
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