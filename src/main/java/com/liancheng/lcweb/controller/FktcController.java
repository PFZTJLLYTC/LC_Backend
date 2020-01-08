package com.liancheng.lcweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
//@RequestMapping("/")
public class FktcController {


    @GetMapping("/")
    public ModelAndView fk(){
        return new ModelAndView("redirect:".concat("/myWebSite.html"));
    }


}
