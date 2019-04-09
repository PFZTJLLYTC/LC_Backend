package com.liancheng.lcweb.service;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.domain.Manager;

import java.util.List;


public interface ManagerService {

    List<Manager> findAll();

    Manager findOne(Integer id);

    Manager getManager(String name, String password);

    Manager addManager(Manager manager);

    ResultVO deleteOne(Integer lineId);



}
