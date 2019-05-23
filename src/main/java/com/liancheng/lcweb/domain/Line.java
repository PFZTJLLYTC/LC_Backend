package com.liancheng.lcweb.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

//得新建一个line表
@Data
@Entity
public class Line {

    @Id
    @GeneratedValue
    private Integer lineId;

    private String lineName1;//正向

    private String lineName2;//反向

    //司机总数和订单总数算出

    //有哪些管理员搜索得出

}
