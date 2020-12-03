package com.hjy.synthetical.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (TSyntheticalRecord)实体类
 *
 * @author liuchun
 * @since 2020-10-30 17:44:37
 */
@Data
public class TSyntheticalRecord implements Serializable {
    private static final long serialVersionUID = -56419808157897971L;
    /**
     * 备案主键id
     */
    private String pkRecordId;
    /**
     * 备案类型
     */
    private String balx;
    /**
     * 机构类型
     */
    private String jglx;
    /**
     * 组织机构代码
     */
    private String zzjgdm;
    /**
     * 单位名称
     */
    private String dwMc;
    /**
     * 单位经办人
     */
    private String jbr;
    /**
     * 经办人联系电话
     */
    private String jbrTel;
    /**
     * 单位联系地址
     */
    private String dwDz;
    /**
     * 单位电话
     */
    private String dwTel;
    /**
     * 说明
     */
    private String basm;
    /**
     * 组织机构代码证路径
     */
    private String zzjgdmz;
    /**
     * 委托书路径
     */
    private String wts;
    /**
     * 备用1名称
     */
    private String by1Mc;
    /**
     * 备用1路径
     */
    private String by1Path;
    /**
     * 备用2名称
     */
    private String by2Mc;
    /**
     * 备用2路径
     */
    private String by2Path;
    /**
     * 备案时间
     */
    private Date baDate;
    /**
     * 查询开始位置
     */
    private int startRow;
    /**
     * 查询结束位置
     */
    private int endRow;
}