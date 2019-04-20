package com.liancheng.lcweb.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class UserInfoForm {

//    @RequestParam("username") String username,
//    @RequestParam("password") String password,
//    @RequestParam("mobile") String mobile,
//    @RequestParam("email") String email,
//    @RequestParam("emailVerified") Boolean emailVerified,

    @NotEmpty(message = "用户名名必填")
    private String username;

//    @NotEmpty(message = "密码必填")
//    private String password;

    @NotEmpty(message = "电话号码必填")
    private String mobile;

//    @NotEmpty(message = "邮箱必填") 如果是手机短信验证，则选择填写
    private String email;




}
