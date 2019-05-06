package com.liancheng.lcweb.controller;
import com.liancheng.lcweb.constant.CookieConstant;
import com.liancheng.lcweb.constant.RedisConstant;
import com.liancheng.lcweb.converter.Driver2DriverDTOConverter;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.DriverDTO;
import com.liancheng.lcweb.dto.TotalInfoDTO;
import com.liancheng.lcweb.enums.DriverStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.ManagerException;
import com.liancheng.lcweb.form.DriverInfoForm;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.service.DriverService;
import com.liancheng.lcweb.service.ManagerService;
import com.liancheng.lcweb.service.OrderService;
import com.liancheng.lcweb.service.UserService;
import com.liancheng.lcweb.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.transaction.Transactional;
import javax.validation.Valid;
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
        if(manager == null){
            throw new ManagerException(ResultEnums.NO_SUCH_MANAGER.getMsg(),CookieConstant.EXPIRE_URL);

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
        map.put("url","/login.html");

        return new ModelAndView("common/success",map);
    }

    /*司机相关*/

    //查询司机

    @GetMapping(value = "/driver/allDrivers")
    public ModelAndView allDriver(HttpServletRequest request,Map<String,Object>map){

        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        log.info("获取lineId来查司机信息");
        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");

        log.info("lineId={}",lineId);

        List<DriverDTO> driverDTOList= managerService.getAllDrivers(lineId);
        if (driverDTOList == null){

            throw new ManagerException("请添加司机","manager/index");

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
        if (status.equals(DriverStatusEnums.ATREST.getCode())){
            return new ModelAndView("manager/atRestDrivers",map);
        }
        else if (status.equals(DriverStatusEnums.AVAILABLE.getCode())){
            return new ModelAndView("manager/availableDrivers",map);
        }
        else if (status.equals(DriverStatusEnums.ONROAD.getCode())){
            return new ModelAndView("manager/onRoadDrivers",map);
        }
        else{
            return new ModelAndView("manager/toBeVerifiedDrivers",map);
        }

    }


    //司机详情
    @GetMapping("/driver/driverDetail")
    @Transactional
    public ModelAndView updateDriverInfo(@RequestParam("dnum")String dnum,
                                         HttpServletRequest request,
                                         Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);
        log.info("获取lineId来查获取司机详情");
        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("lineId={}",lineId);

        Driver driver = driverService.findOne(dnum);
        DriverDTO driverDTO = Driver2DriverDTOConverter.convert(driver);
        map.put("name",lineId);
        map.put("driver",driverDTO);

        return new ModelAndView("manager/driverDetail",map);

    }

    //增加司机,这里是指通过manager直接添加，但是仍需要确认
    @PostMapping("/driver/DriverAdd")
    @Transactional
    public ModelAndView addOneDriver(@RequestBody @Valid DriverInfoForm driverInfoForm,
                                     BindingResult bindingResult,
                                     HttpServletRequest request,
                                     Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);
        log.info("获取lineId来添加司机");
        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("lineId={}",lineId);

        if (bindingResult.hasErrors()){
            //todo 后面统一搞个manager返回的枚举
            log.error("增加司机时填表有误");
            throw new ManagerException(bindingResult.getFieldError().getDefaultMessage(),"/manager/allDrivers");

        }
        managerService.AddOneDriver(driverInfoForm,lineId);
        log.info("线路{},添加司机{}成功",lineId,driverInfoForm.getName());
        map.put("msg","添加注册司机信息"+driverInfoForm.getName()+ResultEnums.SUCCESS.getMsg()+", 请确认相关司机完成操作！");
        map.put("url","/manager/driver/allDrivers");

        return new ModelAndView("common/success",map);


    }

    //删除司机信息(根据司机dnum删)
    @DeleteMapping("/driver/DriverDelete")
    @Transactional
    public ModelAndView deleteOneDriver(@RequestParam("dnum") String dnum,
                                        HttpServletRequest request,
                                        Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);
        log.info("获取lineId来删除司机");
        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("lineId={}",lineId);

        managerService.DeleteOneDriver(dnum,lineId);
        log.info("删除司机成功,line={},dnum={}",lineId,dnum);
        map.put("msg","删除司机" + dnum + ResultEnums.SUCCESS.getMsg());
        map.put("url","/manager/driver/allDrivers");

        return new ModelAndView("common/success",map);
    }

    //确认添加司机
    @PutMapping("/driver/confirmDriver")
    @Transactional
    public ModelAndView confirmOneDriver(@RequestParam("dnum")String dnum,
                                         HttpServletRequest request,
                                         Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);
        log.info("获取lineId来同意司机注册");
        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("lineId={}",lineId);

        managerService.confirmOneDriver(dnum,lineId);
        log.info("确认添加司机成功,line={},dnum={}",lineId,dnum);

        map.put("msg","确认司机" + dnum + ResultEnums.SUCCESS.getMsg());
        map.put("url","/manager/driver/findByStatus?status="+DriverStatusEnums.TO_BE_VERIFIED.getCode());

        return new ModelAndView("common/success",map);

    }




    /*订单相关*/


    //查看所有订单
    @GetMapping("/order/allOrders")
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

    //查看不同状态订单
    @GetMapping("/order/findBysStatus")
    public ModelAndView getOrdersByStatus(@RequestParam("status") String status,
                                          HttpServletRequest request,
                                          Map<String,Object> map){
        //todo
        return new ModelAndView("manager/",map);
    }



    //确认订单
    @PutMapping("/order/confirm")
    @Transactional
    public ModelAndView confirmOrder(@RequestParam("orderId") String orderId,
                                     @RequestParam("dnum") String dnum,
                                     HttpServletRequest request,
                                     Map<String,Object> map){

        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        log.info("获取lineId来确认订单信息");
        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("lineId={}",lineId);

        Order order = orderService.findOne(orderId);
        if (order == null){
            throw new ManagerException(ResultEnums.ORDER_NOT_FOUND.getMsg(),"/manager/order/allOrders");

        }
        //加一个选择司机的按钮,然后传dnum，弹出确定窗口，确定后调用confirmOrder这个方法.
        managerService.confirmOneOrder(order,dnum);

        map.put("msg",ResultEnums.SUCCESS.getMsg());
        map.put("url","/manager/order/allOrders");
        return new ModelAndView("common/success",map);

    }


}
