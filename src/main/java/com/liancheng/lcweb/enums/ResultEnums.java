package com.liancheng.lcweb.enums;


import lombok.Getter;

@Getter
public enum ResultEnums {
    SUCCESS(0,"操作成功"),
    UNKNOWN_ERROR(-1,"unknown error"),
    AGE_TOOYOUNG(-2,"should above 18 years old at least"),
    NOT_FOUND(-3,"Not Found"),
    NO_SUCH_USER(-4,"无此用户"),
    NO_SUCH_MANAGER(-5,"无此线路负责人"),
    NO_SUCH_DRIVER(-6,"无此司机"),
    NO_WAIT_ORDER(-7,"无待处理订单"),//-7,-8,-9用户查询
    NO_PROCESSIN_ORDER(-8,"无进行中订单"),
    NO_DONE_ORDER(-9,"无已完成订单"),
    NO_WAIT_OR_PROCESSIN_ORDER(-10,"无待处理和进行中订单"),
    NO_SUCH_LINENAME(-11,"无此线路"),
    ORDER_NOT_FOUND(11,"查无此订单"),
    ORDER_INFO_ERROR(12,"订单信息错误"),
    USER_TOKEN_EXPIRE(13,"用户token过期或不存在"),
    USER_LOGIN_FORM_ERROR(14,"用户登入信息不合法"),
    WAIT_ORDER_MORE_THAN_ONE(15,"待处理订单过多"),//系统异常
    PROCESSIN_ORDER_TOO_MANY(16,"进行中订单过多"),//系统异常
    WAIT_OR_PROCESSIN_ORDER_MORE_THAN_ONE(17,"待处理或进行中订单过多"),//系统异常
    USER_CHANGE_FORM_ERROR(18,"信息修改填表错误"),
    USER_MOBILE_ALREADY_EXISTS(19,"号码已经被注册"),
    DRIVER_STATUS_ERROR(20,"司机状态错误"),
    LOG_OUT_SUCCESS(21,"登出成功"),
    DRIVER_REGISTER_FORM_ERROR(22,"司机注册表单信息错误"),
    DRIVER_LOGIN_FORM_ERROR(23,"司机登入表单信息错误"),
    SEATS_NOT_ENOUGH(24,"该司机可用座位数目不足"),
    ORDER_STATUS_ERROR(25,"订单状态错误"),
    SEATS_ERROR(26,"更改座位数目无效"),
    PASSWORD_MATCHES_ERROR(27,"密码错误"),
    NO_CERTAIN_CONTENT_MESSAGE(28,"无内容消息"),
    WAIT_TO_BE_VERIFY(29,"当前未通过审核"),
    SEATS_TYPE_ERROR(30,"请按照要求输入司机最大可用座位数"),;


    private String msg;
    private Integer code;


    ResultEnums(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
