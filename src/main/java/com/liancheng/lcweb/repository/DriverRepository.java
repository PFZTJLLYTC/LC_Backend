package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver,String> {

    Driver findByCarNum(String carNum);

    Driver findByName(String name);

    List<Driver> findByStatus(Integer status);

    //两种分别有使用的时候
    Page<Driver> findByLineId(Integer lineId, Pageable pageable);

    List<Driver> findByLineId(Integer lineId);

    List<Driver> findByStatusAndLineId(Integer status,Integer lineId);

    Page<Driver> findByStatusAndLineId(Integer status,Integer lineId,Pageable pageable);

    Driver findByDnumAndPassword(String dnum, String password);

}
