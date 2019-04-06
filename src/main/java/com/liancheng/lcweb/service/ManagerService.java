package com.liancheng.lcweb.service;

import com.liancheng.lcweb.VO.Result;
import com.liancheng.lcweb.domain.Manager;

import java.util.List;


public interface ManagerService {

    List<Manager> findAll();

    Manager findOne(Integer id);

    Manager getManager(String name, String password);

    Manager addManager(Manager manager);

    Result deleteOne(Integer lineId);



}
