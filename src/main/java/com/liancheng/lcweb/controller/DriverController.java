package com.liancheng.lcweb.controller;


import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.VO.Result;
import com.liancheng.lcweb.domain.User;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.service.DriverService;
import com.liancheng.lcweb.service.ManagerService;
import com.liancheng.lcweb.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

//实现返回格式的拼接！
@RestController
@RequestMapping("/driver")
@Slf4j
public class DriverController {

    //private final static Logger logger = LoggerFactory.getLogger(DriverController.class);

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverService driverService;

    //登陆
    @PostMapping(value = "/login")
    @Transactional
    public Result userLogin(@RequestBody Driver driver){
        Driver result = driverService.getByMobileAndPassword(driver.getDnum(),driver.getPassword());
        if(result!=null){
            log.info("司机登陆成功，dnum={}",result.getDnum());
            return ResultUtil.success(result);
        }
        else{
            log.error("无此司机信息，dnum={}",driver.getDnum());
            return ResultUtil.error();
        }

    }


    //注册
    @PostMapping(value = "/drivers/add")//加表单验证
    public Result driverAdd(@Valid Driver driver, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return null;
        }
        driver.setDnum(driver.getDnum());

        driver.setAge(driver.getAge());
        driver.setCarNum(driver.getCarNum());
        driver.setStatus(0);
        driver.setPassword("123456");//初始化密码123456
        driver.setName(driver.getName());
        //如何让一个管理员只能看见自己的呢？
        driver.setLine(driver.getLine());

        log.info("add a new driver");
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
        log.info("update one driver's info");
        return ResultUtil.success(driverRepository.save(driver));
    }



}
