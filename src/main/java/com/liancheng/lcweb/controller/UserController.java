package com.liancheng.lcweb.controller;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.converter.User2UserDTOConverter;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.domain.User;
import com.liancheng.lcweb.dto.UserDTO;
import com.liancheng.lcweb.dto.UserDoneOrderDTO;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.form.UserInfoForm;
import com.liancheng.lcweb.form.UserLoginForm;
import com.liancheng.lcweb.form.UserOrderForm;
import com.liancheng.lcweb.repository.UserRepository;
import com.liancheng.lcweb.service.AccessTokenService;
import com.liancheng.lcweb.service.OrderService;
import com.liancheng.lcweb.service.UserService;
import com.liancheng.lcweb.utils.KeyUtil;
import com.liancheng.lcweb.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
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
    private AccessTokenService accessTokenService;


    //增加信息、注册
    @PostMapping(value = "/register")
    public ResultVO userAdd(@RequestBody @Valid UserInfoForm userRegisterForm,BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            log.error("注册表单错误");
            throw new LcException(ResultEnums.USER_CHANGE_FORM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        userService.addUser(userRegisterForm);
        log.info("add a new user，userMobile={}",userRegisterForm.getMobile());

        //跳转到登陆界面
        return ResultVOUtil.success();
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

    @PostMapping(value = "/login")
    public ResultVO userLogin(@RequestBody@Valid UserLoginForm user,BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("登入信息不合法");
            throw new LcException(ResultEnums.USER_LOGIN_FORM_ERROR);
        }
        User result = userService.userLogin(user);
        if(result!=null)
            return ResultVOUtil.success(accessTokenService.createAccessToken(result.getId()));
        else
            return ResultVOUtil.error(ResultEnums.NO_SUCH_USER);
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
            throw new LcException(ResultEnums.ORDER_INFO_ERROR);
        }
        //todo 交给相应manager操作,应该被异步通知，目前为节约时间选择数据库变化作为消息媒介
        //考虑消息队列

        return ResultVOUtil.success(orderService.createOne(userId,userOrderForm));
    }

    //取消订单，只有当订单未被confirm才可以

    @GetMapping("/orders/show")
    public ResultVO findWaitOrProcessinOrder(@RequestParam String userId){
        List<Order> orderList = orderService.findUserWaitOrProcessinOrder(userId);
        if(orderList.size()==0){
            log.info("No wait or processin order");
            return ResultVOUtil.error(ResultEnums.NO_WAIT_OR_PROCESSIN_ORDER);
        }
        else if(orderList.size()==1){
            log.info("find wait or processin order");
            return ResultVOUtil.success(orderList.get(0));
        }else{
            log.info("wait or process more than one");
            return ResultVOUtil.error(ResultEnums.WAIT_OR_PROCESSIN_ORDER_MORE_THAN_ONE);
        }
    }

    /**
     * 查询待处理订单，正常返回单个order
     * @param userId
     * @return
     */
    @GetMapping("/orders/wait")
    public ResultVO findWaitOrder(@RequestParam String userId){
            List<Order> orderList = orderService.findUserWaitOrder(userId);
            if(orderList.size()==0){
                log.info("No wait order");
                return ResultVOUtil.error(ResultEnums.NO_WAIT_ORDER);
            }
            else if(orderList.size()==1){
                log.info("find a wait order, order={}",orderList.get(0));//正常情况只有一个未处理订单
                return ResultVOUtil.success(orderList.get(0));
            }
            else {
                log.error("wait order more than one");
                return ResultVOUtil.error(ResultEnums.WAIT_ORDER_MORE_THAN_ONE);
            }
    }


    /**
     * 查询进行中订单，正常返回单个order
     * @param userId
     * @return
     */
    @GetMapping("/orders/processin")
    public ResultVO findProcessinOrder(@RequestParam String userId){
        List<Order> orderList =orderService.findUserProcessinOrder(userId);
        if(orderList.size()==0){
            log.info("No processin order");
            return ResultVOUtil.error(ResultEnums.NO_PROCESSIN_ORDER);
        }
        else if(orderList.size()==1){
            log.info("find a processin order, order={}",orderList.get(0));//正常情况只有一个进行中订单
            return ResultVOUtil.success(orderList.get(0));
        }
        else {
            log.error("processin order more than one");
            return ResultVOUtil.error(ResultEnums.PROCESSIN_ORDER_MORE_THAN_ONE);
        }
    }

    /**
     * 查询已完成订单，正常返回一个order列表
     * @param userId
     * @return
     */
    @GetMapping("/orders/done")
    public ResultVO findDoneOrder(@RequestParam String userId){
        List<UserDoneOrderDTO> orderList=orderService.findUserDoneOrder(userId);
        if(orderList.size()==0){
            log.info("No done order");
            return ResultVOUtil.error(ResultEnums.NO_DONE_ORDER);
        }
        else{
            log.info("find done orderList={}",orderList);
            return ResultVOUtil.success(orderList);
        }
    }
}
