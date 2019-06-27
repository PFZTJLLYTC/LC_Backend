package com.liancheng.lcweb.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Messages {

    @Id
    private Integer id;

    @NotNull
    private String target;

    @NotNull
    private String message;

    @CreatedDate
    private Date createTime;

}
