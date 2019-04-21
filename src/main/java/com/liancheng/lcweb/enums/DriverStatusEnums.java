package com.liancheng.lcweb.enums;


import lombok.Getter;

@Getter
public enum DriverStatusEnums {
    ONROAD(1,"在路上"),
    ATREST(0,"休息中"),
    AVAILABLE(2,"待出行"),
    TO_BE_VERIFIED(-1,"待审核");

    private Integer code;

    private String msg;

    DriverStatusEnums(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
