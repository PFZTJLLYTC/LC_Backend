package com.liancheng.lcweb.service;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.dto.DriverDTO;

import java.util.List;


public interface ManagerService {

    List<Manager> findAll();

    Manager findOne(Integer id);

    Manager getManager(String name, String password);

    Manager addManager(Manager manager);

    ResultVO deleteOne(Integer lineId);

    List<DriverDTO> getDriversByStatus(Integer lineId,Integer status);


}
