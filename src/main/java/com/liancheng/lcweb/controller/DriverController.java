package com.liancheng.lcweb.controller;


import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.form.DriverInfoForm;
import com.liancheng.lcweb.form.DriverLoginForm;
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

    //注册
    @PostMapping(value = "/register")//加表单验证
    public ResultVO driverAdd(@RequestBody@Valid DriverInfoForm driverInfoForm,
                              BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            log.error("注册信息错误");
            return ResultVOUtil.error(ResultEnums.DRIVER_REGISTER_FORM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        driverService.addDriver(driverInfoForm);

        log.info("add a new driver，driver={}",driverInfoForm);
        //跳转到登陆界面
        return ResultVOUtil.success();

    }

    //登陆
    @PostMapping(value = "/login")
    @Transactional
    public ResultVO driverLogin(@RequestBody@Valid DriverLoginForm driverLoginForm,
                                BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("司机登入信息错误");
            return ResultVOUtil.error(ResultEnums.DRIVER_LOGIN_FORM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        Driver result = driverService.getByMobileAndPassword(driverLoginForm.getDnum(),
                driverLoginForm.getPassword());
        if(result!=null){
            log.info("司机登陆成功，dnum={}",result.getDnum());
            return ResultVOUtil.success(result);
        }
        else{
            log.error("无此司机信息，dnum={}",driverLoginForm.getDnum());
            return ResultVOUtil.error();
        }
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
