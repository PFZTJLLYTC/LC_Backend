package com.liancheng.lcweb.domain;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import netscape.javascript.JSObject;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

//可以用于统计
@Entity
@TypeDef(name = "json",typeClass = JsonStringType.class)
public class LineTotal {

    @Id
    private String LineTotalId;

    private Integer lineId;

    private String date;

    //今日有工作司机数量
    private Integer liveDrivers;

    //设置只能看见名字,点进去可以看详情？
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private List<String> liveDriverList;

    //总载客人数
    private Integer totalUserNum;

    //总订单数量,据时间来
    private Integer OrderNUm;

    //总收入，可能作为前端计算一下
    private Integer totalGet;




}
