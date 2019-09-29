package com.liancheng.lcweb.controller;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.form.ApplicationForm;
import com.liancheng.lcweb.service.ApplicationService;
import com.liancheng.lcweb.service.ManagerService;
import com.liancheng.lcweb.service.impl.ManagerServiceImpl;
import com.liancheng.lcweb.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@Slf4j
@RequestMapping("/root")
@RestController
public class RootController {
    @Autowired
    private ManagerServiceImpl managerService;

    @Autowired
    private ApplicationService applicationService;


    //增加线路负责人申请
    @PostMapping("/manager/add")
    @Transactional
    public ResultVO newManagerWill(@RequestBody @Valid ApplicationForm applicationForm, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            log.error("线路申请表格填写错误");
            log.error(bindingResult.getFieldError().getDefaultMessage());
            //具体原因就不写了
            return ResultVOUtil.error();
        }
        return ResultVOUtil.success(applicationService.applyFor(applicationForm));
    }

    //转换



    /*司机相关*/
    //查司机
//    @GetMapping(value = "/driver")
//    @Transactional
//    public ResultVO driverList(){
//        log.info("所有司机信息");
//        return ResultVOUtil.success(driverService.findAll());
//    }
//
//    @GetMapping(value = "/driver/onRoad")
//    @Transactional
//    public ResultVO onRoadDrivers(){
//        log.info("所有在路上状态的司机");
//        return ResultVOUtil.success(driverService.findOnRoadDrivers());
//    }
//
//    @GetMapping(value = "/driver/Atrest")
//    @Transactional
//    public ResultVO atRestDrivers(){
//        log.info("所有休息状态的司机");
//        return ResultVOUtil.success(driverService.findAtrestDrivers());
//    }
//
//    @GetMapping(value = "/driver/Available")
//    @Transactional
//    public ResultVO availableDrivers(){
//        log.info("所有司机信息");
//        return ResultVOUtil.success(driverService.findAvailableDrivers());
//    }
//
//    //查询by num
//    @GetMapping(value = "/driver/fbd/{dnum}")
//    @Transactional
//    public ResultVO FindOneByDum(@PathVariable("dnum") String dnum){
//        log.info("find driver by phone number,dnum={}",dnum);
//        return ResultVOUtil.success(driverService.findOne(dnum));
//    }
//
//    //查询by name
//    @GetMapping(value = "/driver/fbn/{name}")
//    @Transactional
//    public ResultVO FindOneByName(@PathVariable("name") String name){
//        log.info("find driver by name,name={}",name);
//        return ResultVOUtil.success(driverService.findByDriverName(name));
//    }

    //查询by carNum
//    @GetMapping(value = "/driver/fbcN")
//    public ResultVO FindOneByCarNum(@RequestParam("carNum") String carNum){
//        log.info("find driver by carNum,carNum={}",carNum);
//        return ResultVOUtil.success(driverService.findByCarNum(carNum));
//    }


    //删除司机信息
//    @DeleteMapping(value = "/driver/delete/{dnum}")
//    @Transactional
//    public ResultVO driverDelete(@PathVariable("dnum") String dnum){
//        log.info("delete a particular driver,dnum={}",dnum);
//        return driverService.deleteOne(dnum);
//    }
    //改司机


    //加司机


    /*用户相关*/
    //查询用户
//    @GetMapping(value = "/user")
//    public ResultVO userList(){
//        log.info("所有用户信息");
//        return ResultVOUtil.success(userService.findAll());
//    }

    //查询by unum
//    @GetMapping(value = "/user/fbu/{unum}")
//    public ResultVO FindOneByUnum(@PathVariable("unum") String unum){
//        log.info("find user by unum");
//        return ResultVOUtil.success(userService.findOne(unum));
//    }

    //查询by mobile
//    @GetMapping(value = "/user/fbm/{mobile}")
//    public ResultVO FindOneByMobile(@PathVariable("mobile") String mobile){
//        log.info("find user by mobile");
//        return ResultVOUtil.success(userService.findByMobile(mobile));
//    }

    //查询by username
//    @GetMapping(value = "/user/fbn/{username}")
//    public ResultVO FindOneByUserName(@PathVariable("username") String username){
//        log.info("find user by username");
//        return ResultVOUtil.success(userService.findByUserName(username));
//    }


    //删除用户信息
//    @DeleteMapping(value = "/user/delete/{unum}")
//    @Transactional
//    public ResultVO userDelete(@PathVariable("unum") String unum){
//        log.info("delete a particular user,userMobile={}",userService.findOne(unum).getMobile());
//        userService.deleteOne(unum);
//        return ResultVOUtil.success();
//    }



    /*线路管理人相关*/

    //查询线路负责人
//    @GetMapping("/manager")
//    @Transactional
//    public ResultVO managerList(){
//        log.info("所有线路管理人信息");
//        return ResultVOUtil.success(managerService.findAll());
//    }


    //删除对应线路负责人（慎重，会同时删除所有司机信息）
//    @DeleteMapping("/manager/delete")
//    @Transactional
//    public ResultVO deleteManager(@RequestParam("telNum") String telNum){
//        managerService.deleteOne(telNum);
//        return ResultVOUtil.success();
//    }


    //修改线路负责人信息




}
