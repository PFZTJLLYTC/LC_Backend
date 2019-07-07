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

    @NotNull
    private Integer lineId;

    @NotNull
    private String lineName;

    //备注
    private String remark;

    @NotNull
    private String userId;

    @NotNull
    private String userPhone;

    private String dnum;

    @NotNull
    private String time;

    @NotNull
    private Integer userCount;

    //详细地址
    private String detailAddress;

    private String date; //前端传入的下单日期，格式“YYYY-MM-DD”

    private String driverName;

    private String carNum;

    private Integer orderStatus =0;

//    private Integer payStatus = 0; //没有用啊

    @CreatedDate
    @JsonIgnore
    //@JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime;

    @LastModifiedDate
    @JsonIgnore
    //@JsonSerialize(using = Date2LongSerializer.class)
    private Date updateTime;

}
