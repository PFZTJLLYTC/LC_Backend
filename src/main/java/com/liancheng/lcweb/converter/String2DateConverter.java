package com.liancheng.lcweb.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class String2DateConverter {

    //addDriver表改出生日期用的
    //todo 捕获这些异常
    public static Date convert(String data) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(data);
    }
}
