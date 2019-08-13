package com.liancheng.lcweb.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
//注册
public class UserInfoForm {

//    @RequestParam("username") String username,
//    @RequestParam("password") String password,
//    @RequestParam("mobile") String mobile,
//    @RequestParam("email") String email,
//    @RequestParam("emailVerified") Boolean emailVerified,

    @NotBlank(message = "用户名名必填")
    private String username;

    @NotBlank(message = "密码必填")
    private String password;//二次验证

    @NotBlank(message = "电话号码必填")
    private String mobile;


}
