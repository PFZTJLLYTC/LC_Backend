package com.liancheng.lcweb.exception;

import com.liancheng.lcweb.enums.ResultEnums;

//只有继承RuntimeException 才会事务回滚，不然直接继承Exception是没有回滚的
public class ManagerException extends RuntimeException {

    //返回的码值
    private Integer code;

    public ManagerException(ResultEnums resultEnums){
        super(resultEnums.getMsg());
        this.code=resultEnums.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
