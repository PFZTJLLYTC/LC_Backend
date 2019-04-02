package com.liancheng.lcweb.controller;

import com.liancheng.lcweb.VO.Result;
import com.liancheng.lcweb.service.DriverService;
import com.liancheng.lcweb.service.ManagerService;
import com.liancheng.lcweb.service.UserService;
import com.liancheng.lcweb.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@Slf4j
@RequestMapping("/root")
@RestController
public class RootController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private UserService userService;

    /*司机相关*/
    //查司机
    @GetMapping(value = "/driver")
    @Transactional
    public Result driverList(){
        log.info("所有司机信息");
        return ResultUtil.success(driverService.findAll());
    }

    @GetMapping(value = "/driver/onRoad")
    @Transactional
    public Result onRoadDrivers(){
        log.info("所有在路上状态的司机");
        return ResultUtil.success(driverService.findOnRoadDrivers());
    }

    @GetMapping(value = "/driver/Atrest")
    @Transactional
    public Result atRestDrivers(){
        log.info("所有休息状态的司机");
        return ResultUtil.success(driverService.findAtrestDrivers());
    }

    @GetMapping(value = "/driver/Available")
    @Transactional
    public Result availableDrivers(){
        log.info("所有司机信息");
        return ResultUtil.success(driverService.findAvailableDrivers());
    }

    //查询by num
    @GetMapping(value = "/driver/fbd/{dnum}")
    @Transactional
    public Result FindOneByDum(@PathVariable("dnum") String dnum){
        log.info("find driver by phone number,dnum={}",dnum);
        return ResultUtil.success(driverService.findOne(dnum));
    }

    //查询by name
    @GetMapping(value = "/driver/fbn/{name}")
    @Transactional
    public Result FindOneByName(@PathVariable("name") String name){
        log.info("find driver by name,name={}",name);
        return ResultUtil.success(driverService.findByDriverName(name));
    }

    //查询by carNum
    @GetMapping(value = "/driver/fbcN/{carNum}")
    @Transactional
    public Result FindOneByCarNum(@PathVariable("carNum") String carNum){
        log.info("find driver by carNum,carNum={}",carNum);
        return ResultUtil.success(driverService.findByCarNum(carNum));
    }


    //删除司机信息
    @DeleteMapping(value = "/driver/delete/{dnum}")
    @Transactional
    public Result driverDelete(@PathVariable("dnum") String dnum){
        log.info("delete a particular driver,dnum={}",dnum);
        return driverService.deleteOne(dnum);
    }
    //改司机


    //加司机


    /*用户相关*/
    //查询用户
    @GetMapping(value = "/user")
    @Transactional
    public Result userList(){
        log.info("所有用户信息");
        return ResultUtil.success(userService.findAll());
    }

    //查询by unum
    @GetMapping(value = "/user/fbu/{unum}")
    @Transactional
    public Result FindOneByUnum(@PathVariable("unum") String unum){
        log.info("find user by unum");
        return ResultUtil.success(userService.findOne(unum));
    }

    //查询by mobile
    @GetMapping(value = "/user/fbm/{mobile}")
    @Transactional
    public Result FindOneByMobile(@PathVariable("mobile") String mobile){
        log.info("find user by mobile");
        return ResultUtil.success(userService.findByMobile(mobile));
    }

    //查询by username
    @GetMapping(value = "/user/fbn/{username}")
    @Transactional
    public Result FindOneByUserName(@PathVariable("username") String username){
        log.info("find user by username");
        return ResultUtil.success(userService.findByUserName(username));
    }

    //查询by email
    @GetMapping(value = "/user/fbe/{email}")
    @Transactional
    public Result FindOneByStatus(@PathVariable("email") String email){
        log.info("find user by email");
        return ResultUtil.success(userService.findbyEmail(email));
    }


    //无法修改顾客信息?（后期加入会员或黑名单？）
    //删除用户信息
    @DeleteMapping(value = "/user/delete/{unum}")
    @Transactional
    public Result userDelete(@PathVariable("unum") String unum){
        log.info("delete a particular user,userMobile={}",userService.findOne(unum).getMobile());
        return userService.deleteOne(unum);
    }



    /*线路管理人相关*/

    //查询线路负责人
    @GetMapping("/manager")
    @Transactional
    public Result managerList(){
        log.info("所有线路管理人信息");
        return ResultUtil.success(managerService.findAll());
    }

    //增加线路负责人


    //删除对应线路负责人（慎重，会同时删除所有司机信息）


    //修改线路负责人信息




}
