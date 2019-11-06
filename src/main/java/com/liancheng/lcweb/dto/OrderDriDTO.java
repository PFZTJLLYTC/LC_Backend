package com.liancheng.lcweb.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class OrderDriDTO {

    private String orderId;

//    private String username;

    private String source;

    private String destination;

    private String carNum;

    private String userPhone;

    private String time;

    private Integer userCount;

    private String detailAddress;
    //出发经度和纬度
    private String startLon;
    private String startLat;

    private String detailDestination;
    //目的地经度和纬度
    private String endLon;
    private String endLat;

    private String date;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",timezone = "Asia/Shanghai")
    private Date createTime;

}
