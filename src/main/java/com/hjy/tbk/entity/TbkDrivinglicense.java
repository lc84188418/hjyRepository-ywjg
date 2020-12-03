package com.hjy.tbk.entity;

import lombok.Data;

import java.util.Date;

/**
 * 驾驶证
 */
@Data
public class TbkDrivinglicense {

    /**
     * 姓名
     */
    private String xm;
    /**
     * 证件号
     */
    private String sfzmhm;
    /**
     * 手机号码
     */
    private String sjhm;
    /**
     * 流水号
     * A
     */
    private String lsh;
    /**
     * 状态
     */
    private String zt;
    /**
     * 经办人
     */
    private String jbr;
    /**
     * 准驾车型
     */
    private String zjcx;
    /**
     * 领证日期
     */
    private Date cclzrq;
    /**
     * 有效期止
     */
    private Date yxqz;
    /**
     *审验有效期止
     */
    private Date syyxqz;
    /**
     * 档案编号
     */
    private String dabh;
    /**
     * 异常原因
     */
    private String exceptionReason;
}
