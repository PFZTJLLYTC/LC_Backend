package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.domain.User;
import com.liancheng.lcweb.repository.UserRepository;
import com.liancheng.lcweb.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public User getUser(String mobile, String password) {
        return userRepository.findByMobileAndPassword(mobile,password);
    }

    @Override
    public User addUser(@Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return null;
        }
        User result = new User();
        BeanUtils.copyProperties(user,result);
        return userRepository.save(result);
    }

    @Override
    public User userLogin(User user){
        return getUser(user.getMobile(),user.getPassword());
    }

    @Override
    public User findByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }

    @Override
    public User findbyEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findOne(String unum) {
        return userRepository.findByUnum(unum);
    }
}
