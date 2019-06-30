package com.liancheng.lcweb.service;


import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.dto.DriverAccountInfoDTO;
import com.liancheng.lcweb.dto.DriverDTO;
import com.liancheng.lcweb.form.DriverInfoForm;
import com.liancheng.lcweb.form.DriverLoginForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DriverService {

    //注册
    void addDriver(DriverInfoForm driverInfoForm);

    DriverDTO driverLogin(DriverLoginForm driverLoginForm);

    List<Driver> findAll();

    //通过dnum查找,即id，亦即phonenum
    Driver findOne(String dnum);

    Page<Driver> findbyLineId(Integer lineId, Pageable pageable);

    List<Driver> findbyLineId(Integer lineId);

    Driver getByMobileAndPassword(String mobile,String password);

    void switchStatus(Integer status, Driver driver);

    void switchAvailableSeats(Integer availableSeats, Driver driver);

    Driver findByCarNum(String carNum);

    Driver findByDriverName(String name);

    List<Driver> findOnRoadDrivers();

    List<Driver> findAtrestDrivers();

    List<Driver> findAvailableDrivers();

    //暂时不删除，可能之后有方法需要使用

    List<Driver> certainLIneOnroad(Integer lineId);

    List<Driver> certainLIneAtrest(Integer lineId);

    //总的
    List<Driver> certainLIneAvailable(Integer lineId);

    List<Driver> certainLIneToVerify(Integer lineId);

    //待出行
    List<Driver> certainLineAvailable1(Integer lineId);

    //带返程
    List<Driver> certainLineAvailable2(Integer lineId);

    Page<Driver> certainLIneOnroad(Integer lineId,Pageable pageable);

    Page<Driver> certainLIneAtrest(Integer lineId,Pageable pageable);

    //总的
    Page<Driver> certainLIneAvailable(Integer lineId,Pageable pageable);

    Page<Driver> certainLIneToVerify(Integer lineId,Pageable pageable);

    void deleteOne(String dnum);

    //删除消息（已读消息直接删除!）
    void deleteMessages(String dnum);

    //删除选中的消息列表
    void deleteCertainMessages(List<Integer> idList);

    DriverAccountInfoDTO findAccountInfo(String dnum);


}
