package com.liancheng.lcweb.controller;

import com.liancheng.lcweb.VO.Result;
import com.liancheng.lcweb.domain.User;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.repository.UserRepository;
import com.liancheng.lcweb.service.ManagerService;
import com.liancheng.lcweb.service.UserService;
import com.liancheng.lcweb.utils.KeyUtil;
import com.liancheng.lcweb.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    //展现all
    @GetMapping(value = "/user")
    @Transactional
    public Result userList(){
        log.info("userList");
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


    //删除用户信息
    @DeleteMapping(value = "/user/delete/{unum}")
    @Transactional
    public Result userDelete(@PathVariable("unum") String unum){
        log.info("delete a particular user");
        userRepository.deleteById(unum);
        return ResultUtil.success();
    }


    //增加信息
    @PostMapping(value = "/user")//加表单验证
    public Result userAdd(@RequestBody User user){
        user.setUnum(KeyUtil.genUniquekey());
//        user.setUsername(user.getUsername());
        user.setPassword(user.getPassword());
        user.setMobile(user.getMobile());
//        user.setEmail(user.getEmail());
//        user.setEmailVerifiled(user.getEmailVerifiled());
        log.info("add a new user");
        return ResultUtil.success(userService.addUser(user));

    }



    //根据unum更新user信息
    //也可以提出来改成单独修改一项
    @PutMapping(value = "/user/update/{unum}")
    public Result userUpdate(@PathVariable("unum") String unum,
                             @RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("mobile") String mobile,
                             @RequestParam("email") String email,
                             @RequestParam("emailVerified") Boolean emailVerified,
                             @RequestParam("updateAt") Date updateAt){
        User user = new User();
        user.setUnum(unum);
        user.setUsername(username);
        user.setPassword(password);
        user.setMobile(mobile);
        user.setEmail(email);
        user.setEmailVerifiled(emailVerified);
        log.info("update one user's info");
        return ResultUtil.success(userRepository.save(user));
    }

    @PostMapping(value = "/user/login")
    public Result userLogin(@RequestParam User user){
        User result = userService.userLogin(user);
        if(result!=null)
            return ResultUtil.success(result);
        else
            return ResultUtil.error();
    }
}
