package com.liancheng.lcweb.controller;


import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Result;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.service.DriverService;
import com.liancheng.lcweb.service.ManagerService;
import com.liancheng.lcweb.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
public class DriverController {

    private final static Logger logger = LoggerFactory.getLogger(DriverController.class);
    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverService driverService;


    //登陆？



    //展现all
    @GetMapping(value = "/drivers")
    @Transactional
    public Result driverList(){
        logger.info("driverList");
        return ResultUtil.success(driverRepository.findAll());
    }

    //查询by num
    @GetMapping(value = "/driver/{num}")
    @Transactional
    public Result FindOneByDum(@PathVariable("num") String num){
        logger.info("find driver by phone number");
        return ResultUtil.success(driverRepository.findByDnum(num));
    }

    //查询by name
    @GetMapping(value = "/driver/{name}")
    @Transactional
    public Result FindOneByName(@PathVariable("name") String name){
        logger.info("find driver by name");
        return ResultUtil.success(driverRepository.findByName(name));
    }

    //查询by carNum
    @GetMapping(value = "/driver/{carNum}")
    @Transactional
    public Result FindOneByCarNum(@PathVariable("carNum") String carNum){
        logger.info("find driver by carNum");
        return ResultUtil.success(driverRepository.findByCarNum(carNum));
    }

    //查询by status
    @GetMapping(value = "/driver/{status}")
    @Transactional
    public Result FindOneByStatus(@PathVariable("status") Integer status){
        logger.info("find driver by carNum");
        return ResultUtil.success(driverRepository.findByStatus(status));
    }


    //删除司机信息
    @DeleteMapping(value = "/delete/{dnum}")
    @Transactional
    public Result driverDelete(@PathVariable("dnum") String dnum){
        logger.info("delete a particular driver");
        driverRepository.deleteById(dnum);
        return ResultUtil.success();
    }


    //增加信息
    @PostMapping(value = "/drivers/add")//加表单验证
    public Result driverAdd(@Valid Driver driver, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return null;
        }
        driver.setDnum(driver.getDnum());

        driver.setAge(driver.getAge());
        driver.setCarNum(driver.getCarNum());
        driver.setStatus(0);
        driver.setPassword("123456");
        driver.setName(driver.getName());
        //如何让一个管理员只能看见自己的呢？
        driver.setLine(driver.getLine());

        logger.info("add a new driver");
        return ResultUtil.success(driverRepository.save(driver));

    }



    //根据id更新driver信息
    //也可以提出来改成单独修改一项
    @PutMapping(value = "/driver/update/{id}")
    public Result driverUpdate(@PathVariable("id") String id,
                               @RequestParam("name") String name,
                               @RequestParam("password") String password,
                               @RequestParam("carNum") String carNum,
                               @RequestParam("line") String line,
                               @RequestParam("dNum") String dNum,
                               @RequestParam("age") Integer age,
                               @RequestParam("status") Integer status){
        Driver driver = new Driver();
        driver.setDnum(dNum);

        driver.setName(name);
        driver.setPassword(password);
        driver.setCarNum(carNum);
        driver.setLine(line);
        driver.setAge(age);
        driver.setStatus(status);
        logger.info("update one driver's info");
        return ResultUtil.success(driverRepository.save(driver));
    }



}
