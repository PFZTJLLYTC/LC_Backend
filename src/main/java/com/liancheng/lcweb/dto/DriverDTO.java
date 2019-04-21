package com.liancheng.lcweb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class DriverDTO implements Serializable {

    //todo 再来个详情页面才行啊
    //不知是否正确
    @JsonIgnore
    private static final long serialVersionUID = 1600536376434322957L;

    private String dnum;

    private String name;

    private String carNum;

    private String line;

    private Integer age;

    private Integer status;

    private Integer availableSeats;

    private Integer workTimes;

}
