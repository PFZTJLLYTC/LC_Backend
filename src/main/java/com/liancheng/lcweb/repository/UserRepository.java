package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findById(String id);

    User findByMobile(String mobile);

    User findByUsername(String username);

    User findByEmail(String email);

    User findByMobileAndPassword(String mobile,String password);
}
