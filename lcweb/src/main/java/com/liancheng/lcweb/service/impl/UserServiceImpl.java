package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.domain.User;
import com.liancheng.lcweb.repository.UserRepository;
import com.liancheng.lcweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAllByLine(String line) {
        return userRepository.findAll();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return null;
    }
}
