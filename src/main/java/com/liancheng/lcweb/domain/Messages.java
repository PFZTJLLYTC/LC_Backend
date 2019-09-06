package com.liancheng.lcweb.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Messages {

    @Id
    @GeneratedValue
    private Integer id;

    @NotNull
    private String target;

    @NotNull
    private String message;

    @NotNull
    // type 0 表示为普通通知， 1为订单相关， 2为其他通知（包括注册成功通知）
    private Integer type;

    @CreatedDate
    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss",timezone = "Asia/Shanghai")
    private Date createTime;

}
