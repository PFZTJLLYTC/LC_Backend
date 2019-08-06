package com.liancheng.lcweb.dto;


import lombok.Data;

@Data
public class LineSelectDTO {

    private Integer lineId;

    //线路名字（必填）
    private String name;

    //条目类型（不传不显示）
    private String customerTypes;
}
