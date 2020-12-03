package com.hjy.tbk.statusCode;

/**
 * 身份证明标识
 */
public enum SFZMStatus {
    CLLXStatus_A("A", "居民身份证"),
    CLLXStatus_N("N", "组织机构代码证");
    /**
     * status 状态
     */
    private String status;
    /**
     * desc 描述
     */
    private String desc;

    SFZMStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }
    // 普通方法
    public static String getDesc(String status) {
        for (SFZMStatus d : SFZMStatus.values()) {
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
