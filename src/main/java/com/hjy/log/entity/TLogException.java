package com.hjy.log.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (TLogException)实体类
 *
 * @author liuchun
 * @since 2020-10-15 09:47:47
 */
@Data
public class TLogException implements Serializable {
    private static final long serialVersionUID = 645736260212505746L;
    /**
     * 异常日志主键id
     */
    private String pkExcId;
    /**
     * 请求参数
     */
    private String excRequParam;
    /**
     * 异常名称
     */
    private String excName;
    /**
     * 异常信息
     */
    private String excMsg;
    /**
     * 操作员id
     */
    private String operUserId;
    /**
     * 操作员账户
     */
    private String operUserName;
    /**
     * 操作员姓名
     */
    private String operFullName;
    /**
     * 操作方法
     */
    private String operMethod;
    /**
     * 请求url
     */
    private String operUrl;
    /**
     * 请求ip
     */
    private String operIp;
    /**
     * 操作版本号
     */
    private String operVersion;
    /**
     * 异常日志创建时间
     */
    private Date excTime;
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