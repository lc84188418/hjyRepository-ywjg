package com.hjy.tbk.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author liuchun
 * @createDate 2020/11/25 11:49
 * @Classname TbkDrvFlow
 * @Description 驾驶证流水信息
 */
@Data
public class TbkDrvFlow {
    /**
     * 流水号
     */
    private String lsh;
    /**
     * 证件号
     */
    private String sfzmhm;
    /**
     * 档案编号
     */
    private String dabh;
    /**
     * 姓名
     */
    private String xm;
    /**
     * 业务类型
     */
    private String ywlx;
    /**
     * 业务原因
     */
    private String ywyy;
    /**
     *准驾车型
     */
    private String zjcx;
    /**
     *业务状态
     */
    private String ywzt;
    /**
     *管理部门
     */
    private String glbm;

//    private String ywgw;
//    private String kskm;
//    private String xygw;
//    private String ffbz;
//    private String rkbz;
//    private String hdbz;
//    private String xgzl;
//    private String ywblbm;
//    private String fzjg;
//    private String dcbj;
//    private String gxsj;
}
