package com.yjq.programmer.enums;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-07-06 15:57
 */
public enum StoreStateEnum {

    WAIT(1,"待审核"),

    SUCCESS(2,"审核通过"),

    FAIL(3,"审核不通过"),

    ;

    Integer code;

    String desc;

    StoreStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
