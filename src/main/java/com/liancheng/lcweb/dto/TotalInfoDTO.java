package com.liancheng.lcweb.dto;

import lombok.Data;

import java.util.Date;

@Data
//前几天的根据数据库中来取,今日的就用今日的数据来当场算！
public class TotalInfoDTO {

    private String date;

    //当前可用司机数量
    private Integer liveDrivers;

    //总载客人数
    private Integer totalUserNum;

    //总订单数量,据时间来
    private Integer OrderNum;

    //前一日比再前一日的增长
    private String compareLWithLL;

}
