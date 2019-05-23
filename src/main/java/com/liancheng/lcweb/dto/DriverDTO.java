package com.liancheng.lcweb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class DriverDTO implements Serializable {

    @JsonIgnore
    private static final long serialVersionUID = 1600536376434322957L;

    private String dnum;

    private String name;

    private String carNum;

    private String lineId;

    private Integer age;//算

    private Integer status;

    private Integer availableSeats;

    private Integer workTimes;

}
