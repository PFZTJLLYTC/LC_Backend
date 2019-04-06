package com.liancheng.lcweb.enums;


import lombok.Getter;

@Getter
public enum ResultEnums {
    UNKNOWN_ERROR(-1,"unknown error"),
    AGE_TOOYOUNG(-2,"should above 18 years old at least"),
    NOT_FOUND(-3,"Not Found"),
    NO_SUCH_USER(-4,"无此用户"),
    NO_SUCH_MANAGER(-5,"无此线路负责人"),
    NO_SUCH_DRIVER(-6,"无此司机"),
    ORDER_NOT_FOUND(11,"查无此订单"),
    ORDER_INFO_ERROR(12,"订单信息错误"),;



    private String msg;
    private Integer code;


    ResultEnums(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
