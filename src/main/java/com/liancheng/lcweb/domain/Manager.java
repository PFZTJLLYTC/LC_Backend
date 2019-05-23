package com.liancheng.lcweb.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Data
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Manager {

    //初步的管理员demo,考虑线路id即为管理人id
    @Id
    @GeneratedValue
    private String telNum;

    private Integer lineId;//加外键jilian

    private String name;

    @NotBlank(message = "密码必填")
    private String password;

    @CreatedDate
    @JsonIgnore
    private Date createTime;

    @LastModifiedDate
    @JsonIgnore
    private Date updateTime;

}
