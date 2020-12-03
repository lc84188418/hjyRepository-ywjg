package com.hjy.tbk.statusCode;

/**
 * 业务类型
 * 同步库中没有该字段
 */
public enum YWLXStatus {
    YWLXStatus_A("A", "注册登记"),
    YWLXStatus_B("B", "转移登记"),
    YWLXStatus_D("D", "变更登记"),
    YWLXStatus_E("E", "抵押登记"),
    YWLXStatus_F("F", "停复驶业务"),
    YWLXStatus_G("G", "注销登记"),
    YWLXStatus_H("H", "二手车异地登记"),
    YWLXStatus_I("I", "转入业务"),
    YWLXStatus_J("J", "校车登记"),
    YWLXStatus_K("K", "补换牌证合格标志"),
    YWLXStatus_L("L", "补换登记证书"),
    YWLXStatus_M("M", "档案更正"),
    YWLXStatus_N("N", "被盗抢记录"),
    YWLXStatus_O("O", "临时号牌"),
    YWLXStatus_P("P", "核发检验标志"),
    YWLXStatus_Q("Q", "委托检验"),
    YWLXStatus_R("R", "受托检验"),
    YWLXStatus_S("S", "锁定业务"),
    YWLXStatus_T("T", "解除监管"),
    YWLXStatus_U("U", "转出、注销恢复"),
    YWLXStatus_V("V", "变更备案"),
    YWLXStatus_W("W", "电子档案补录"),
    YWLXStatus_X("X", "大吨小标更正");
    /**
     * status 状态
     */
    private String status;
    /**
     * desc 描述
     */
    private String desc;

    YWLXStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }
    // 普通方法
    public static String getDesc(String status) {
        for (YWLXStatus d : YWLXStatus.values()) {
            if (d.getStatus().equals(status)) {
                return d.desc;
            }
        }
        return null;
    }
    public String getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
