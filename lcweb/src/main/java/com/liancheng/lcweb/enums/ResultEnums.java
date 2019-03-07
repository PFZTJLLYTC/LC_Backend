package com.liancheng.lcweb.enums;

public enum ResultEnums {
    UNKNOWN_ERROR(-1,"unknown error"),
    AGE_TOOYOUNG(-2,"should above 18 years old at least");



    private String msg;
    private Integer code;


    ResultEnums(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
