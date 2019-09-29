package com.liancheng.lcweb.service;

import com.google.gson.Gson;
import com.liancheng.lcweb.constant.PushModuleConstant;
import com.liancheng.lcweb.converter.PushDTO2Map;
import com.liancheng.lcweb.dto.PushDTO;
import com.liancheng.lcweb.utils.PushUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PushServiceWithImpl {

    @Autowired
    private RestTemplate restTemplate;

    public  boolean pushMessage2User(PushDTO userPushBody) throws Exception{

//        HttpHeaders headers = new HttpHeaders();
//        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        String appId = PushModuleConstant.user_X_APICloud_AppId;
        String appkey = PushModuleConstant.user_app_key;

//        String trueAppkey = getKey(appId,appkey);

//        headers.add("X-APICloud-AppId", appId);
//        headers.add("X-APICloud-AppKey", trueAppkey);
//        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        MultiValueMap<String, String> map = PushDTO2Map.pushDTO2Map(userPushBody);

//        String params = new Gson().toJson(map);

        //打印看看params
//        log.info(map.toString());

//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

//        ResponseEntity<String> response = restTemplate.postForEntity( PushModuleConstant.url_pre, request, String.class);

//        restTemplate.postForEntity( PushModuleConstant.url_pre, request, String.class);


        return post(appId,appkey,userPushBody);
    }


    public  boolean pushMessage2Driver(PushDTO driverPushBody){


        String appId = PushModuleConstant.driver_X_APICloud_AppId;
        String appKey = PushModuleConstant.driver_app_key;

        return post(appId,appKey,driverPushBody);

    }

    // 给管理员移动端的推送
    public  boolean pushMessage2Manager(PushDTO managerPushBody){


        String appId = PushModuleConstant.manager_X_APICloud_AppId;
        String appKey = PushModuleConstant.manager_app_key;

        return post(appId,appKey,managerPushBody);

    }

    private String getKey(String appId, String appKey){

        //返回自1970年1月1日 00:00:00 UTC到当前时间的毫秒数
        long now = Instant.now().toEpochMilli();

        String s = appId + "UZ" + appKey + "UZ" + now ;

        return PushUtil.getTrueAppkey(s)+ "."+ now ;

    }

    private boolean post(String appId, String appKey, PushDTO body){

        HttpHeaders headers = new HttpHeaders();
        //设置接收返回值的格式为json,
        //解决Error while extracting response for type [class xxx] and content type application/xml;charset=UTF-8 错误
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_JSON_UTF8);
        headers.setAccept(mediaTypeList);

        String trueAppkey = getKey(appId,appKey);
        headers.add("X-APICloud-AppId", appId);
        headers.add("X-APICloud-AppKey", trueAppkey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = PushDTO2Map.pushDTO2Map(body);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        //不管发送成功没有？暂时不解析response，反正有入库？
        restTemplate.postForEntity( PushModuleConstant.url_pre, request, String.class);
        return  true;
    }

}
