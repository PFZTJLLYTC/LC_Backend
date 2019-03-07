package com.liancheng.lcweb.domain;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "driver")
public class Driver {

    //初步内容
    /* 0表示在休息（一般刚刚创建的司机状态为0，需要司机手动改变状态，1表示在路上，2表示可载客）
    初始密码为123456
    * */
    @Id
    private String dnum;

    private String name;

    private String password;

    @NotNull(message = "车牌号")
    private String carNum;

    @NotNull(message = "选择所属路线")
    private String line;

    @Min(value = 18,message = "should above 18 years old!")
    private Integer age;

    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Driver() {
        super();
    }

    public String getDnum() {
        return dnum;
    }

    public void setDnum(String dnum) {
        this.dnum = dnum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    @Override
    public String toString() {
        return "Driver{" +
                ", dnum='" + dnum + '\'' +
                ", password='" + password + '\'' +
                ", carNum='" + carNum + '\'' +
                ", line='" + line + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", status=" + status +
                '}';
    }
}
