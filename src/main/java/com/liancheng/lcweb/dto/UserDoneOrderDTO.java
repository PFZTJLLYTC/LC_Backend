package com.liancheng.lcweb.dto;

import lombok.Data;

@Data
public class UserDoneOrderDTO {

    private String carNum;

    private String source;

    private String destination;

    private String userCount;

    private String date;
}
