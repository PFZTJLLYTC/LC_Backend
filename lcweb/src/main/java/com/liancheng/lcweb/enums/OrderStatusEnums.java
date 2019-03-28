package com.liancheng.lcweb.enums;


import lombok.Getter;

@Getter
public enum OrderStatusEnums {
    WAIT(0,"未处理订单"),
    PROCESSIN(1,"进行中订单"),
    DONE(2,"已完成订单");

    private Integer code;

    private String msg;

    OrderStatusEnums(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
