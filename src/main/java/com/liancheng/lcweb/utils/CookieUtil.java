package com.liancheng.lcweb.utils;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class CookieUtil {

    //设置cookie
    //这里name就是token，value是造的值，maxage是寿命
    public static void set(HttpServletResponse response,
                           String name,
                           String value,
                           int maxAge){
        Cookie cookie = new Cookie(name,value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }


    //获取cookie,md直接上object算了,算了还是改回cookie
    public static Cookie get(HttpServletRequest request, String name){
        //取到map
        Map<String, Cookie> cookieMap = readCookieMap(request);
        if (cookieMap.containsKey(name)){
            return cookieMap.get(name);
        }
        return null;
    }



    //登陆的名字只是token，可能以后会用到其他name吧,所以就用map吧，便于扩展可能会
    //name就是key，需要的是"token"
    public static Map<String, Cookie> readCookieMap(HttpServletRequest request){
        Map<String, Cookie> map = new HashMap<>();
        //用cookie数组存拿
        Cookie[] cookies = request.getCookies();
        if (cookies!=null){
            for (Cookie cookie : cookies){
                map.put(cookie.getName(),cookie);
            }
        }
        return map;
    }


}
