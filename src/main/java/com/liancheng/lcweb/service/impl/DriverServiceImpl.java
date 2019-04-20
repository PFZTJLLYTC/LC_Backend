package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.enums.DriverStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.form.DriverInfoForm;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.service.DriverService;
import com.liancheng.lcweb.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public void addDriver(DriverInfoForm driverInfoForm) {
        if (findOne(driverInfoForm.getDnum())!=null){
            log.error("手机号已经被注册");
            throw new LcException(ResultEnums.USER_MOBILE_ALREADY_EXISTS);
        }
        Driver driver = new Driver();
        BeanUtils.copyProperties(driverInfoForm,driver);
        driver.setStatus(DriverStatusEnums.ATREST.getCode());
        //表示没有得到验证
        driver.setVerified(0);
        driver.setWorkTimes(0);
        //todo line的名字-考虑新建一个表？
        driverRepository.save(driver);
    }

    @Override
    public Driver getByMobileAndPassword(String mobile, String password) {
        return driverRepository.findByDnumAndPassword(mobile,password);
    }

    @Override
    public List<Driver> findAll() {
        return driverRepository.findAll();
    }

    @Override
    public List<Driver> findbyLineId(Integer lineId) {
        return driverRepository.findByLineId(lineId);
    }

    @Override
    public Driver findOne(String dnum) {
        return driverRepository.findById(dnum).get();
    }

    @Override
    public Driver findByCarNum(String carNum) {
        return driverRepository.findByCarNum(carNum);
    }

    @Override
    public Driver findByDriverName(String name) {
        return driverRepository.findByName(name);
    }

    @Override//1
    public List<Driver> findOnRoadDrivers() {
        return driverRepository.findByStatus(DriverStatusEnums.ONROAD.getCode());
    }

    @Override//0
    public List<Driver> findAtrestDrivers() {
        return driverRepository.findByStatus(DriverStatusEnums.ATREST.getCode());
    }

    @Override//2
    public List<Driver> findAvailableDrivers() {
        return driverRepository.findByStatus(DriverStatusEnums.AVAILABLE.getCode());
    }

    @Override
    public List<Driver> certainLIneOnroad(Integer lineId) {
        return driverRepository.findByStatusAndLineId(DriverStatusEnums.ONROAD.getCode(),lineId);
    }

    @Override
    public List<Driver> certainLIneAtrest(Integer lineId) {
        return driverRepository.findByStatusAndLineId(DriverStatusEnums.ATREST.getCode(),lineId);
    }

    @Override
    public List<Driver> certainLIneAvailable(Integer lineId) {
        return driverRepository.findByStatusAndLineId(DriverStatusEnums.AVAILABLE.getCode(),lineId);
    }

    @Override
    public ResultVO deleteOne(String dnum) {
        if (findOne(dnum)!=null){
            driverRepository.deleteById(dnum);
            return ResultVOUtil.success();
        }
        else {
            log.error("删除司机失败,dnum={}",dnum);
            throw new LcException(ResultEnums.NO_SUCH_DRIVER);
        }
    }
}
