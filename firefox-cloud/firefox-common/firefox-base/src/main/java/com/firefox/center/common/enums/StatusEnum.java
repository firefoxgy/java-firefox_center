package com.firefox.center.common.enums;

import lombok.Getter;

/**
 * @Author: ZJL
 * @Description: 操作方式枚举
 * @Date: 2020/07/01
 */
@Getter
public enum StatusEnum {

    /**
     * 无效
     */
    DISABLE(0, "禁用"),
    /**
     * 有效
     */
    ENABLE(1, "正常"),
    /**
     * 删除
     */
    DELETE(-1, "删除");

    private final Integer code;
    private final String value;

    private StatusEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() { return code;}

    public String getValue() { return value; }

}
