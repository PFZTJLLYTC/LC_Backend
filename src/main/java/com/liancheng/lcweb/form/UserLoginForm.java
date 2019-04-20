package com.liancheng.lcweb.form;

import lombok.Data;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

//用于接收登陆表单对象
@Data
public class UserLoginForm {

    @NotEmpty(message="手机号不能为空")
    private String mobile;

    @NotEmpty(message="密码不能为空")
    private String password;
}
