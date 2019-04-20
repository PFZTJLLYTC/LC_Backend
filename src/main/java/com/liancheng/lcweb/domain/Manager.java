package com.liancheng.lcweb.domain;


import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Manager {

    //初步的管理员demo,考虑线路id即为管理人id
    @Id
    @GeneratedValue
    private Integer lineId;//因为管理人员不会太多，故用int自增即可

    @NotNull(message = "用户名不能为为空")
    private String name;

    @NotNull(message = "密码不能为为空")
    private String password;

    //对应lineId的名字
    private String line;

    private String phoneNum;

    private Integer age;

    private Integer peopleAmount;

    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private Date updateTime;

}
