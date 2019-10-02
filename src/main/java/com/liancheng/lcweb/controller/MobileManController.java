package com.liancheng.lcweb.controller;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.constant.RedisConstant;
import com.liancheng.lcweb.domain.Line;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.LineInfoDTO;
import com.liancheng.lcweb.dto.ManagerDTO;
import com.liancheng.lcweb.enums.OrderStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.form.MobileManRequestForm;
import com.liancheng.lcweb.form.UserLoginForm;
import com.liancheng.lcweb.service.LineService;
import com.liancheng.lcweb.service.MobileManService;
import com.liancheng.lcweb.service.OrderService;
import com.liancheng.lcweb.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/mobileManager")
public class MobileManController {

    @Autowired
    private MobileManService managerService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private LineService lineService;


    // 做统一验证
    private void validForm(String token, Integer lineId){
        if (token==null || StringUtils.isEmpty(token)){
            throw new LcException(ResultEnums.USER_TOKEN_EXPIRE);
        }
        String tokenValue = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,token))+"";

        if (StringUtils.isEmpty(tokenValue)){
            log.warn("redis中无此管理员信息，无法配合其操作");
            throw new LcException(ResultEnums.USER_TOKEN_EXPIRE);
        }
        Manager manager = managerService.findOne(tokenValue);
        if (manager == null || !manager.getLineId().equals(lineId)){
            // 不匹配直接说无此线路就行
            throw new LcException(ResultEnums.NO_SUCH_LINENAME);
        }
    }

    @PostMapping("/login")
    public ResultVO mobileManagerLogin(@Valid @RequestBody UserLoginForm form, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("用户(管理员)登入信息不正确");
            return ResultVOUtil.error(
                    ResultEnums.USER_LOGIN_FORM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        ManagerDTO manager =  managerService.login(form);

        // 存管理员的tel信息
        String telNum = manager.getTelNum();

        // 设置token到redis
        String token = UUID.randomUUID().toString();
        Integer expire = RedisConstant.EXPIRE;// 每天要求重新登陆
        //加个前缀显得高端
        //使用telNum可以一线路多管理员支持同时在线
        redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX,token),telNum,expire, TimeUnit.SECONDS);

        manager.setToken(token);
        // 返回一个DTO，前段
        return ResultVOUtil.success(manager);
    }

    @GetMapping("/logout")
    public ResultVO mobileManagerLogout(@RequestParam("token") String token){

        // 从redis上删除信息
        redisTemplate.opsForValue().getOperations().delete(String.format(RedisConstant.TOKEN_PREFIX,token));

        // 需要前端将本地缓存删除
        return ResultVOUtil.success("登出成功");

    }


    @PostMapping("/driver/allDrivers")
    public ResultVO allDrivers(@Valid @RequestBody MobileManRequestForm form,BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            // 填表信息有误
            return ResultVOUtil.error(
                    ResultEnums.MOBILE_FORM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        validForm(form.getToken(),form.getLineId());
        return ResultVOUtil.success(managerService.getAllDrivers(form.getLineId()));
    }

    @PostMapping("/driver/findByStatus")
    public ResultVO findDriverByStatus(@Valid @RequestBody MobileManRequestForm form, BindingResult bindingResult){
        if (bindingResult.hasErrors()||form.getStatus()==null){
            // 填表信息有误
            return ResultVOUtil.error(
                    ResultEnums.MOBILE_FORM_ERROR);
        }
        validForm(form.getToken(),form.getLineId());
        return ResultVOUtil.success(managerService.getDriversByStatus(form.getLineId(),form.getStatus()));

    }

    // LCMan 的allOrders返回的是根据status和time排序的list，只包含待处理和进行中
    @PostMapping("/orders/allOrders")
    public ResultVO allOrders(@Valid @RequestBody MobileManRequestForm form, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            // 填表信息有误
            return ResultVOUtil.error(
                    ResultEnums.MOBILE_FORM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        validForm(form.getToken(),form.getLineId());
        return ResultVOUtil.success(managerService.getAllOrders(form.getLineId()));
    }

    @PostMapping("/orders/findByStatus")
    public ResultVO findOrdersByStatus(@Valid @RequestBody MobileManRequestForm form, BindingResult bindingResult){
        if (bindingResult.hasErrors()||form.getStatus()==null){
            // 填表信息有误
            return ResultVOUtil.error(
                    ResultEnums.MOBILE_FORM_ERROR);
        }
        validForm(form.getToken(),form.getLineId());
        return ResultVOUtil.success(
                        managerService.getOrdersByStatus(form.getLineId(),
                                                         form.getStatus())
                                    );
    }

    @PostMapping("/order/confirmOne")
    public ResultVO confirmOne(@Valid @RequestBody MobileManRequestForm form, BindingResult bindingResult){
        if (bindingResult.hasErrors() || form.getOrderId()==null || form.getDnum()==null){
            // 填表信息有误
            return ResultVOUtil.error(
                    ResultEnums.MOBILE_FORM_ERROR);
        }
        validForm(form.getToken(),form.getLineId());
        Order order = orderService.findOne(form.getOrderId());
        if (order==null || !order.getLineId().equals(form.getLineId())){
            throw new LcException(ResultEnums.ORDER_NOT_FOUND);
        }
        else if (!order.getOrderStatus().equals(OrderStatusEnums.WAIT.getCode())){
            // 可能被其他管理员处理
            throw new LcException(ResultEnums.ORDER_STATUS_ERROR);
        }
        else {
            managerService.confirmOneOrder(order,form.getDnum());
        }
        return ResultVOUtil.success();
    }

    @PostMapping("/order/cancelOne")
    public ResultVO cancelOne(@Valid @RequestBody MobileManRequestForm form, BindingResult bindingResult){
        if (bindingResult.hasErrors() || form.getOrderId()==null ){
            // 填表信息有误
            return ResultVOUtil.error(
                    ResultEnums.MOBILE_FORM_ERROR);
        }
        validForm(form.getToken(),form.getLineId());
        managerService.cancelOneOrder(form.getOrderId(),form.getLineId());
        return ResultVOUtil.success();
    }

    // 线路 tab 某一项使用
    @PostMapping("/lineInfo")
    public ResultVO getLineInfo(@Valid @RequestBody MobileManRequestForm form, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            // 填表信息有误
            return ResultVOUtil.error(
                    ResultEnums.MOBILE_FORM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        validForm(form.getToken(),form.getLineId());
        Line line = lineService.findOne(form.getLineId());

        return ResultVOUtil.success(managerService.getLineInfo(line));
    }

    @PostMapping("/setPrice")
    public ResultVO setPrice(@Valid @RequestBody MobileManRequestForm form, BindingResult bindingResult){
        if (bindingResult.hasErrors() || form.getPrice()==null ){
            // 填表信息有误
            return ResultVOUtil.error(
                    ResultEnums.MOBILE_FORM_ERROR);
        }
        Integer lineId = form.getLineId();
        validForm(form.getToken(),lineId);
        Line line =  managerService.setLinePrice(lineId,form.getPrice());
        LineInfoDTO lineInfoDTO = managerService.getLineInfo(line);

        return ResultVOUtil.success(lineInfoDTO);
    }

}
