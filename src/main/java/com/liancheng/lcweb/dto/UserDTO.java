package com.liancheng.lcweb.dto;

import lombok.Data;


@Data
public class UserDTO {

    private String username;

    private String mobile;

    private String email;

    //乘车次数
    private Integer takeTimes;
}
