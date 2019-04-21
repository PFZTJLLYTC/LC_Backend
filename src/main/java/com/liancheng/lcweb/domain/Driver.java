package com.liancheng.lcweb.domain;


import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Driver {

    //初步内容
    /* 0表示在休息（一般刚刚创建的司机状态为0，需要司机手动改变状态，1表示在路上，2表示可载客）
    初始密码为123456
    * */
    @Id
    private String dnum;//dnum就是phonenum？

    private String name;

    private String password;

    @NotNull(message = "车牌号")
    private String carNum;

    //线路id
    private Integer lineId;

    private String line;

    @Min(value = 18,message = "should above 18 years old!")
    private Integer age;

    private Integer status;

    private Integer availableSeats;

    //改进方向：精确时间
    private Integer workTimes = 0;

    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private Date updateTime;
}
