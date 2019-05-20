package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.enums.DriverStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.exception.ManagerException;
import com.liancheng.lcweb.form.DriverInfoForm;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.service.DriverService;
import com.liancheng.lcweb.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        //表示没有得到验证
        driver.setStatus(DriverStatusEnums.TO_BE_VERIFIED.getCode());
        driver.setWorkTimes(0);
        //默认四座,应该在form李表现出来
        driver.setAvailableSeats(4);
        //todo line的名字-考虑新建一个表？
        //等待被确认
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
    public Page<Driver> findbyLineId(Integer lineId, Pageable pageable) {
        return driverRepository.findByLineId(lineId,pageable);
    }

    @Override
    public List<Driver> findbyLineId(Integer lineId) {
        return driverRepository.findByLineId(lineId);
    }

    @Override
    public Driver findOne(String dnum) {

        Optional<Driver> driver = driverRepository.findById(dnum);
//todo 应该写在manager的service里，不然新增司机时有逻辑冲突
//        if (!driver.isPresent()){
//            //查一般是交给manager，所以就不跑异常，到时候直接返回错误界面
//            throw new ManagerException(ResultEnums.NO_SUCH_USER.getMsg(),"/manager/driver/allDrivers");
//        }
        return driver.orElse(null);
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
    public List<Driver> certainLIneToVerify(Integer lineId) {
        return driverRepository.findByStatusAndLineId(DriverStatusEnums.TO_BE_VERIFIED.getCode(),lineId);
    }

    @Override
    public Page<Driver> certainLIneAvailable(Integer lineId, Pageable pageable) {
        return driverRepository.findByStatusAndLineId(DriverStatusEnums.AVAILABLE.getCode(),lineId,pageable);

    }

    @Override
    public Page<Driver> certainLIneToVerify(Integer lineId, Pageable pageable) {
        return driverRepository.findByStatusAndLineId(DriverStatusEnums.TO_BE_VERIFIED.getCode(),lineId,pageable);
    }

    @Override
    public Page<Driver> certainLIneOnroad(Integer lineId, Pageable pageable) {
        return driverRepository.findByStatusAndLineId(DriverStatusEnums.ONROAD.getCode(),lineId,pageable);

    }

    @Override
    public Page<Driver> certainLIneAtrest(Integer lineId, Pageable pageable) {
        return driverRepository.findByStatusAndLineId(DriverStatusEnums.ATREST.getCode(),lineId,pageable);
    }

    @Override
    public void deleteOne(String dnum) {
//        if (findOne(dnum)!=null){
//            driverRepository.deleteById(dnum);
//        }
//        else {
//            log.error("删除司机失败,dnum={}",dnum);
//            throw new ManagerException(ResultEnums.NO_SUCH_DRIVER.getMsg(),"/manager/driver/allDrivers");
//        }
//    }
        driverRepository.deleteById(dnum);
    }

}
