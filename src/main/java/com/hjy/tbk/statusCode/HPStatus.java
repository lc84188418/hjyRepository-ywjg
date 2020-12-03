package com.hjy.tbk.statusCode;

/**
 * 号牌种类
 */
public enum HPStatus {
    STATUS_01("01", "大型汽车"),
    STATUS_02("02", "小型汽车"),
    STATUS_03("03", "使馆汽车"),
    STATUS_04("04", "领馆汽车"),
    STATUS_05("05", "境外汽车"),
    STATUS_06("06", "外籍汽车"),
    STATUS_07("07", "普通摩托车"),
    STATUS_08("08", "轻便摩托车"),
    STATUS_09("09", "使馆摩托车"),
    STATUS_10("10", "领馆摩托车"),
    STATUS_11("11", "境外摩托车"),
    STATUS_12("12", "外籍摩托车"),
    STATUS_13("13", "低速车"),
    STATUS_14("14", "拖拉机"),
    STATUS_15("15", "挂车"),
    STATUS_16("16", "教练汽车"),
    STATUS_17("17", "教练摩托车"),
    STATUS_18("18", "试验汽车"),
    STATUS_19("19", "试验摩托车"),
    STATUS_20("20", "临时入境汽车"),
    STATUS_21("21", "临时入境摩托车"),
    STATUS_22("22", "临时行驶车"),
    STATUS_34("23", "警用汽车"),
    STATUS_24("24", "警用摩托"),
    STATUS_25("25", "原农机号牌"),
    STATUS_26("26", "香港入出境车"),
    STATUS_27("27", "澳门入出境车"),
    STATUS_51("51", "大型新能源汽车"),
    STATUS_52("52", "小型新能源汽车");
    /**
     * key 标识
     */
    private String key;
    /**
     * desc 描述
     */
    private String desc;

    HPStatus(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }
    // 普通方法
    public static String getDesc(String key) {
        for (HPStatus d : HPStatus.values()) {
            if (d.getKey().equals(key)) {
                return d.desc;
            }
        }
        return null;
    }
    public String getKey() {
        return key;
    }
    public String getDesc() {
        return desc;
    }
}
