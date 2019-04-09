package com.liancheng.lcweb.controller;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.domain.User;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
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
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AccessTokenService accessTokenService;


    //增加信息、注册
    @PostMapping(value = "/signUp")//加表单验证的话新加dto层
    public ResultVO userAdd(@RequestBody User user){
        user.setId(KeyUtil.genUniquekey());
//        user.setUsername(user.getUsername());
        user.setPassword(user.getPassword());
        user.setMobile(user.getMobile());
//        user.setEmail(user.getEmail());
//        user.setEmailVerifiled(user.getEmailVerifiled());
        log.info("add a new user，userMobile={}",user.getMobile());
        return ResultVOUtil.success(userService.addUser(user));
    }



    //根据Id更新user信息
    //也可以提出来改成单独修改一项,应该会拆开比较好
    @PutMapping(value = "/update/{Id}")
    public ResultVO userUpdate(@PathVariable("Id") String id,
                               @RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("mobile") String mobile,
                               @RequestParam("email") String email,
                               @RequestParam("emailVerified") Boolean emailVerified,
                               @RequestParam("updateAt") Date updateAt){
        //todo 需要改
        User user = userService.findOne(id);
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setMobile(mobile);
        user.setEmail(email);
        user.setEmailVerifiled(emailVerified);
        log.info("update one user's info，user={}",username);
        return ResultVOUtil.success(userRepository.save(user));
    }

    @PostMapping(value = "/user/login")
    public ResultVO userLogin(@RequestBody User user){
        User result = userService.userLogin(user);
        if(result!=null)
            return ResultVOUtil.success(accessTokenService.createAccessToken(result.getId()));
        else
            return ResultVOUtil.error();
    }


    /*订单相关*/

    //创建订单
    @PostMapping("/createOrder")
    @Transactional
    public ResultVO createOrder(@RequestBody@Valid Order order, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            log.error("订单填写信息不合法");
            throw new LcException(ResultEnums.ORDER_INFO_ERROR);
        }
        //todo 交给相应manager操作,应该被同步通知

        return ResultVOUtil.success(orderService.createOne(order));
    }

    //查看未处理订单
    @GetMapping("/order/wait")
    public ResultVO findWaitOrder(@RequestParam String userId){
            List<Order> orderList = orderService.findUserWaitOrder(userId);
            if(orderList==null){
                log.info("No wait order");
                return ResultVOUtil.error();
            }
            else if(orderList.size()==1){
                log.info("find a wait order, order={}",orderList.get(0));//正常情况只有一个未处理订单
                return ResultVOUtil.success(orderList.get(0));
            }
            else {
                log.error("wait order more than one");
                return ResultVOUtil.error();
            }
    }

    //取消订单，只有当订单未被confirm才可以

    //查看进行中订单
    @GetMapping("/order/processin")
    public ResultVO findProcessinOrder(@RequestParam String userId){
        List<Order> orderList =orderService.findUserProcessinOrder(userId);
        if(orderList==null){
            log.info("No processin order");
            return ResultVOUtil.error();
        }
        else if(orderList.size()==1){
            log.info("find a processin order, order={}",orderList.get(0));//正常情况只有一个进行中订单
            return ResultVOUtil.success(orderList.get(0));
        }
        else {
            log.error("processin order more than one");
            return ResultVOUtil.error();
        }
    }

    //查看已完成订单
    @GetMapping("/order/done")
    public ResultVO findDoneOrder(@RequestParam String userId){
        List<Order> orderList=orderService.findUserDoneOrder(userId);
        if(orderList==null){
            log.info("No done order");
            return ResultVOUtil.error();
        }
        else{
            log.info("find done orderList={}",orderList);
            return ResultVOUtil.success(orderList);
        }
    }
}
