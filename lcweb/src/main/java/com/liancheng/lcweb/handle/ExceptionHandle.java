package com.liancheng.lcweb.handle;


import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.domain.Result;
import com.liancheng.lcweb.exception.ManagerException;
import com.liancheng.lcweb.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.liancheng.lcweb.enums.ResultEnums.UNKNOWN_ERROR;

@ControllerAdvice
public class ExceptionHandle {

    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);
    //抛出的异常在这里被接受和处理
    @ExceptionHandler(value = Exception.class)
    @ResponseBody //json 格式？
    public Result handle(Exception e){
        if (e instanceof ManagerException){
            ManagerException managerException = (ManagerException) e;
            return ResultUtil.error(managerException.getCode(),managerException.getMessage());
        }
        else{
            logger.error("[系统异常]",e);
            return ResultUtil.error(UNKNOWN_ERROR.getCode(),UNKNOWN_ERROR.getMsg());
        }
    }

}
