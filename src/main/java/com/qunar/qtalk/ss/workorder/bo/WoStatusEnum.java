package com.qunar.qtalk.ss.workorder.bo;


import java.util.Objects;

public enum WoStatusEnum {
    TO_BE_PROCESSED(0, "待处理"),
    PROCESSING(1, "处理中"),
    CLOSED(2, "已关闭");

    private Integer status;

    private String name;

    WoStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public static WoStatusEnum getStatus(Integer status) {
        for (WoStatusEnum woStatusEnum : WoStatusEnum.values()) {
            if (Objects.equals(woStatusEnum.status, status)) {
                return woStatusEnum;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }
}
