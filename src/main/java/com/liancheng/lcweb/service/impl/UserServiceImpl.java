package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.constant.MessagesConstant;
import com.liancheng.lcweb.constant.PushModuleConstant;
import com.liancheng.lcweb.domain.AccessToken;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.domain.User;
import com.liancheng.lcweb.dto.PushDTO;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.form.ChangePasswordForm;
import com.liancheng.lcweb.form.UserInfoForm;
import com.liancheng.lcweb.form.UserLoginForm;
import com.liancheng.lcweb.repository.UserRepository;
import com.liancheng.lcweb.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private PushServiceWithImpl pushService;

    @Autowired
    private OrderService orderService;


    @Override
    public User getUser(String mobile, String password) {
        return userRepository.findByMobileAndPassword(mobile,password);
    }

    @Override
    public void addUser(UserInfoForm userRegisterForm) {

        if (findByMobile(userRegisterForm.getMobile())!=null){
            throw new LcException(ResultEnums.USER_MOBILE_ALREADY_EXISTS);
        }
        User user = new User();
        BeanUtils.copyProperties(userRegisterForm,user);

        user.setPassword(passwordEncoder.encode(userRegisterForm.getPassword()));
        user.setId(UUID.randomUUID().toString());
        user.setTakeTimes(0);

        userRepository.save(user);
        log.info("新增用户，user={}",userRegisterForm);

        //防止nullpointerexception,需要先找到
        messagesService.createMessage(findByMobile(user.getMobile()).getId(), MessagesConstant.welcomeCustom, MessagesConstant.type2);

    }

    @Override
    public void changePassword(ChangePasswordForm form) {
        String userTel = form.getId();
        User user = userRepository.findByMobile(userTel);
        log.info("user{}修改密码",user.getId());
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public AccessToken userLogin(UserLoginForm userLoginForm){

        User user = findByMobile(userLoginForm.getMobile());

        if(user==null){
            throw new LcException(ResultEnums.NO_SUCH_USER);
        }

        boolean matches=passwordEncoder.matches(
                userLoginForm.getPassword(),
                user.getPassword());

        if(!matches){
            throw new LcException(ResultEnums.PASSWORD_MATCHES_ERROR);
        }

        return accessTokenService.createAccessToken(user.getId());
    }

    @Override
    public User findByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }


    @Override
    public List<User> findByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findOne(String id) {

        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()){
            throw new LcException(ResultEnums.NO_SUCH_USER);
        }
        return user.get();

    }

    @Override
    public void deleteOne(String id) {

        User user = findOne(id);
        userRepository.delete(user);

    }

    @Override
    public void changeInfo(String userId, UserInfoForm userChangeInfoForm) {

        User user = findOne(userId);
        if (user==null){
            throw new LcException(ResultEnums.NO_SUCH_USER);
        }
        user.setUsername(userChangeInfoForm.getUsername());
        user.setMobile(userChangeInfoForm.getMobile());

        userRepository.save(user);
    }

    @Override
    public void deleteMessages(String id) {
        log.info("用户{}删除所有消息",id);
        messagesService.deleteMessageByTarget(id);
    }

    @Override
    public void deleteCertainMessages(List<Integer> idList) {
        //已读即删
        for (Integer id : idList){
            messagesService.deleteMessage(id);
        }
    }

    @Override
    public void cancelOneOrder(String orderId) {
        //用户取消的目前是未处理的，为方便扩展,另代码已注释.
        Order order = orderService.findOne(orderId);
        if (order!=null){
            try {
                String msg = "您已经成功取消行程为"+order.getLineName()+"的订单!";
                PushDTO userPushDTO = new PushDTO(PushModuleConstant.TITLE,msg,2,PushModuleConstant.platform,"",order.getUserId());
                messagesService.createMessage(order.getUserId(),msg, MessagesConstant.type1);
                pushService.pushMessage2User(userPushDTO);
            } catch (Exception e) {
                e.printStackTrace();
                log.warn("向乘客发送即时消息失败,userId={},message={}",order.getUserId(),e.getMessage());
            }
            orderService.deleteByOrderId(orderId);
        }
        else {
            throw new LcException(ResultEnums.ORDER_NOT_FOUND);
        }


    }
}
