package com.liancheng.lcweb.enums;


import lombok.Getter;

@Getter
public enum ResultEnums {
    UNKNOWN_ERROR(-1,"unknown error"),
    AGE_TOOYOUNG(-2,"should above 18 years old at least"),
    NOT_FOUND(-3,"Not Found"),;



    private String msg;
    private Integer code;


    ResultEnums(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
