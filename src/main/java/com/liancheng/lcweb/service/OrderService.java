package com.liancheng.lcweb.service;

import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.DriverDoneOrderDTO;
import com.liancheng.lcweb.dto.UserOrderDTO;
import com.liancheng.lcweb.form.UserOrderForm;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    List<Order> findAll();

    void createOne(String userId,UserOrderForm order);

    Order confirmOne(Order order, Driver driver);

    Order finishOne(Order order);

    void cancelOne(Order order);

    Order findOne(String OrderId);

    Integer findDriverTodayOrders(String dnum, LocalDate today);

    Integer findDriverTodayUsers(String dnum,LocalDate today);

    List<Order> findByUserId(String userId);

    List<Order> findByDnum(String dnum);

    List<Order> findAllWait();

    List<Order> findAllProcessing();

    List<Order> findAllDone();


    List<UserOrderDTO> findUserWaitOrder(String userId);

    List<UserOrderDTO> findUserProcessinOrder(String userId);

    List<UserOrderDTO> findUserDoneOrder(String userId);

    List<Order> findDriverProcessinOrder(String dnum);

    List<DriverDoneOrderDTO> findDriverDoneOrder(String dnum);

    void deleteByOrderId(String orderId);
}
