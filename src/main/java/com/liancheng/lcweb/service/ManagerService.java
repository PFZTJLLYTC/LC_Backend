package com.liancheng.lcweb.service;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.DriverDTO;
import com.liancheng.lcweb.dto.TotalInfoDTO;

import java.util.List;


public interface ManagerService {

    List<Manager> findAll();

    Manager findOne(Integer id);

    Manager getManager(Integer lineId, String password);

    Manager addManager(Manager manager);

    ResultVO deleteOne(Integer lineId);

    List<DriverDTO> getDriversByStatus(Integer lineId,Integer status);

    TotalInfoDTO getTotal(Integer lineId);

    List<DriverDTO> getAllDrivers(Integer lineId);

    List<Order> getAllOrders(Integer lineId);


}