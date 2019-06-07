package com.liancheng.lcweb.dto;

import lombok.Data;

import java.util.Date;

@Data
//前几天的就存在前端！
public class TotalInfoDTO {

    private String date;

    //当前可用司机数量
    private Integer liveDrivers;

    //总载客人数
    private Integer totalUserNum;

    //总订单数量,据时间来
    private Integer OrderNum;

    //总收入，可能作为前端计算一下
    private Integer totalGet;

}
