package com.liancheng.lcweb.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Data
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
//暂时不管它
public class admin {
    @Id
    private String symbol;


    @NotNull(message = "密码不能为为空")
    private String password;
}
