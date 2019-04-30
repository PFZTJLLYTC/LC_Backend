package com.liancheng.lcweb.controller;


import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.service.DriverService;
import com.liancheng.lcweb.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Date;

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
    public ResultVO userLogin(@RequestBody Driver driver){
        Driver result = driverService.getByMobileAndPassword(driver.getDnum(),driver.getPassword());
        if(result!=null){
            log.info("司机登陆成功，dnum={}",result.getDnum());
            return ResultVOUtil.success(result);
        }
        else{
            log.error("无此司机信息，dnum={}",driver.getDnum());
            return ResultVOUtil.error();
        }

    }


    //注册
    @PostMapping(value = "/drivers/add")//加表单验证
    public ResultVO driverAdd(@RequestBody@Valid Driver driver, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return null;
        }
        driver.setDnum(driver.getDnum());

        driver.setBirthday(driver.getBirthday());
        driver.setCarNum(driver.getCarNum());
        driver.setStatus(0);
        driver.setPassword("123456");//初始化密码123456
        driver.setName(driver.getName());
        //如何让一个管理员只能看见自己的呢？
        driver.setLine(driver.getLine());

        log.info("add a new driver");
        return ResultVOUtil.success(driverRepository.save(driver));
    }



    //根据id更新driver信息
    //也可以提出来改成单独修改一项,eg:修改状态
    @PutMapping(value = "/driver/update/{id}")
    public ResultVO driverUpdate(@PathVariable("id") String id,
                                 @RequestParam("name") String name,
                                 @RequestParam("password") String password,
                                 @RequestParam("carNum") String carNum,
                                 @RequestParam("line") String line,
                                 @RequestParam("dNum") String dNum,
                                 @RequestParam("birthday") Date birthday,
                                 @RequestParam("status") Integer status){
        Driver driver = new Driver();
        driver.setDnum(dNum);

        driver.setName(name);
        driver.setPassword(password);
        driver.setCarNum(carNum);
        driver.setLine(line);
        driver.setBirthday(birthday);
        driver.setStatus(status);
        log.info("update one driver's info");
        return ResultVOUtil.success(driverRepository.save(driver));
    }






    /*订单相关*/

    //查看订单

    //订单通知



}
