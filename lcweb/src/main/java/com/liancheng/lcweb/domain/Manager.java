package com.liancheng.lcweb.domain;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "manager")
public class Manager {

    //初步的管理员demo
    @Id
    @GeneratedValue
    private Integer id;//因为管理人员不会太多，故用int自增即可

    @NotNull(message = "名称不可为空")
    private String name;

    @NotNull(message = "密码不可为空")
    private String password;

    @NotNull(message = "手机号必填")
    private String phoneNum;

    @NotNull(message = "选择所管理的线路")
    private String line;

    private Integer age;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", line='" + line + '\'' +
                ", age=" + age +
                '}';
    }
}
