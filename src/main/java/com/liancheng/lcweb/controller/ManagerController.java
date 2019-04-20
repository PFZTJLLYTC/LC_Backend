package com.liancheng.lcweb.controller;
import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.constant.CookieConstant;
import com.liancheng.lcweb.constant.RedisConstant;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.DriverDTO;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.transaction.Transactional;
import java.util.List;
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
    @GetMapping(value = "/login")
    public ResultVO login(@RequestParam("name") String name,
                          @RequestParam("password") String password,
                          HttpServletRequest request,
                          HttpServletResponse response){

        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);
        if (cookie != null && !StringUtils.isEmpty(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue())))) {

            return ResultVOUtil.success();
        }

        Manager manager = managerService.getManager(name,password);

        //2.存userId信息
        String managerId = manager.getLineId().toString();

        //3.设置token到redis
        String token = UUID.randomUUID().toString();
        Integer expire = RedisConstant.EXPIRE;//其实也相当于cookieconstant.expire
        //加个前缀显得高端
        redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX,token),managerId,expire, TimeUnit.SECONDS);

        //4. 设置token到cookie
        CookieUtil.set(response, CookieConstant.TOKEN,token, expire);

        return ResultVOUtil.success();
    }

    //logout
    @GetMapping("/logout")
    public ResultVO logout(HttpServletRequest request,
                           HttpServletResponse response){

        //1.查名字为token的cookie
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);
        if (cookie!=null){
            //删除redis
            redisTemplate.opsForValue().getOperations().delete(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()));

            //删除cookie
            CookieUtil.set(response,CookieConstant.TOKEN,null, 0);
        }

        return ResultVOUtil.success();
    }

    /*司机相关*/

    //查询司机

    @GetMapping(value = "/driver")
    public ResultVO allDriver(HttpServletRequest request){

        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        log.info("获取lineId来查司机信息");
        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");

        log.info("lineId={}",lineId);
        return ResultVOUtil.success(driverService.findbyLineId(lineId));
    }

    //查询不同状态司机
    @GetMapping("/driver/findByStatus")
    public ResultVO getDriversByStatus(@RequestParam("status") Integer status,HttpServletRequest request){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        log.info("获取lineId来查不同状态司机信息");
        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("lineId={}",lineId);

        return ResultVOUtil.success(managerService.getDriversByStatus(lineId,status));

    }


    //增加司机（批量）

    //修改司机

    //删除信息



    /*订单相关*/

    //确认订单
    @PutMapping("/order/confirm")
    @Transactional
    public ResultVO confirmOrder(@RequestParam("orderId") String orderId){

        Order order = orderService.findOne(orderId);
        if (order == null){
            log.error("无此订单");
            throw new LcException(ResultEnums.ORDER_NOT_FOUND);
        }
        return ResultVOUtil.success(orderService.confirmOne(order));
    }



    //查看订单



}
