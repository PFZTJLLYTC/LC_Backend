package com.liancheng.lcweb.constant;

public interface PushModuleConstant {

    String url_pre = "https://p.apicloud.com/api/push/message";

    //git上传的时候不上传
    String user_X_APICloud_AppId = "A6096149079402";

    String user_app_key = "32CFC654-7074-7E4E-38A5-48B190D16721";//需要计算得到真正的appkey

    String driver_X_APICloud_AppId = "A6093508435107";

    String driver_app_key = "81CB3135-457E-5DFB-54CB-1B015F9322FB";

    String TITLE = "新的消息"; //消息标题

    Integer platform = 0; //0代表全平台推送,1表示ios,2表示安卓

    // todo 加上manager 简易移动端

}
