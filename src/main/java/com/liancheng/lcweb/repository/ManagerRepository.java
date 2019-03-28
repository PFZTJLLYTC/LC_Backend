package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManagerRepository extends JpaRepository<Manager,Integer> {

    Manager findByPhoneNum(String phoneNUm);

    Manager findByNameAndPassword(String name, String password);
}
