package com.liancheng.lcweb.service;

import com.liancheng.lcweb.VO.Result;
import com.liancheng.lcweb.domain.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findOne(String id);

    User getUser(String mobile, String password);

    User addUser(User user);

    User findByMobile(String mobile);

    User findbyEmail(String email);

    User findByUserName(String userName);

    User userLogin(User user);

    Result deleteOne(String id);
}
