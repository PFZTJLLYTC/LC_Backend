package com.liancheng.lcweb.controller;

import com.liancheng.lcweb.VO.Result;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.domain.User;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.repository.UserRepository;
import com.liancheng.lcweb.service.ManagerService;
import com.liancheng.lcweb.service.OrderService;
import com.liancheng.lcweb.service.UserService;
import com.liancheng.lcweb.utils.KeyUtil;
import com.liancheng.lcweb.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;


    //增加信息、注册
    @PostMapping(value = "/signUp")//加表单验证的话新加dto层
    public Result userAdd(@RequestBody User user){
        user.setUnum(KeyUtil.genUniquekey());
//        user.setUsername(user.getUsername());
        user.setPassword(user.getPassword());
        user.setMobile(user.getMobile());
//        user.setEmail(user.getEmail());
//        user.setEmailVerifiled(user.getEmailVerifiled());
        log.info("add a new user，userMobile={}",user.getMobile());
        return ResultUtil.success(userService.addUser(user));
    }



    //根据unum更新user信息
    //也可以提出来改成单独修改一项,应该会拆开比较好
    @PutMapping(value = "/update/{unum}")
    public Result userUpdate(@PathVariable("unum") String unum,
                             @RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("mobile") String mobile,
                             @RequestParam("email") String email,
                             @RequestParam("emailVerified") Boolean emailVerified,
                             @RequestParam("updateAt") Date updateAt){
        //todo 需要改
        User user = userService.findOne(unum);
        user.setUnum(unum);
        user.setUsername(username);
        user.setPassword(password);
        user.setMobile(mobile);
        user.setEmail(email);
        user.setEmailVerifiled(emailVerified);
        log.info("update one user's info，user={}",username);
        return ResultUtil.success(userRepository.save(user));
    }

    @PostMapping(value = "/login")
    @Transactional
    public Result userLogin(@RequestBody User user){
        User result = userService.userLogin(user);
        if(result!=null)
            return ResultUtil.success(result);
        else
            return ResultUtil.error();
    }


    /*订单相关*/

    //创建订单
    @PostMapping("/createOrder")
    @Transactional
    public Result createOrder(@RequestBody@Valid Order order,BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            log.error("订单填写信息不合法");
            throw new LcException(ResultEnums.ORDER_INFO_ERROR);
        }
        //todo 交给相应manager操作,应该被同步通知

        return ResultUtil.success(orderService.createOne(order));
    }

    //查看未处理订单

    //取消订单，只有当订单未被confirm才可以

    //查看进行中订单


    //查看已完成订单
}
