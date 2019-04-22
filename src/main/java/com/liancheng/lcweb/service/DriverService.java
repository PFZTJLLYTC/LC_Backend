package com.liancheng.lcweb.service;


import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.form.DriverInfoForm;

import java.util.List;

public interface DriverService {

    //注册
    void addDriver(DriverInfoForm driverInfoForm);

    List<Driver> findAll();

    //通过dnum查找,即id，亦即phonenum
    Driver findOne(String dnum);

    List<Driver> findbyLineId(Integer lineId);

    Driver getByMobileAndPassword(String mobile,String password);

    Driver findByCarNum(String carNum);

    Driver findByDriverName(String name);

    List<Driver> findOnRoadDrivers();

    List<Driver> findAtrestDrivers();

    List<Driver> findAvailableDrivers();

    List<Driver> certainLIneOnroad(Integer lineId);

    List<Driver> certainLIneAtrest(Integer lineId);

    List<Driver> certainLIneAvailable(Integer lineId);

    List<Driver> certainLIneToVerify(Integer lineId);

    void deleteOne(String dnum);




}
