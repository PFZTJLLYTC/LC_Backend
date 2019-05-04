package com.liancheng.lcweb.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DriverLoginForm {

    @NotBlank(message = "手机号不为空")
    private String dnum;

    @NotBlank(message = "密码不为空")
    private String password;
}
