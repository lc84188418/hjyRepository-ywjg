package com.hjy.tbk.entity;

import lombok.Data;

/**
 * 机动车
 */
@Data
public class TbkVehicle {
    //同步库数据中存在的字段
    /**
     * 证件号
     */
    private String sfzmhm;
    /**
     * 证件类型,如：居民身份证，组织机构代码证
     */
    private String sfzmmc;
    /**
     * 准驾车型
     */
    private String zjcx;
    /**
     * 系统类别
     * meiyou
     * 机动车、驾驶证
     */
    private String stlb;
    /**
     * 业务流水号
     * B
     */
    private String lsh;
    /**
     * 异常类型
     */
    private String zt;
    /**
     * 异常原因
     */
    private String exceptionReason;
    /**
     * 所有人
     */
    private String syr;
    /**
     * 号牌种类
     */
    private String hpzl;
    /**
     * 号牌号码
     */
    private String hphm;
    /**
     * 车辆品牌
     */
    private String clpp1;
    /**
     * 车辆类型
     */
    private String cllx;
    /**
     * 车辆序号
     */
    private String clxh;
    /**
     * 使用性质
     */
    private String syxz;
    /**
     * 辆识别代号
     */
    private String clsbdh;
    /**
     * 管理部门
     */
    private String glbm;
    /**
     * 经办人
     * 这里的经办人是同步库里机动车业务的经办人还是本排队叫号系统里的？
     */
    private String jbr;
    /**
     * 档案编号
     */
    private String dabh;

}
