package com.liancheng.lcweb.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserOrderDTO {


    private String orderId;

    private String source;

    private String destination;

    private Integer userCount;

    private String userPhone;

    private String carNum;

    private String dnum;

    private String driverName;

    private String time;

    private String detailAddress;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",timezone = "Asia/Shanghai")
    private Date createTime;

}
