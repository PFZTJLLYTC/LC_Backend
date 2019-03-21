package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepository extends JpaRepository<User,String> {
    public List<User> findByUnum(String unum);

    public List<User> findByMobile(String mobile);

    public List<User> findByUsername(String username);

    public List<User> findByEmail(String email);
}
