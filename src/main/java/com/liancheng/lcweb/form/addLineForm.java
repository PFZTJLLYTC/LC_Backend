package com.liancheng.lcweb.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class addLineForm{

    @NotBlank(message = "线路名不能为空")
    private String lineName1;

    @NotBlank(message = "线路名不能为空")
    private String lineName2;

}
