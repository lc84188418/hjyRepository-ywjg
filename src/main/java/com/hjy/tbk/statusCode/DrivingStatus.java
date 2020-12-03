package com.hjy.tbk.statusCode;

/**
 * 驾驶证状态
 */
public enum DrivingStatus {

    STATUS_A("A", "正常"),
    STATUS_B("B", "超分"),
    STATUS_C("C", "转出"),
    STATUS_D("D", "暂扣"),
    STATUS_E("E", "撤销"),
    STATUS_F("F", "吊销"),
    STATUS_G("G", "注销"),
    STATUS_H("H", "违法未处理"),
    STATUS_I("I", "事故未处理"),
    STATUS_J("J", "停止使用"),
    STATUS_K("K", "扣押"),
    STATUS_L("L", "锁定"),
    STATUS_M("M", "逾期未换证"),
    STATUS_N("N", "延期换证"),
    STATUS_P("P", "延期体检"),
    STATUS_R("R", "注销可恢复"),
    STATUS_S("S", "逾期未审验"),
    STATUS_T("T", "延期审验"),
    STATUS_U("U", "扣留");
    /**
     * status 状态
     */
    private String status;
    /**
     * desc 描述
     */
    private String desc;

    DrivingStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }
    // 普通方法
    public static String getDesc(String status) {
        for (DrivingStatus d : DrivingStatus.values()) {
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
