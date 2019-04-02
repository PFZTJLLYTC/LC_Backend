package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver,String> {

    Driver findByCarNum(String carNum);

    Driver findByName(String name);

    List<Driver> findByStatus(Integer status);

    List<Driver> findByLineId(String lineId);

    List<Driver> findByStatusAndLineId(Integer status,String lineId);

    Driver findByDnumAndPassword(String dnum, String password);

}
