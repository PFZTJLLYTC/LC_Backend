package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepository extends JpaRepository<User,String> {
    User findByUnum(String unum);

    User findByMobile(String mobile);

    List<User> findByUsername(String username);

    User findByEmail(String email);
}
