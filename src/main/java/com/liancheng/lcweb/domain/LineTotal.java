package com.liancheng.lcweb.domain;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

//可以用于统计
@Entity
@TypeDef(name = "json",typeClass = JsonStringType.class)
public class LineTotal {

    @Id
    private String LineTotalId;//根据时间生成

    private Integer lineId;//id

    private String date;//当天时间

    //今日在线工作司机数量
    private Integer liveDrivers;

    //当天展示
//    //设置只能看见名字,点进去可以看详情？
//    @Type(type = "json")
//    @Column(columnDefinition = "json")
//    private List<String> liveDriverList;

    //当天总载客人数
    private Integer totalUserNum;

    //当天总订单数量,据时间来
    private Integer OrderNUm;

    //假设总收入，可能作为前端计算一下
    private Integer totalGet;




}
