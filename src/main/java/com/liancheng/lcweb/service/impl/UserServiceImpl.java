package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.VO.Result;
import com.liancheng.lcweb.domain.User;
import com.liancheng.lcweb.repository.UserRepository;
import com.liancheng.lcweb.service.UserService;
import com.liancheng.lcweb.utils.ResultUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public User addUser(User user) {
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

    @Override
    public Result deleteOne(String unum) {
        if (findOne(unum)!=null){
            userRepository.deleteById(unum);
            return ResultUtil.success();
        }
        else {
            return ResultUtil.error();
        }

    }
}
