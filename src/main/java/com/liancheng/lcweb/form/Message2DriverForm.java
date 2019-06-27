package com.liancheng.lcweb.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Message2DriverForm {

    private String dnum;

    @NotBlank(message = "要发推送则必须要有内容")
    private String message;

}
