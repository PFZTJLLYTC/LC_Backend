package com.liancheng.lcweb.service;


import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.User;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.List;

public interface DriverService {

    //注册
    Driver addDriver(Driver driver);

    //通过dnum查找,即id，亦即phonenum
    Driver findOne(String dnum);

    Driver findByCarNum(String carNum);

    Driver findByDriverName(String name);

    List<Driver> findOnRoadDrivers();

    List<Driver> findAtrestDrivers();

    List<Driver> findAvailableDrivers();



}
