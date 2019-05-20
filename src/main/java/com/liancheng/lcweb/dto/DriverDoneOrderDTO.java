package com.liancheng.lcweb.dto;

import lombok.Data;

@Data
public class DriverDoneOrderDTO {

    private String carNum;

    private String source;

    private String destination;

    private Integer userCount;

    private String date;

}
