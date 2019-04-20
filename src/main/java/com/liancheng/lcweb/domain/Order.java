package com.liancheng.lcweb.domain;

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
    private String userId;

    private String source;

    private String destination;

    private String userPhone;

    private String time;

    private Integer userCount;

    private String detailAddress;

    private String date; //前端传入的下单时间，格式“YY-MM-DD”可以考虑去掉

    private String dnum;

    private String driverName;

    private String carNum;


    //只显示了line的名字
    private String line;



    private Integer orderStatus =0;

    private Integer payStatus = 0;

    @CreatedDate
    //@JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime;

    @LastModifiedDate
    //@JsonSerialize(using = Date2LongSerializer.class)
    private Date updateTime;

}
