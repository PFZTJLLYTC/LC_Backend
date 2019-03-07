package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver,String> {

    public List<Driver> findByDnum(String dnum);

    public List<Driver> findByCarNum(String carNum);

    public List<Driver> findByName(String name);

    public List<Driver> findByStatus(Integer status);

}
