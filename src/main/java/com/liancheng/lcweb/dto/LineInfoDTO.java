package com.liancheng.lcweb.dto;

import lombok.Data;

@Data
public class LineInfoDTO {

    private Integer lineId;

    private String lineName;

    private String linePrice;

    private Integer managerNum;

    private Integer driverNum;

    // 今日订单数，要想更多/all/ -> 电脑端查看
    // 此处是实时更新的
    private Integer todayOrderNum;

    public LineInfoDTO(Integer lineId, String lineName, String linePrice, Integer managerNum, Integer driverNum, Integer todayOrderNum) {
        this.lineId = lineId;
        this.lineName = lineName;
        this.linePrice = linePrice;
        this.managerNum = managerNum;
        this.driverNum = driverNum;
        this.todayOrderNum = todayOrderNum;
    }
}
