package com.liancheng.lcweb.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;

@Data
public class addDriverFormForManager {
    @Pattern(regexp = "^[1][3,4,5,7,8][0-9]{9}$",message = "手机号格式不正确")
    private String dnum;

    @NotBlank(message = "姓名必填")
    private String name;

    //性别
    private Integer mof;

    @NotBlank(message = "密码必填")
    private String password;

    @NotBlank(message = "车牌号必填")
    private String carNum;

    @Min(value = 1,message = "人数不得小于1")
    @Max(value = 8,message = "人数不得多于8")//可以更大吗？
    private Integer availableSeats;

    @Past(message = "生日只能为以前的时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
}
