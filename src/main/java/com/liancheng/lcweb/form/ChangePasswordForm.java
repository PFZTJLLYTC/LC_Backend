package com.liancheng.lcweb.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

//id三合一啊
@Data
public class ChangePasswordForm {

    @NotEmpty(message = "name或tel必填")
    private String id;

    @NotEmpty(message = "新密码必传")
    private String newPassword;

}
