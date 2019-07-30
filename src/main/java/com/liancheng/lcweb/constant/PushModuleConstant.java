package com.liancheng.lcweb.constant;

public interface PushModuleConstant {

    String url_pre = "https://p.apicloud.com/api/push/message";

    //git上传的时候不上传
    String user_X_APICloud_AppId = "A6096149079402";

    String user_app_key = "32CFC654-7074-7E4E-38A5-48B190D16721";//需要计算得到真正的appkey

    String driver_X_APICloud_AppId = "A6093508435107";

    String driver_app_key = "81CB3135-457E-5DFB-54CB-1B015F9322FB";

    String TITLE = "新的消息"; //消息标题，是否固定另说

    Integer platform = 2; //默认就安卓推送,0代表全平台推送

}
