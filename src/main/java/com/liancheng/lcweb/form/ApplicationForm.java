package com.liancheng.lcweb.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ApplicationForm {

    //主要用于查看申请list，并与之联系
    @NotBlank(message = "申请线路名不能为空")
    private String lineName;

    //选填
    private String wx;

    //选填
    private String tel;

    @NotBlank(message = "申请人姓名不能为空")
    private String man_name;

    //其他信息选填，eg：dr人数
    private String other;
}
