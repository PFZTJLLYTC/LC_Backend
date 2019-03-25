package com.liancheng.lcweb.service;

import com.liancheng.lcweb.domain.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    List<User> findAllByLine(String line);

    User save(User user);

    //待补充，现在只理框架
}
