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

    Integer manager_platform = 2; // 负责人移动端只有安卓

    String MANAGER_TITLE1 = "新的订单"; // 负责人的消息提示，移动端只有新订单？

    String MANAGER_TITLE2 = "新的司机申请";

    String manager_X_APICloud_AppId = "A6014137855572";

    String manager_app_key = "72FDFD90-613A-1BFF-14C5-71CC5908DE07";


}
