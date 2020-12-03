package com.hjy.tbk.statusCode;

/**
 * 车辆类型
 */
public enum CLLXStatus {
    CLLXStatus_B11("B11", "重型栏板半挂车"),
    CLLXStatus_B12("B12", "重型箱式半挂车");
    /**
     * status 状态
     */
    private String status;
    /**
     * desc 描述
     */
    private String desc;

    CLLXStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }
    // 普通方法
    public static String getDesc(String status) {
        for (CLLXStatus d : CLLXStatus.values()) {
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
