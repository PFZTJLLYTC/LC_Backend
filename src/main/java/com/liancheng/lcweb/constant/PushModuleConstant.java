package com.liancheng.lcweb.constant;

public interface PushModuleConstant {

    String url_pre = "https://p.apicloud.com/api/push/message";

    //git上传的时候不上传
    String user_X_APICloud_AppId = "A6096149079402";

    String user_app_key = "32CFC654-7074-7E4E-38A5-48B190D16721";//需要计算得到真正的appkey

    String driver_X_APICloud_AppId = "";

    String driver_app_key = "";

    String TITLE = "新的消息"; //消息标题，是否固定另说

    Integer platform = 2; //默认就安卓推送,0代表全平台推送

}
