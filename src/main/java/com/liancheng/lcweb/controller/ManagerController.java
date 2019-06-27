package com.liancheng.lcweb.controller;
import com.liancheng.lcweb.constant.CookieConstant;
import com.liancheng.lcweb.constant.RedisConstant;
import com.liancheng.lcweb.converter.Driver2DriverDTOConverter;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.DriverDTO;
import com.liancheng.lcweb.dto.MessageNumDTO;
import com.liancheng.lcweb.dto.TotalInfoDTO;
import com.liancheng.lcweb.enums.DriverStatusEnums;
import com.liancheng.lcweb.enums.OrderStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.ManagerException;
import com.liancheng.lcweb.form.Message2DriverForm;
import com.liancheng.lcweb.form.addDriverFormForManager;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.service.*;
import com.liancheng.lcweb.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private LineService lineService;


    //login
    //考虑安全性，跳转一个方法来进index,改成post！
    @GetMapping(value = "/login")
    public ModelAndView login(@RequestParam("telNum") String telNum,
                              @RequestParam("password") String password,
                              HttpServletRequest request,
                              HttpServletResponse response,
                              Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);


        if (cookie != null && !StringUtils.isEmpty(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue())))) {

            Integer lineId = managerService.findOne(telNum).getLineId();
            MessageNumDTO messageNum = managerService.getMessages(lineId);
            TotalInfoDTO totalInfoDTOS = managerService.getTotal(lineId);
            map.put("name",lineId);
            map.put("total",totalInfoDTOS);
            map.put("orderMessages",messageNum.getOrderMessages());
            map.put("driverMessages",messageNum.getDriverMessages());
            map.put("allMessages",messageNum.getAllMessages());
            return new ModelAndView("/manager/index",map);
        }

        Manager manager = managerService.getManager(telNum,password);

        if(manager == null){
            throw new ManagerException(ResultEnums.NO_SUCH_MANAGER.getMsg(),CookieConstant.EXPIRE_URL);

        }
        Integer lineId = manager.getLineId();

        //2.存管理员的tel信息or lineId?
        String managerId = manager.getLineId().toString();

        //3.设置token到redis
        String token = UUID.randomUUID().toString();
        Integer expire = RedisConstant.EXPIRE;//其实也相当于cookieconstant.expire
        //加个前缀显得高端
        redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX,token),managerId,expire, TimeUnit.SECONDS);

        //4. 设置token到cookie
        CookieUtil.set(response, CookieConstant.TOKEN,token, expire);

        MessageNumDTO messageNum = managerService.getMessages(lineId);

        TotalInfoDTO totalInfoDTOS = managerService.getTotal(lineId);

        map.put("orderMessages",messageNum.getOrderMessages());
        map.put("driverMessages",messageNum.getDriverMessages());
        map.put("name",lineId);
        map.put("allMessages",messageNum.getAllMessages());
        map.put("total",totalInfoDTOS);
        return new ModelAndView("manager/index",map);
    }

    //专供页面返回首页使用
    @GetMapping("/goIndex")
    public ModelAndView goToIndex(HttpServletRequest request,
                                  Map<String,Object>map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);
        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");

        TotalInfoDTO totalInfoDTOS = managerService.getTotal(lineId);

        MessageNumDTO messageNum = managerService.getMessages(lineId);

        map.put("orderMessages",messageNum.getOrderMessages());
        map.put("driverMessages",messageNum.getDriverMessages());
        map.put("allMessages",messageNum.getAllMessages());
        map.put("name",lineId);
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
//        map.put("msg",ResultEnums.LOG_OUT_SUCCESS.getMsg());
//        map.put("url","/login.html");
//
//        return new ModelAndView("common/success",map);
        return new ModelAndView("redirect:".concat("/login.html"));
    }

    /*司机相关*/

    //查询司机

    @GetMapping(value = "/driver/allDrivers")
    public ModelAndView allDriver(@RequestParam(value = "page",defaultValue = "1") Integer page ,
                                  @RequestParam(value = "size",defaultValue = "10") Integer size,
                                  HttpServletRequest request,
                                  Map<String,Object>map){

        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");

        log.info("获取lineId来查司机信息，lineId={}",lineId);

        //原来是从第0页开始的，现在想从第一页开始
        PageRequest pageRequest = new PageRequest(page-1,size);

        Page<DriverDTO> driverDTOPage= managerService.getAllDrivers(lineId,pageRequest);
        if (driverDTOPage == null){

            throw new ManagerException("请添加司机","manager/index");

        }
        MessageNumDTO messageNum = managerService.getMessages(lineId);

        map.put("drivers",driverDTOPage);
        map.put("orderMessages",messageNum.getOrderMessages());
        map.put("driverMessages",messageNum.getDriverMessages());
        map.put("allMessages",messageNum.getAllMessages());
        map.put("name",lineId);
        map.put("currentPage",page);
        map.put("size",size);

        return new ModelAndView("manager/allDrivers",map);
    }

    //查询不同状态司机
    @GetMapping("/driver/findByStatus")
    public ModelAndView getDriversByStatus(@RequestParam("status") Integer status,
                                           @RequestParam(value = "page",defaultValue = "1") Integer page ,
                                           @RequestParam(value = "size",defaultValue = "10") Integer size,
                                           HttpServletRequest request,Map<String,Object> map){

        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("获取lineId来查不同状态司机信息，lineId={}",lineId);

        PageRequest pageRequest = new PageRequest(page-1,size);

        Page<DriverDTO> driverDTOPage = managerService.getDriversByStatus(lineId,status,pageRequest);

        MessageNumDTO messageNum = managerService.getMessages(lineId);

        map.put("drivers",driverDTOPage);
        map.put("name",lineId);
        map.put("orderMessages",messageNum.getOrderMessages());
        map.put("driverMessages",messageNum.getDriverMessages());
        map.put("allMessages",messageNum.getAllMessages());
        map.put("currentPage",page);
        map.put("size",size);

        if (status.equals(DriverStatusEnums.ATREST.getCode())){
            return new ModelAndView("manager/atRestDrivers",map);
        }
        else if (status.equals(DriverStatusEnums.AVAILABLE.getCode())){
            return new ModelAndView("manager/availableDriver",map);
        }
        else if (status.equals(DriverStatusEnums.ONROAD.getCode())){
            return new ModelAndView("manager/onRoadDrivers",map);
        }
        else{
            return new ModelAndView("manager/toBeVerifiedDrivers",map);
        }

    }


    //司机详情.目前好像没有用到
    @GetMapping("/driver/driverDetail")
    @Transactional
    public ModelAndView updateDriverInfo(@RequestParam("dnum")String dnum,
                                         HttpServletRequest request,
                                         Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("获取lineId来查获取司机详情,lineId={}",lineId);
        MessageNumDTO messageNum = managerService.getMessages(lineId);

        Driver driver = driverService.findOne(dnum);
        DriverDTO driverDTO = Driver2DriverDTOConverter.convert(driver);
        map.put("name",lineId);
        map.put("orderMessages",messageNum.getOrderMessages());
        map.put("driverMessages",messageNum.getDriverMessages());
        map.put("allMessages",messageNum.getAllMessages());
        map.put("driver",driverDTO);

        return new ModelAndView("manager/driverDetail",map);

    }

    @GetMapping("/driver/goToAddDriver")
    public ModelAndView goToAddDriver(HttpServletRequest request,
                                      Map<String,Object>map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("获取lineId来添加司机,lineId={}",lineId);
        MessageNumDTO messageNum = managerService.getMessages(lineId);

        map.put("name",lineId);
        map.put("orderMessages",messageNum.getOrderMessages());
        map.put("driverMessages",messageNum.getDriverMessages());
        map.put("allMessages",messageNum.getAllMessages());

        return new ModelAndView("manager/addDriverForm");

    }

    //增加司机,这里是指通过manager直接添加，但是仍需要确认
    @PostMapping("/driver/DriverAdd")
    @Transactional
    public ModelAndView addOneDriver(@Valid addDriverFormForManager driverInfoForm,
                                     BindingResult bindingResult,
                                     HttpServletRequest request,
                                     Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("获取lineId来添加司机,lineId={}",lineId);

        if (bindingResult.hasErrors()){
            log.error("增加司机时填表有误");
            throw new ManagerException(bindingResult.getFieldError().getDefaultMessage(),"/manager/driver/goToAddDriver");

        }
        managerService.AddOneDriver(driverInfoForm,lineId);

        log.info("线路{},添加司机{}成功",lineId,driverInfoForm.getName());
        //司机已经不需要再确认注册这一步操作！
        map.put("msg","添加注册司机信息"+driverInfoForm.getName()+ResultEnums.SUCCESS.getMsg()+", 司机已经是休息中状态！");
        map.put("url","/manager/driver/goToAddDriver");

        return new ModelAndView("common/success",map);

    }

    //删除司机信息(根据司机dnum删)
    @DeleteMapping("/driver/DriverDelete")
    @Transactional
    public ModelAndView deleteOneDriver(@RequestParam("dnum") String dnum,
                                        HttpServletRequest request,
                                        Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("获取lineId来删除司机,lineId={}",lineId);

        managerService.DeleteOneDriver(dnum,lineId);
        log.info("删除司机成功,line={},dnum={}",lineId,dnum);
        map.put("msg","删除司机" + dnum + ResultEnums.SUCCESS.getMsg());
        map.put("url","/manager/driver/allDrivers");

        return new ModelAndView("common/success",map);
    }


    //确认添加司机
    @GetMapping("/driver/confirmDriver")
    @Transactional
    public ModelAndView confirmOneDriver(@RequestParam("dnum")String dnum,
                                         HttpServletRequest request,
                                         Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("获取lineId来同意司机注册,lineId={}",lineId);

        managerService.confirmOneDriver(dnum,lineId);
        log.info("确认添加司机成功,line={},dnum={}",lineId,dnum);

        map.put("msg","确认司机" + dnum + ResultEnums.SUCCESS.getMsg());
        map.put("url","/manager/driver/findByStatus?status="+DriverStatusEnums.TO_BE_VERIFIED.getCode());

        return new ModelAndView("common/success",map);

    }

    /*订单相关*/


    //查看所有订单
    @GetMapping("/order/allOrders")
    public ModelAndView allOrders(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                  @RequestParam(value = "size",defaultValue = "10") Integer size,
                                  HttpServletRequest request,
                                  Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("获取lineId来查当前线路所有订单信息,lineId={}",lineId);

        Sort sort = new Sort(Sort.Direction.DESC,"createTime");
        PageRequest pageRequest = new PageRequest(page-1,size,sort);

        Page<Order> orderPage = managerService.getAllOrders(lineId,pageRequest);

        MessageNumDTO messageNum = managerService.getMessages(lineId);

        //List<Order> orders = managerService.getAllOrders(lineId);
        map.put("orders",orderPage);
        map.put("name",lineId);
        map.put("currentPage",page);
        map.put("size",size);
        map.put("orderMessages",messageNum.getOrderMessages());
        map.put("driverMessages",messageNum.getDriverMessages());
        map.put("allMessages",messageNum.getAllMessages());
        return new ModelAndView("manager/allOrders",map);
    }

    //查看不同状态订单
    @GetMapping("/order/findByStatus")
    public ModelAndView getOrdersByStatus(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                          @RequestParam(value = "size",defaultValue = "10") Integer size,
                                          @RequestParam("status") Integer status,
                                          HttpServletRequest request,
                                          Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        log.info("获取lineId来查查询相应状态的订单,status={}", status);
        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("lineId={}",lineId);

        //都按照时间顺序，由新到旧来输出
        Sort sort = new Sort(Sort.Direction.DESC,"createTime");
        PageRequest pageRequest = new PageRequest(page-1,size,sort);

        //List<Order> orderList = managerService.getOrdersByStatus(lineId,status);


        Page<Order> orderPage = managerService.getOrdersByStatus(lineId,status,pageRequest);

        MessageNumDTO messageNum = managerService.getMessages(lineId);

        map.put("orderMessages",messageNum.getOrderMessages());
        map.put("driverMessages",messageNum.getDriverMessages());
        map.put("allMessages",messageNum.getAllMessages());
        map.put("orders",orderPage);
        map.put("name",lineId);
        map.put("currentPage",page);

        map.put("size",size);


        if (status.equals(OrderStatusEnums.WAIT.getCode())){

            //待出行和待返程一起,前端来区分
            List<DriverDTO> drivers = managerService.getDriversByStatus(lineId,DriverStatusEnums.AVAILABLE.getCode());
            //List<DriverDTO> drivers = managerService.getDriversByStatus(lineId,DriverStatusEnums.AVAILABLE2.getCode());
            map.put("drivers",drivers);
            return new ModelAndView("manager/waitOrders",map);
        }
        else if (status.equals(OrderStatusEnums.PROCESSIN.getCode())){
            return new ModelAndView("manager/processingOrders",map);
        }
        else{
            return new ModelAndView("manager/doneOrders",map);
        }
    }

    //取消订单
    @GetMapping("/order/cancel")
    @Transactional
    public ModelAndView cancelOrder(@RequestParam("orderId") String orderId,
                                    HttpServletRequest request,
                                    Map<String,Object> map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("获取lineId来强制取消正在进行中的订单,lineId={}",lineId);
        //司机状态不应改变
        managerService.cancelOneOrder(orderId,lineId);

        map.put("name",lineId);
        map.put("url","/manager/order/allOrders");
        return new ModelAndView("common/success",map);
    }


    //确认订单
    @GetMapping("/order/confirm")
    @Transactional
    public ModelAndView confirmOrder(@RequestParam("orderId") String orderId,
                                     @RequestParam("dnum") String dnum,
                                     HttpServletRequest request,
                                     Map<String,Object> map){

        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("获取lineId来确认订单信息,lineId={}",lineId);

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


    /*manager 个人设置相关及信息反馈 */

    //联系与帮助
    @GetMapping("/goContactAndHelp")
    public ModelAndView goContactAndHelp(HttpServletRequest request,
                                     Map<String,Object>map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("获取lineId来跳转到设置界面,lineId={}",lineId);
        MessageNumDTO messageNum = managerService.getMessages(lineId);


        map.put("orderMessages",messageNum.getOrderMessages());
        map.put("driverMessages",messageNum.getDriverMessages());
        map.put("allMessages",messageNum.getAllMessages());
        map.put("name",lineId);
        return new ModelAndView("manager/goContactAndHelp",map);
    }


    @PostMapping("/message/post")
    public ModelAndView post2Driver(HttpServletRequest request,
                                    Message2DriverForm message2DriverForm,
                                    Map<String,Object>map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("获取lineId来发送消息,lineId={}",lineId);
        MessageNumDTO messageNum = managerService.getMessages(lineId);

        managerService.postMessages(lineId,message2DriverForm);

        map.put("msg",ResultEnums.SUCCESS.getMsg());
        map.put("url","/manager/goContactAndHelp");

        return new ModelAndView("common/success",map);
    }

    //个人信息
    @GetMapping("/personalInfo")
    public ModelAndView personalInfo(HttpServletRequest request,
                                     Map<String,Object>map){
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
        log.info("获取lineId来跳转到个人信息界面,lineId={}",lineId);

        List<Manager> managers = managerService.findAllByLineId(lineId);
        MessageNumDTO messageNum = managerService.getMessages(lineId);


        map.put("name",lineId);
        //查看本线路有几个负责人及其信息
        //暂不区分开来，暂不转DTO做信息保护
        map.put("managers",managers);
        map.put("orderMessages",messageNum.getOrderMessages());
        map.put("driverMessages",messageNum.getDriverMessages());
        map.put("allMessages",messageNum.getAllMessages());
        return new ModelAndView("manager/personalInfo",map);

    }

//    //其他设置
//    @GetMapping("/otherSettings")
//    public ModelAndView otherSettings(HttpServletRequest request,
//                                      Map<String,Object> map){
//        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);
//
//        Integer lineId = Integer.parseInt(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"");
//        log.info("获取lineId来跳转到其他设置界面,lineId={}",lineId);
//
//        MessageNumDTO messageNum = managerService.getMessages(lineId);
//
//
//        map.put("name",lineId);
//        map.put("orderMessages",messageNum.getOrderMessages());
//        map.put("driverMessages",messageNum.getDriverMessages());
//        map.put("allMessages",messageNum.getAllMessages());
//
//
//
//        return new ModelAndView("manager/otherSettings",map);
//    }



}
