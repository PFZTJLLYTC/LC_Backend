package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application,Integer> {

    //暂时不需要额外方法

    Application findByTel(String tel);

}
