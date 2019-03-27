package com.liancheng.lcweb.utils;

import com.liancheng.lcweb.VO.Result;


public class ResultUtil {

    //有返回数据的时候
    public static Result success(Object object){
        Result result = new Result();
        result.setCode(0);
        result.setMsg("success");
        result.setData(object);//data应该是json数据块
        return result;
    }

    //没有返回数据的时候
    public static Result success(){
        return success(null);
    }

    public static Result error(Integer code, String msg){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        //result.setData(null);
        return result;

    }
}
