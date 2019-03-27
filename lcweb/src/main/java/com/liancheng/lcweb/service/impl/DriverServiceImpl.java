package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.enums.DriverStatusEnums;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.service.DriverService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.List;

@Service
@Slf4j//如果使用此句其实可以不用定义log，但是无所谓多几行代码显得高端
public class DriverServiceImpl implements DriverService {

    private final static Logger log = LoggerFactory.getLogger(DriverServiceImpl.class);

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public Driver addDriver(@Valid Driver driver, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return null;
        }
        Driver result = new Driver();
        BeanUtils.copyProperties(driver,result);
        log.info("add a new driver successfully !");
        return driverRepository.save(result);
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
}
