package com.liancheng.lcweb.service;

import com.liancheng.lcweb.domain.Manager;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.List;


public interface ManagerService {

    List<Manager> findAll();

    Manager findOne(Integer id);

    Manager getManager(String name, String password);

    Manager addManager(Manager manager);

}
