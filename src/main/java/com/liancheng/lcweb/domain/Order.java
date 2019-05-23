package com.liancheng.lcweb.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_order")
public class Order {

    @Id
    private String OrderId;

    //不加外键和jilian删除
    private Integer lineId;

    @NotNull
    private String userId;

    private String userPhone;

    private String dnum;

    private String time;

    private Integer userCount;

    private String detailAddress;

    private String date; //前端传入的下单时间，格式“YY-MM-DD”可以考虑去掉

    private String driverName;

    private String carNum;

    private Integer orderStatus =0;

    private Integer payStatus = 0;

    @CreatedDate
    @JsonIgnore
    //@JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime;

    @LastModifiedDate
    @JsonIgnore
    //@JsonSerialize(using = Date2LongSerializer.class)
    private Date updateTime;

}
