package com.liancheng.lcweb.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;

@Data
public class DriverInfoForm {

    @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$",message = "手机号格式不正确")
    private String dnum;

    @NotBlank(message = "姓名必填")
    private String name;

    @NotBlank(message = "密码必填")
    private String password;

    @NotBlank(message = "车牌号必填")
    private String carNum;

    private Integer seatType;

    //线路名称
    //做一个选择
    @NotBlank(message = "注册线路必填")
    private String lineName;//A-B

    @Past(message = "生日只能为以前的时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

}
