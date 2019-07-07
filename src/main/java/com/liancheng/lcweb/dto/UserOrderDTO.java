package com.liancheng.lcweb.dto;

import lombok.Data;

@Data
public class UserOrderDTO {

    //详细地址和备注暂时不在用户端显示

    private String orderId;

    private String source;

    private String destination;

    private String userCount;

    private String userPhone;

    private String carNum;

    private String dnum;

    private String driverName;

    private String time;

    private String date;

}
