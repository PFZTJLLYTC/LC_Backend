package com.liancheng.lcweb.domain;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "user")
public class User {

    @Id
    private String unum;

    @NotNull(message = "用户名不能为为空")
    private String username;

    @NotNull(message = "密码不能为为空")
    private String password;

    private String mobile;

    @Email//必须为电子邮件
    private String email;

    private Boolean emailVerifiled;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")//时间格式化
    @NotNull(message = "创建日期") // 不能为空
    private Date createdAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")//时间格式化
    @NotNull(message = "更新日期") // 不能为空
    private Date updateAt;

    public User(){ super(); }

    public String getUnum() { return unum; }

    public void setUnum(String unum){ this.unum = unum; }

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getPassword(){ return password; }

    public void setPassword(String password){ this.password = password; }

    public String getEmail(){return email;}

    public void setEmail(String email){this.email = email;}

    public Boolean getEmailVerifiled(){ return emailVerifiled;}

    public void setEmailVerifiled(Boolean emailVerifiled) {this.emailVerifiled = emailVerifiled;}

    public String getMoblie(){ return mobile; }

    public void setMobile(String mobile){ this.mobile = mobile; }

    public Date getCreatedAt(){return createdAt;}

    public void setCreatedAt(Date createdAt){this.createdAt = createdAt;}

    public Date getUpdateAt(){return updateAt;}

    public void setUpdateAt(Date updateAt){this.updateAt = updateAt;}

    @Override
    public String toString() {
        return "User{" +
                "unum=" + unum +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", mobile='" + mobile +'\''+
                ", email='" + email + '\'' +
                ", emailVerified='" + emailVerifiled + '\'' +
                ", createdAt='" + createdAt +'\''+
                ", updateAt='" + updateAt +'\''+
                '}';
    }
}
