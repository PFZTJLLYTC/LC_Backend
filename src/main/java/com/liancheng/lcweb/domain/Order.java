package com.liancheng.lcweb.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liancheng.lcweb.utils.serializer.Date2LongSerializer;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    private String OrderId;

    @NotNull
    private String userId;

    private String userPhone;

    private String userAddress;

    private BigDecimal userCount;

    private String dnum;

    private String driverName;

    //只显示了line的名字
    private String line;

    private String carNum;

    private Integer orderStatus;

    private Integer payStatus;

    @CreatedDate
    //@JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime;

    @LastModifiedDate
    //@JsonSerialize(using = Date2LongSerializer.class)
    private Date updateTime;

}
