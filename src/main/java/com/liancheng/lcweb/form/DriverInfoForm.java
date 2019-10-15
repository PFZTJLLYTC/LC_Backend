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

    @Pattern(regexp = "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z][A-Z](([0-9]{5}[DF])|([DF]([A-HJ-NP-Z0-9])[0-9]{4})))|([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳])",
            message="车牌号格式不正确，注意区分大小写")
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
