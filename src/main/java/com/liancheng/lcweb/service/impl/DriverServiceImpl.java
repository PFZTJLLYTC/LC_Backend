package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.constant.MessagesConstant;
import com.liancheng.lcweb.converter.Driver2DriverDTOConverter;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.dto.DriverAccountInfoDTO;
import com.liancheng.lcweb.dto.DriverDTO;
import com.liancheng.lcweb.enums.DriverStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.form.ChangePasswordForm;
import com.liancheng.lcweb.form.DriverInfoForm;
import com.liancheng.lcweb.form.DriverLoginForm;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private LineService lineService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private OrderService orderService;

    @Override
    public void addDriver(DriverInfoForm driverInfoForm) {
        if (findOne(driverInfoForm.getDnum())!=null){
            log.error("手机号已经被注册");
            throw new LcException(ResultEnums.USER_MOBILE_ALREADY_EXISTS);
        }
        Driver driver = new Driver();

        BeanUtils.copyProperties(driverInfoForm,driver);

        Integer lineId= lineService.findLineIdByLineName(driverInfoForm.getLineName());
        //密码BCrypt加密存储，框架自动加盐
        driver.setPassword(passwordEncoder.encode(driverInfoForm.getPassword()));
        driver.setLineId(lineId);
        //表示没有得到验证
        driver.setStatus(DriverStatusEnums.TO_BE_VERIFIED.getCode());

        driver.setWorkTimes(0);

        driver.setAvailableSeats((driverInfoForm.getSeatType()==0)?4:7);
        //等待被确认
        driverRepository.save(driver);
        try {
            webSocketService.sendInfo(MessagesConstant.newDriver,lineId+"");
        }catch (Exception e){
            log.info("提醒管理员有新的司机申请失败,linId={}",lineId);
        }
    }

    @Override
    public DriverDTO driverLogin(DriverLoginForm driverLoginForm){

        Driver driver = findOne(driverLoginForm.getDnum());

        if(driver==null)
            throw new LcException(ResultEnums.NO_SUCH_DRIVER);

        if(driver.getStatus().equals(DriverStatusEnums.TO_BE_VERIFIED.getCode())){
            throw new LcException(ResultEnums.WAIT_TO_BE_VERIFY);
        }

        boolean matches = passwordEncoder.matches(driverLoginForm.getPassword(),
                driver.getPassword());

        if(!matches){
            log.warn("密码验证错误");
            throw new LcException(ResultEnums.PASSWORD_MATCHES_ERROR);
        }

        return Driver2DriverDTOConverter.convert(driver);
    }

    @Override
    public void changePassword(ChangePasswordForm form) {
        String dnum = form.getId();
        Driver driver = findOne(dnum);
        if (driver==null ||  driver.getStatus().equals(DriverStatusEnums.TO_BE_VERIFIED.getCode())){
            log.error("无法修改没有入库或未通过审核的司机的密码");
            throw new LcException(ResultEnums.NO_SUCH_DRIVER);
        }else {
            log.info("司机{}修改密码成功",dnum);
            driver.setPassword(passwordEncoder.encode(form.getNewPassword()));
            driverRepository.save(driver);
        }
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
//应该写在manager的service里，不然新增司机时有逻辑冲突
//        if (!driver.isPresent()){
//            //查一般是交给manager，所以就不跑异常，到时候直接返回错误界面
//            throw new ManagerException(ResultEnums.NO_SUCH_USER.getMsg(),"/manager/driver/allDrivers");
//        }
        return driver.orElse(null);
    }

    @Override
    public void switchStatus(Integer status, Driver driver) {
        if (driver==null) {
            log.error("不能改变不存在的司机的状态");
            throw new LcException(ResultEnums.NO_SUCH_DRIVER);
        }
        driver.setStatus(status);
        driverRepository.save(driver);
    }

    @Override
    public void switchAvailableSeats(Integer availableSeats, Driver driver) {
        if (driver==null) {
            log.error("不能改变不存在的司机的可用座位数目");
            throw new LcException(ResultEnums.NO_SUCH_DRIVER);
        }
        Integer old = driver.getAvailableSeats();
        if (old.equals(availableSeats)) return;
        else {
            if (availableSeats>7||availableSeats<0){
                log.error("座位数不合法");
                throw new LcException(ResultEnums.SEATS_ERROR);
            }
        }
        driver.setAvailableSeats(availableSeats);

        driverRepository.save(driver);
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
    //两种状态
    public List<Driver> certainLIneAvailable(Integer lineId) {
        List<Driver> drivers = driverRepository.findByStatusAndLineId(DriverStatusEnums.AVAILABLE.getCode(),lineId);
        drivers.addAll(driverRepository.findByStatusAndLineId(DriverStatusEnums.AVAILABLE2.getCode(),lineId));
        return drivers;
    }

    @Override
    //待出行
    public List<Driver> certainLineAvailable1(Integer lineId) {
        return driverRepository.findByStatusAndLineId(DriverStatusEnums.AVAILABLE.getCode(),lineId);
    }

    @Override
    //待返程
    public List<Driver> certainLineAvailable2(Integer lineId) {
        return driverRepository.findByStatusAndLineId(DriverStatusEnums.AVAILABLE2.getCode(),lineId);
    }

    @Override
    public List<Driver> certainLIneToVerify(Integer lineId) {
        return driverRepository.findByStatusAndLineId(DriverStatusEnums.TO_BE_VERIFIED.getCode(),lineId);
    }

    @Override
    public Page<Driver> certainLIneAvailable(Integer lineId, Pageable pageable) {
        return driverRepository.findAllAvailable(lineId,pageable);
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
////            driverRepository.deleteById(dnum);
////        }
////        else {
////            log.error("删除司机失败,dnum={}",dnum);
////            throw new ManagerException(ResultEnums.NO_SUCH_DRIVER.getMsg(),"/manager/driver/allDrivers");
////        }
////    }
        driverRepository.deleteById(dnum);
    }

    @Override
    public void deleteMessages(String dnum) {
        log.info("用户{}删除所有消息",dnum);
        messagesService.deleteMessageByTarget(dnum);
    }

    @Override
    public void deleteCertainMessages(List<Integer> idList) {
        //已读即删
        for (Integer id : idList){
            messagesService.deleteMessage(id);
        }
    }

    @Override
    public DriverAccountInfoDTO findAccountInfo(String dnum){
        Driver driver=findOne(dnum);
        if(driver == null){
            log.info("无此司机");
            throw new LcException(ResultEnums.NO_SUCH_DRIVER);
        }

        DriverAccountInfoDTO driverAccountInfoDTO=new DriverAccountInfoDTO();

        BeanUtils.copyProperties(driver,driverAccountInfoDTO);

        driverAccountInfoDTO.setTodayOrders(orderService.findDriverTodayOrders(dnum,LocalDate.now()));
        driverAccountInfoDTO.setTodayUsers(orderService.findDriverTodayUsers(dnum,LocalDate.now()));

        return driverAccountInfoDTO;

    };

    @Override
    public void changeStatus(String dnum,Integer status){
        Driver driver=findOne(dnum);
        driver.setStatus(status);

        driverRepository.save(driver);
    }

    @Override
    public void changeAvailableSeats(String dnum, Integer availableSeats){
        Driver driver=findOne(dnum);
        driver.setAvailableSeats(availableSeats);

        driverRepository.save(driver);
    }

    @Override
    public DriverDTO checkLogin(String dnum){
        Driver driver=findOne(dnum);
        if(driver==null) {
            throw new LcException(ResultEnums.NO_SUCH_DRIVER);
        }

        return Driver2DriverDTOConverter.convert(driver);
    }
}
