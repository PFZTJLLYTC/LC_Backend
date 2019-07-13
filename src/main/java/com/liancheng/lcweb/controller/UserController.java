package com.liancheng.lcweb.controller;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.converter.User2UserDTOConverter;
import com.liancheng.lcweb.domain.AccessToken;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.domain.User;
import com.liancheng.lcweb.dto.UserDTO;
import com.liancheng.lcweb.enums.OrderStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.form.UserInfoForm;
import com.liancheng.lcweb.form.UserLoginForm;
import com.liancheng.lcweb.form.UserOrderForm;
import com.liancheng.lcweb.repository.UserRepository;
import com.liancheng.lcweb.service.*;
import com.liancheng.lcweb.utils.KeyUtil;
import com.liancheng.lcweb.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private LineService lineService;

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private WebSocketService webSocketService;


    //增加信息、注册
    @PostMapping(value = "/register")
    public ResultVO userAdd(@RequestBody @Valid UserInfoForm userInfoForm,BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            log.error("注册表单错误");
//            throw new LcException(ResultEnums.USER_CHANGE_FORM_ERROR.getCode(),
//                    bindingResult.getFieldError().getDefaultMessage());
            return ResultVOUtil.error(ResultEnums.USER_CHANGE_FORM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        userService.addUser(userInfoForm);


        //跳转到登陆界面
        return ResultVOUtil.success();
    }

    @PostMapping(value = "/login")
    public ResultVO userLogin(@RequestBody@Valid UserLoginForm userLoginForm,BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("用户登入信息不合法");
            return ResultVOUtil.error(ResultEnums.USER_LOGIN_FORM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        AccessToken token = userService.userLogin(userLoginForm);

        return ResultVOUtil.success(token);

    }


    //根据Id更新user信息
    //也可以提出来改成单独修改一项,应该会拆开比较好
    @PutMapping(value = "/updateInfo")
    public ResultVO userUpdate(@RequestParam("userId") String userId, @RequestBody @Valid UserInfoForm userChangeInfoForm, BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            log.error("用户信息填表错误");
            throw new LcException(ResultEnums.USER_CHANGE_FORM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        userService.changeInfo(userId,userChangeInfoForm);

        return ResultVOUtil.success();
    }


    //返回司机信息,应该由前端储存起来
    @GetMapping("/info")
    public ResultVO userInfo(@RequestParam("userId") String userId){

        UserDTO userDTO = User2UserDTOConverter.convert(userService.findOne(userId));

        return ResultVOUtil.success(userDTO);
    }


    /*订单相关*/

    //创建订单
    @PostMapping("/orders/create")
    @Transactional
    public ResultVO createOrder(@RequestParam String userId,
                                @Valid @RequestBody UserOrderForm userOrderForm,
                                BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            log.error("订单填写信息不合法");
            return ResultVOUtil.error(ResultEnums.ORDER_INFO_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        orderService.createOne(userId,userOrderForm);
        return ResultVOUtil.success();
    }

    //取消订单，只有当订单未被confirm才可以

    /**
     * 查询待处理订单，正常返回单个order
     * @param userId
     * @return
     */
    @GetMapping("/orders/wait")
    public ResultVO findWaitOrder(@RequestParam String userId){
        return ResultVOUtil.success(orderService.findUserOrderByStatus(
                OrderStatusEnums.WAIT.getCode(),userId));
    }


    /**
     * 查询进行中订单，返回order列表
     * @param userId
     * @return
     */
    @GetMapping("/orders/processin")
    public ResultVO findProcessinOrder(@RequestParam String userId){
        return ResultVOUtil.success(orderService.findUserOrderByStatus(
                OrderStatusEnums.PROCESSIN.getCode(),userId));
    }

    /**
     * 查询已完成订单，正常返回一个order列表
     * @param userId
     * @return
     */
    @GetMapping("/orders/done")
    public ResultVO findDoneOrder(@RequestParam String userId){
        return ResultVOUtil.success(orderService.findUserOrderByStatus(
                OrderStatusEnums.DONE.getCode(),userId));
    }

    //前端回传待处理订单的orderId，用户直接按orderId删除待处理订单
    @PostMapping("/orders/delete")
    public ResultVO deleteOrder(@RequestParam String orderId){
        orderService.deleteByOrderId(orderId);
        return ResultVOUtil.success();
    }

    @GetMapping("/lines/all")
    public ResultVO findAllLines(){
        return ResultVOUtil.success(lineService.findAllLineName1AndLineName2());
    }

    @GetMapping("/messages/findMessages")
    public ResultVO findMessages(@RequestParam String userId){
        return ResultVOUtil.success(messagesService.findByTarget(userId));
    }

    @PostMapping("/messages/delete/{id}")
    public ResultVO deleteOneMessage(@PathVariable Integer id){
        messagesService.deleteMessage(id);
        return ResultVOUtil.success();
    }
}
