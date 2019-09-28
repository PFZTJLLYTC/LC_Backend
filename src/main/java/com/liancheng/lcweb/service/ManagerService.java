package com.liancheng.lcweb.service;

import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Line;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.DriverDTO;
import com.liancheng.lcweb.dto.MessageNumDTO;
import com.liancheng.lcweb.dto.TotalInfoDTO;
import com.liancheng.lcweb.form.Message2DriverForm;
import com.liancheng.lcweb.form.addDriverFormForManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ManagerService {

    MessageNumDTO getMessages(Integer lineId);

    List<Manager> findAll();

    List<Manager> findAllByLineId(Integer lineId);

    Manager findOne(String telNum);

    Line setLinePrice(Integer lineId, String price);

    Manager getManager(String telNum, String password);

    Manager addManager(Manager manager);

    void deleteOne(String telNum);

    List<DriverDTO> getDriversByStatus(Integer lineId,Integer status);

    //先只是多种实现，之后考虑删一个
    Page<DriverDTO> getDriversByStatus(Integer lineId,Integer status,Pageable pageable);

    TotalInfoDTO getTotal(Integer lineId);

    Page<DriverDTO> getAllDrivers(Integer lineId, Pageable pageable);

    List<Driver> getAllDrivers(Integer lineId);

    List<Order> getOrdersByStatus(Integer lineId,Integer status);

    Page<Order> getOrdersByStatus(Integer lineId,Integer status,Pageable pageable);

    List<Order> getAllOrders(Integer lineId);

    Page<Order> getAllOrders(Integer lineId,Pageable pageable);

    void AddOneDriver(addDriverFormForManager driverInfoForm, Integer lineId);

    void DeleteOneDriver(String dnum, Integer lineId);

    void confirmOneOrder(Order order,String dnum);

    void cancelOneOrder(String orderId, Integer lineId);

    void confirmOneDriver(String dnum,Integer lineId);

    void postMessages(Integer lineId, Message2DriverForm message2DriverForm);


}
