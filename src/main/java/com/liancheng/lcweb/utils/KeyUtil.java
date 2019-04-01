package com.liancheng.lcweb.utils;

import java.util.Random;

public class KeyUtil {

    public static synchronized String genUniquekey(){
        Random random = new Random();
        //永远生成6位随机数
        Integer number = random.nextInt(900000)+100000;

        return System.currentTimeMillis()+String.valueOf(number);

    }
}