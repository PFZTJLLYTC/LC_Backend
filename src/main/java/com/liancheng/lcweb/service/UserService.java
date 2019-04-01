package com.liancheng.lcweb.service;

import com.liancheng.lcweb.domain.User;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.List;

public interface UserService {

    List<User> findAll();

    User findOne(String unum);

    User getUser(String mobile, String password);

    User addUser(User user);

    User findByMobile(String mobile);

    User findbyEmail(String email);

    User findByUserName(String userName);

    User userLogin(User user);
}
