package com.liancheng.lcweb.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;

import javax.swing.*;
import javax.validation.constraints.*;
import java.util.Date;

@Data
public class DriverInfoForm {

    @Pattern(regexp = "^[1][3,4,5,7,8][0-9]{9}$",message = "手机号格式不正确")
    private String dnum;

    @NotBlank(message = "姓名必填")
    private String name;

    @NotBlank(message = "密码必填")
    private String password;

    @NotBlank(message = "车牌号必填")
    private String carNum;

    @Min(value = 1,message = "人数不得小于1")
    @Max(value = 8,message = "人数不得多于8")//可以更大吗？
    private Integer availableSeats;

    //线路id
    //于是在manager添加的时候就需要验证一下是不是同一线路的了！
    @NotBlank(message = "注册线路必填")
    private String line;

    @Past(message = "生日只能为以前的时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

}
