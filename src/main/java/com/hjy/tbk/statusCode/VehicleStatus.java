package com.hjy.tbk.statusCode;

/**
 * 机动车状态
 */
public enum VehicleStatus {
    Vehicle_STATUS_A("A", "正常"),
    Vehicle_STATUS_B("B", "转出"),
    Vehicle_STATUS_C("C", "被盗抢"),
    Vehicle_STATUS_D("D", "停驶"),
    Vehicle_STATUS_E("E", "注销"),
    Vehicle_STATUS_G("G", "违法未处理"),
    Vehicle_STATUS_H("H", "海关监管"),
    Vehicle_STATUS_I("I", "事故未处理"),
    Vehicle_STATUS_J("J", "嫌疑车"),
    Vehicle_STATUS_K("K", "查封"),
    Vehicle_STATUS_L("L", "扣留"),
    Vehicle_STATUS_M("M", "达到报废标准"),
    Vehicle_STATUS_N("N", "事故逃逸"),
    Vehicle_STATUS_O("O", "锁定"),
    Vehicle_STATUS_P("P", "达到报废标准公告牌作废"),
    Vehicle_STATUS_Q("Q", "逾期未检验");
    /**
     * status 状态
     */
    private String status;
    /**
     * desc 描述
     */
    private String desc;

    VehicleStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }
    // 普通方法
    public static String getDesc(String status) {
        for (VehicleStatus d : VehicleStatus.values()) {
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
