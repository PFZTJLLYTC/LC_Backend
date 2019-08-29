package com.liancheng.lcweb.controller;


import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.DriverDoneOrderDTO;
import com.liancheng.lcweb.dto.OrderDriDTO;
import com.liancheng.lcweb.enums.OrderStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.form.ChangePasswordForm;
import com.liancheng.lcweb.form.DriverInfoForm;
import com.liancheng.lcweb.form.DriverLoginForm;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.service.*;
import com.liancheng.lcweb.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.http.WebSocket;
import java.util.Date;
import java.util.List;

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

    @Autowired
    private OrderService orderService;

    @Autowired
    private LineService lineService;

    @Autowired
    private MessagesService messagesService;


    /** 身份验证 **/

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


    @GetMapping(value = "/checkLogin")
    public ResultVO driverCheckLogin(@RequestParam String dnum){
        return ResultVOUtil.success(driverService.checkLogin(dnum));
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

        return ResultVOUtil.success(driverService.driverLogin(driverLoginForm));//登陆成功回传手机号码给前台缓存

    }

    @PostMapping(value = "/changePassword")
    @Transactional
    public ResultVO changePassword(@Valid @RequestBody ChangePasswordForm form,BindingResult result ){
        if (result.hasErrors()){
            String msg = result.getFieldError().getDefaultMessage();
            log.error("修改密码错误"+msg);
            throw new LcException(ResultEnums.USER_CHANGE_FORM_ERROR);
        }
        driverService.changePassword(form);
        return ResultVOUtil.success();
    }


    /**操作 **/

    //根据id更新driver信息
    //也可以提出来改成单独修改一项,eg:修改状态
    @PostMapping(value = "/update/{id}")
    public ResultVO driverUpdate(@PathVariable("id") String id,
                                 @RequestParam("name") String name,
                                 @RequestParam("password") String password,
                                 @RequestParam("carNum") String carNum,
                                 @RequestParam("dNum") String dNum,
                                 @RequestParam("birthday") Date birthday,
                                 @RequestParam("status") Integer status){
        Driver driver = new Driver();
        driver.setDnum(dNum);

        driver.setName(name);
        driver.setPassword(password);
        driver.setCarNum(carNum);
        driver.setBirthday(birthday);
        driver.setStatus(status);
        log.info("update one driver's info");
        return ResultVOUtil.success(driverRepository.save(driver));
    }

    //单改一个状态
    @PostMapping(value = "/switchStatus")
    @Transactional
    //因为每一次不知道在哪一边，结束订单后，手动切换状态,且也给缓冲的时间
    public ResultVO switchStatus(@RequestParam("dnum") String dnum,
                                 @RequestParam("status") Integer status){


        driverService.switchStatus(status,driverService.findOne(dnum));

        return ResultVOUtil.success();
    }

    @PostMapping("/changeAvailableSeats")
    @Transactional
    //满足可以自由接单后改变座位数量
    public ResultVO changeAvailableSeats(@RequestParam("dnum") String dnum,
                                         @RequestParam("seats") Integer seats){

        driverService.switchAvailableSeats(seats,driverService.findOne(dnum));

        return ResultVOUtil.success();
    }

    @PostMapping("/order/finishOne")
    @Transactional
    //一个一个的点完成！
    public ResultVO finishOneOrder(@RequestParam("dnum") String dnum,
                                   @RequestParam("orderId") String orderId){
        Order order = orderService.findOne(orderId);
        if (order==null){
            log.error("订单未找到");
            return ResultVOUtil.error(ResultEnums.ORDER_NOT_FOUND);
        }
        else if (!order.getDnum().equals(dnum)){
            log.error("司机无此订单");
            return ResultVOUtil.error(ResultEnums.ORDER_NOT_FOUND);
        }
        else {
            orderService.finishOne(order);
        }
        return ResultVOUtil.success(orderId);
    }

                /******** 订单信息展示相关 *********/

    //查看订单
    @GetMapping("/orders/processin")
    public ResultVO findProcessinOrder(@RequestParam String dnum){
        return ResultVOUtil.success(orderService.findDriverOrderByStatus(
                OrderStatusEnums.PROCESSIN.getCode(),dnum));
    }
    @GetMapping("/orders/done")
    public ResultVO findDoneOrder(@RequestParam String dnum){
        return ResultVOUtil.success(orderService.findDriverOrderByStatus(
                OrderStatusEnums.DONE.getCode(),dnum));
    }

    //
    @GetMapping("/messages/findMessages")
    public ResultVO findMessages(@RequestParam String dnum){
        return ResultVOUtil.success(messagesService.findByTarget(dnum));
    }

    @PostMapping("/messages/delete/{id}")
    public ResultVO deleteOneMessage(@PathVariable Integer id){
        messagesService.deleteMessage(id);
        return ResultVOUtil.success();
    }

    @GetMapping("/accountInfo/{dnum}")
    public ResultVO findAccountInfo(@PathVariable String dnum){
        return ResultVOUtil.success(driverService.findAccountInfo(dnum));
    }


    @GetMapping("/lines/all")
    public ResultVO findAllLine(){
        return ResultVOUtil.success(lineService.findAllSelectLine());
    }

}
