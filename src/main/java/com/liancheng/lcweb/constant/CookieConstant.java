package com.liancheng.lcweb.constant;

public interface CookieConstant {

    String TOKEN = "token";//想不到其他好名字了，就它吧

    //之后改成20或者更久好了
    Integer EXPIRE = 7200;//保持一致

    String EXPIRE_URL = "http://127.0.0.1:8080/lc/index.html";
}