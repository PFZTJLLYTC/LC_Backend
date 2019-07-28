package com.liancheng.lcweb.converter;

import com.liancheng.lcweb.dto.PushDTO;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class PushDTO2Map {

    public static MultiValueMap<String, String> pushDTO2Map (PushDTO body){
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("title",body.getTitle());
        map.add("content",body.getContent());
        map.add("type",body.getType().toString());
        map.add("platform",body.getPlatform().toString());
        if (!body.getGroupName().equals("")){
            map.add("groupName",body.getGroupName());
        }
        if (!body.getUserIds().equals("")){
            map.add("userIds",body.getUserIds());
        }
        return map;

    }
}
