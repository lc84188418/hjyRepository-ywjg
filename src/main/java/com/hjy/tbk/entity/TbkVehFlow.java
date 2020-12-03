package com.hjy.tbk.entity;

import lombok.Data;

/**
 * @author liuchun
 * @createDate 2020/11/25 11:49
 * @Classname TbkDrvFlow
 * @Description 机动车流水信息
 */
@Data
public class TbkVehFlow {
    /**
     * 流水号
     */
    private String lsh;
    /**
     * 序号
     */
    private String xh;
    /**
     * 业务类型
     */
    private String ywlx;
    /**
     * 业务原因
     */
    private String ywyy;
    /**
     * 所有人
     */
    private String syr;
    /**
     *号牌种类
     */
    private String hpzl;
    /**
     *号牌号码
     */
    private String hphm;
    /**
     *车辆品牌
     */
    private String clpp1;
    /**
     *车辆序号
     */
    private String clxh;
    /**
     *车辆类型
     */
    private String cllx;
    /**
     *管理部门
     */
    private String glbm;
    /**
     *车辆识别代码
     */
    private String clsbdh;
//    /**
//     *
//     */
//    private String xzqh;
//    private String xygw;
//    private String ywlc;
//    private String fpbj;
//    private String ffbj;
//    private String rkbj;
//    private String lszt;
//    private String ly;
//    private String ywlybz;
//    private String ycjsj;

}
