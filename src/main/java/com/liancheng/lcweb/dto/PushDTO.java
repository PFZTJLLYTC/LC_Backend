package com.liancheng.lcweb.dto;


import lombok.Data;

@Data
public class PushDTO {

    private String title;

    private String content;

    private Integer type; // 消息类型，1:消息 2:通知

    private Integer platform; // 0:全部平台，1：ios, 2：android

    /** 下面两个user使用默认，司机则不能使用默认 **/
    private String groupName; //推送组名，多个组用英文逗号隔开.默认:全部组。eg.group1,group2

    private String userIds; //推送用户id, 多个用户用英文逗号分隔，eg. user1,user2。

    public PushDTO(String title, String content, Integer type, Integer platform, String groupName,String userIds){

        this.title = title;
        this.content = content;
        this.type = type;
        this.platform = platform;
        this.groupName = groupName;
        this.userIds = userIds;
    }


}
