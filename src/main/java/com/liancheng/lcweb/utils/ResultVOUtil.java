package com.liancheng.lcweb.utils;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.enums.ResultEnums;

public class ResultVOUtil {

    //有返回数据的时候
    public static ResultVO success(Object object){
        ResultVO result = new ResultVO();
        result.setCode(0);
        result.setMsg("success");
        result.setData(object);//data应该是json数据块
        return result;
    }

    //没有返回数据的时候
    public static ResultVO success(){
        return success(null);
    }

    public static ResultVO error(ResultEnums resultEnums){
        ResultVO result = new ResultVO();
        result.setCode(resultEnums.getCode());
        result.setMsg(resultEnums.getMsg());
        //result.setData(null);
        return result;
    }
    public static ResultVO error(Integer code, String msg){
        ResultVO result = new ResultVO();
        result.setCode(code);
        result.setMsg(msg);
        //result.setData(null);
        return result;
    }
    public static ResultVO error(){
        return null;
    }
}
