package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver,String> {

    Driver findByDnum(String dnum);

    Driver findByCarNum(String carNum);

    List<Driver> findByName(String name);

    List<Driver> findByStatus(Integer status);

}
