package com.liancheng.lcweb.form;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 用户填写的订单form， 修改订单也传这张表
 */
@Data
public class UserOrderForm {

    @NotBlank(message = "路线必填")
    private String lineName;//与line的name查

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$",message = "手机号格式不正确")
    private String userPhone;

    @NotBlank(message = "预约时间不能为空")
    private String time;

    @NotNull(message = "乘客人数不能为空")
    @Min(value = 0,message = "乘客人数不得少于0人" )//0人为寄件
    @Max(value = 7,message = "乘客人数不得多于7人" )
    private Integer userCount;

    private String date;

    /*** 选填部分 ***/
    private String detailAddress;
    //出发经度和纬度
    private String startLon;
    private String startLat;

    //详细下车地址
    private String detailDestination;
    //目的地经度和纬度
    private String endLon;
    private String endLat;

    //备注
    private String remark;
}
