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

    @NotBlank(message = "路线必填")
    private String lineName;//与line的name查

    @NotBlank(message = "联系电话不能为空")
    private String userPhone;

    @NotBlank(message = "预约时间不能为空")
    private String time;

    @NotNull(message = "乘客人数不能为空")
    private Integer userCount;

    private String detailAddress;

    private String date;
}
