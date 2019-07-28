package com.liancheng.lcweb.service;

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
import java.util.LinkedHashMap;

@Service
@Slf4j
public class PushServiceWithImpl {

    @Autowired
    private RestTemplate restTemplate;

    //如果传入的是xxx，就全部推送
    public  boolean pushMessage2User(Integer userId, PushDTO body) throws Exception{

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");

        String appId = PushModuleConstant.user_X_APICloud_AppId;
        String appkey = PushModuleConstant.user_app_key;

        //返回自1970年1月1日 00:00:00 UTC到当前时间的毫秒数
        long now = Instant.now().toEpochMilli();

        String s = appId + "UZ" + appkey + "UZ" + now ;
        String trueAppkey = PushUtil.getTrueAppkey(s)+ "."+ now ;

        headers.add("X-APICloud-AppId", appId);
        headers.add("X-APICloud-AppKey", trueAppkey);
        headers.setContentType(type);
        MultiValueMap<String, String> params = PushDTO2Map.pushDTO2Map(body);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity( PushModuleConstant.url_pre, request , String.class );
        //不管发送成功没有？暂时不解析response，反正有入库？
        return true;
    }


    public  boolean pushMessage2Driver(Integer groupId, PushDTO body){

        return true;

    }

}
