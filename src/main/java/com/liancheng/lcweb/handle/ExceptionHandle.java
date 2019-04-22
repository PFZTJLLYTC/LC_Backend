package com.liancheng.lcweb.handle;


import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.exception.ManagerException;
import com.liancheng.lcweb.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static com.liancheng.lcweb.enums.ResultEnums.UNKNOWN_ERROR;

@ControllerAdvice
@Slf4j
public class ExceptionHandle {

    //抛出的异常在这里被接受和处理
    @ExceptionHandler(value = LcException.class)
    @ResponseBody //json 格式？
    public ResultVO handleOthers(Exception e){

            LcException lcException = (LcException) e;
            return ResultVOUtil.error(lcException.getCode(), lcException.getMessage());

//        else if (e instanceof ManagerAuthorizeException){
//            return ResultVOUtil.error(ResultEnums.USER_TOKEN_EXPIRE.getCode(), ResultEnums.USER_TOKEN_EXPIRE.getMsg());
//        }

//        else{
//            log.error("[系统异常]",e);
//            return ResultVOUtil.error(UNKNOWN_ERROR.getCode(),UNKNOWN_ERROR.getMsg());
//        }
    }

    @ExceptionHandler(value = ManagerException.class)
    public ModelAndView handleAuthorizeExceptionOfManager(ManagerException e, Map<String,Object> map){

        map.put("msg",e.getMessage());
        map.put("url",e.getUrl());
        return new ModelAndView("common/error",map);

        //return new ModelAndView("redirect:".concat("www.baidu.com"));
    }

}
