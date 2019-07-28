package com.liancheng.lcweb.utils;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PushUtil {

    // AppKey= SHA1（你的应用ID + 'UZ' + 你的应用KEY +'UZ' +当前时间毫秒数）.当前时间毫秒数
    public static synchronized String getTrueAppkey(String str){
        if(null==str||str.length()==0){
            return null;
        }

        char[] hexDigits={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

        try{
            MessageDigest messageDigest=MessageDigest.getInstance("SHA1");
            messageDigest.update(str.getBytes("UTF-8"));
            byte[] md=messageDigest.digest();
            int j=md.length;
            char[] buf=new char[j*2];
            int k=0;
            for(int i=0;i<j;i++){
                byte byte0=md[i];
                buf[k++]=hexDigits[byte0>>>4&0xf];
                buf[k++]=hexDigits[byte0&0xf];
            }
            return new String(buf);
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return null;
    }
}
