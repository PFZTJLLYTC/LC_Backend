package com.liancheng.lcweb.controller;
import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.constant.CookieConstant;
import com.liancheng.lcweb.constant.RedisConstant;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.DriverDTO;
import com.liancheng.lcweb.dto.TotalInfoDTO;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.service.DriverService;
import com.liancheng.lcweb.service.ManagerService;
import com.liancheng.lcweb.service.OrderService;
import com.liancheng.lcweb.service.UserService;
import com.liancheng.lcweb.utils.CookieUtil;
import com.liancheng.lcweb.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller//之后都改为ModelAndView
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

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;


    //login
    //考虑安全性，跳转一个方法来进index
    @GetMapping(value = "/login")
    public ModelAndView login(@RequestParam("lineId") Integer lineId,
                              @RequestParam("password") String password,
                              HttpServletRequest request,
                              HttpServletResponse response,
                              Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);
        if (cookie != null && !StringUtils.isEmpty(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue())))) {

            TotalInfoDTO totalInfoDTOS = managerService.getTotal(lineId);
            map.put("name",lineId);
            map.put("total",totalInfoDTOS);
            return new ModelAndView("/manager/index",map);
        }

        Manager manager = managerService.getManager(lineId,password);
        if(manager ==null){
            map.put("msg","用户名或密码错误");
            map.put("url","http://127.0.0.1:8080/lc/login.html");
            return new  ModelAndView("common/error",map);
        }

        //2.存userId信息
        String managerId = manager.getLineId().toString();

        //3.设置token到redis
        String token = UUID.randomUUID().toString();
        Integer expire = RedisConstant.EXPIRE;//其实也相当于cookieconstant.expire
        //加个前缀显得高端
        redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX,token),managerId,expire, TimeUnit.SECONDS);

        //4. 设置token到cookie
        CookieUtil.set(response, CookieConstant.TOKEN,token, expire);
        TotalInfoDTO totalInfoDTOS = managerService.getTotal(lineId);

        map.put("name",manager.getName());
        map.put("total",totalInfoDTOS);
        return new ModelAndView("manager/index",map);
    }

    //logout
    @GetMapping("/logout")
    public ModelAndView logout(HttpServletRequest request,
                           HttpServletResponse response,
                               Map<String,Object> map){

        //1.查名字为token的cookie
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);
        if (cookie!=null){
            //删除redis
            redisTemplate.opsForValue().getOperations().delete(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()));

            //删除cookie
            CookieUtil.set(response,CookieConstant.TOKEN,null, 0);
        }
        map.put("msg",ResultEnums.LOG_OUT_SUCCESS.getMsg());
        map.put("url","http://127.0.0.1:8080/lc/login.html");

        return new ModelAndView("common/success",map);
    }

    /*司机相关*/

    //查询司机

    @GetMapping(value = "/driver")
    public ModelAndView allDriver(HttpServletRequest request,Map<String,Object>map){

        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        log.info("获取lineId来查司机信息");
        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");

        log.info("lineId={}",lineId);

        List<DriverDTO> driverDTOList= managerService.getAllDrivers(lineId);
        if (driverDTOList == null){
            map.put("msg",ResultEnums.NO_SUCH_USER.getMsg());
            map.put("url","manager/index");
            return new ModelAndView("common/error",map);
        }

        Manager manager = managerService.findOne(lineId);
        map.put("drivers",driverDTOList);
        map.put("manager",manager.getName());

        return new ModelAndView("manager/drivers",map);
    }

    //查询不同状态司机
    @GetMapping("/driver/findByStatus")
    public ModelAndView getDriversByStatus(@RequestParam("status") Integer status,HttpServletRequest request,Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        log.info("获取lineId来查不同状态司机信息");
        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("lineId={}",lineId);
        List<DriverDTO> driverDTOList = managerService.getDriversByStatus(lineId,status);

        //todo 暂时不分页，只把全部搞出来
        map.put("drivers",driverDTOList);

        return new ModelAndView("manager/drivers",map);

    }


    //增加司机（批量）


    //修改司机

    //删除信息



    /*订单相关*/


    //查看所有订单
    @GetMapping("/order/orders")
    public ModelAndView allOrders(HttpServletRequest request,Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        log.info("获取lineId来查当前线路所有订单信息");
        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("lineId={}",lineId);

        List<Order> orders = managerService.getAllOrders(lineId);
        map.put("orderList",orders);
        map.put("name",lineId);
        return new ModelAndView("manager/alldeals",map);
    }


    //确认订单
    @PutMapping("/order/confirm")
    @Transactional
    public ModelAndView confirmOrder(@RequestParam("orderId") String orderId,
                                     HttpServletRequest request,
                                     Map<String,Object> map){

        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        log.info("获取lineId来确认订单信息");
        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("lineId={}",lineId);

        Order order = orderService.findOne(orderId);
        if (order == null){
            log.error("无此订单");
            map.put("msg",ResultEnums.ORDER_NOT_FOUND.getMsg());
            map.put("url","/lc/manager/orders");

            return new ModelAndView("manager/alldeals",map);
//            throw new LcException(ResultEnums.ORDER_NOT_FOUND);

        }
        orderService.confirmOne(order);
        map.put("msg",ResultEnums.SUCCESS.getMsg());
        map.put("url","/lc/manager/alldeals");
        return new ModelAndView("common/success",map);

    }





}
