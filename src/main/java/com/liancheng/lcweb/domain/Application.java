package com.liancheng.lcweb.domain;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Application {

    @Id
    @GeneratedValue
    private Integer apply_id;

    //申请的线路的名称
    private String lineName;

    //微信联系方式
    private String wx;

    //联系方式
    private String tel;

    //申请人姓名
    private String man_name;

    //补充信息
    private String other;

    //是否已经处理，0为未处理，1为已经接受，2为拒绝
    private Integer isDeal;

}
