package com.liancheng.lcweb.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

//生成报表统计用
@Entity
//@TypeDef(name = "json",typeClass = JsonStringType.class)
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
@Data
public class LineTotal {

    @Id
    @GeneratedValue
    private Integer lineTotalId;//根据时间生成

    private Integer lineId;//id

    //生成报表的时间
    private String date;

    // 0 表示 每天的；1表示每个月的；2表示每年的
    private Integer type;

    //当前工作司机数量，这个不放在报表里面
//    private Integer liveDrivers;

    //当天展示
//    //设置只能看见名字,点进去可以看详情？
//    @Type(type = "json")
//    @Column(columnDefinition = "json")
//    private List<String> liveDriverList;

    //今日总载客人数
    private Integer userCount;

    //当天总订单数量,据时间来
    private Integer orderCount;

    //与前一日的比较
    private String compare;

    @CreatedDate
    @JsonIgnore
    //@JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime;


}
