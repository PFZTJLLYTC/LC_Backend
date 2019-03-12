package com.liancheng.lcweb.controller;


import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class ManagerController {

    //只实现了登陆，注册需要更高级后台进行
    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private ManagerService managerService;

    @GetMapping(value = "/")
    public String loginIndex(){
        return "login";
    }

    //login
    @PostMapping(value = "/login")
    public String getInfo(@RequestParam("name") String name,
                          @RequestParam("password") String password,
                          HttpSession session){ //RedirectAttributes attributes
        Manager manager = managerService.getManager(name,password);
        if(manager!=null){
            manager.setPassword(null);
            session.setAttribute("manager",manager);
            return "index1";
        }
        else{
            //attributes.addAttribute("message","用户名或密码错误");//?未出现
            return "login";
        }
    }


}
