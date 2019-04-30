package com.liancheng.lcweb.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
public class DriverInfoForm {

    @NotEmpty(message = "号码必填且不可更改")
    private String dnum;

    @NotEmpty(message = "姓名必填")
    private String name;

    @NotEmpty(message = "密码必填")
    private String password;

    @NotEmpty(message = "车牌号必填")
    private String carNum;

    //线路id
    //于是在manager添加的时候就需要验证一下是不是同一线路的了！
    @NotEmpty(message = "注册线路必填")
    private String line;

    @Min(value = 18,message = "不得小于18岁")
    @NotEmpty(message = "姓名必填")
    private Integer age;

}
