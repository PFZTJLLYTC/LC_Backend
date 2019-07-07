package com.liancheng.lcweb.dto;

import lombok.Data;

@Data
public class DriverAccountInfoDTO {

    private String name;

    private String dnum;

    private String carNum;

    private Integer status;

    private Integer seatType;

    private Integer availableSeats;

    private Integer todayOrders;//今日接单数

    private Integer todayUsers;//今日载客量

}
