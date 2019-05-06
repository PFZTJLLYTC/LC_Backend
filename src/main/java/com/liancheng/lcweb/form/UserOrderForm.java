package com.liancheng.lcweb.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 用户填写的订单form
 */
@Data
public class UserOrderForm {

    //lineId必须在这里有呈现!
    @NotNull(message = "线路选择！")
    private Integer lineId;

    @NotBlank(message = "出发地不能为空")
    private String source;

    @NotBlank(message = "目的地不能为空" )
    private String destination;

    @NotBlank(message = "联系电话不能为空")
    private String userPhone;

    @NotBlank(message = "预约时间不能为空")
    private String time;

    @NotNull(message = "乘客人数不能为空")
    private Integer userCount;

    private String detailAddress;

    private String date;
}
