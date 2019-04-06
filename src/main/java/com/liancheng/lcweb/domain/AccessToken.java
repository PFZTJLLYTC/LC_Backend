package com.liancheng.lcweb.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class AccessToken {

    @Id
    private String Id;      //token ID登陆时生成

    private String userId;  //保存用户ID

    private Integer ttl;   //过期时间1209600秒 14天

    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private  Date updateTime;
}
