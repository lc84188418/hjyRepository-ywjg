package com.hjy.tbk.statusCode;

/**
 * 使用性质
 */
public enum SYXZStatus {
    STATUS_A("A", "非运营"),
    STATUS_B("B", "公路客运"),
    STATUS_C("C", "公交客运"),
    STATUS_D("D", "出租客运"),
    STATUS_E("E", "旅游客运"),
    STATUS_F("F", "货运"),
    STATUS_G("G", "租赁"),
    STATUS_H("H", "警用"),
    STATUS_I("I", "消防"),
    STATUS_J("J", "救护"),
    STATUS_K("K", "工程救险"),
    STATUS_L("L", "营转非"),
    STATUS_M("M", "出租转非"),
    STATUS_N("N", "教练"),
    STATUS_O("O", "幼儿校车"),
    STATUS_P("P", "小学生校车"),
    STATUS_Q("Q", "初中生校车"),
    STATUS_R("R", "危化品运输"),
    STATUS_S("S", "中小学生校车"),
    STATUS_T("T", "预约出租客运"),
    STATUS_U("U", "预约出租转非");
    /**
     * status 状态
     */
    private String status;
    /**
     * desc 描述
     */
    private String desc;

    SYXZStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }
    // 普通方法
    public static String getDesc(String status) {
        for (SYXZStatus d : SYXZStatus.values()) {
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