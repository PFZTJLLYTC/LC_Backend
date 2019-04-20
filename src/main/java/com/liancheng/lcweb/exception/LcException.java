package com.liancheng.lcweb.exception;

import com.liancheng.lcweb.enums.ResultEnums;
import lombok.Getter;


@Getter
public class LcException extends RuntimeException {

    //返回的码值
    private Integer code;

    public LcException(ResultEnums resultEnums){
        super(resultEnums.getMsg());
        this.code=resultEnums.getCode();
    }

    public LcException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

}
