package com.liancheng.lcweb.controller;
import com.liancheng.lcweb.VO.Result;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.service.DriverService;
import com.liancheng.lcweb.service.ManagerService;
import com.liancheng.lcweb.service.UserService;
import com.liancheng.lcweb.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/manager")
@Slf4j
public class ManagerController {

    //只实现了登陆，注册需要更高级后台进行
    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private UserService userService;


    //login
    @PostMapping(value = "/login")
    public Result login(@RequestParam("name") String name,
                          @RequestParam("password") String password,
                          HttpSession session){ //RedirectAttributes attributes
        Manager manager = managerService.getManager(name,password);
        if (manager!=null){
            log.info("线路管理员登陆成功，lineId={}",name);
            return ResultUtil.success(manager);
        }
        else {
            log.error("无匹配管理员,lineId={}",name);
            return ResultUtil.error();
        }
        /*if(manager!=null){
            manager.setPassword(null);
            session.setAttribute("manager",manager);
            return "index";
        }
        else{
            //attributes.addAttribute("message","用户名或密码错误");//?未出现
            return "login";
        }*/
    }

    /*司机相关*/

    //查询司机

    @GetMapping(value = "/driver")
    @Transactional
    public Result allDriver(@RequestParam("lineId") String lineId){
        //todo not safe
        log.info("查询线路所有司机信息,lineId={}",lineId);
        return ResultUtil.success(driverService.findbyLineId(lineId));
    }

    @GetMapping(value = "/driver/onRoad")
    @Transactional
    public Result onRoadDrivers(@RequestParam("lineId")String lineId){
        log.info("查出此线路在路上状态的司机,lineId={}",lineId);
        //todo not safe
        return ResultUtil.success(driverService.certainLIneOnroad(lineId));
    }

    @GetMapping(value = "/driver/Atrest")
    @Transactional
    public Result atRestDrivers(@RequestParam("lineId")String lineId){
        log.info("查出此线路所有休息状态的司机,lineId={}",lineId);
        //todo not safe
        return ResultUtil.success(driverService.certainLIneAtrest(lineId));
    }

    @GetMapping(value = "/driver/Available")
    @Transactional
    public Result availableDrivers(@RequestParam("lineId")String lineId){
        log.info("查出此线路所有可用司机,lineId={}",lineId);
        //todo not safe
        return ResultUtil.success(driverService.certainLIneAvailable(lineId));
    }
    //增加司机（批量）

    //修改司机

    //删除信息




}
