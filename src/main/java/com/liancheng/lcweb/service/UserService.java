package com.liancheng.lcweb.service;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.domain.AccessToken;
import com.liancheng.lcweb.domain.User;
import com.liancheng.lcweb.form.UserInfoForm;
import com.liancheng.lcweb.form.UserLoginForm;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findOne(String id);

    User getUser(String mobile, String password);

    void addUser(UserInfoForm userRegisterForm);

    User findByMobile(String mobile);

    User findbyEmail(String email);

    List<User> findByUserName(String userName);

    AccessToken userLogin(UserLoginForm user);

    void deleteOne(String id);

    void changeInfo(String userId, UserInfoForm userChangeInfoForm);

}
