package com.liancheng.lcweb.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MobileManRequestForm {

    // 这玩意儿是保存
    @NotNull(message = "lineId字段必填")
    private Integer lineId;

    private String token;

    private String dnum;

    private String orderId;

    private Integer status; // 司机和订单status复用

    private String price; // 定价


}
