package com.liancheng.lcweb.service;

import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.OrderDriDTO;
import com.liancheng.lcweb.dto.UserOrderDTO;
import com.liancheng.lcweb.form.UserOrderForm;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    List<Order> findAll();

    void createOne(String userId,UserOrderForm order);

    void changeOneInfo(String userId, String orderId, UserOrderForm changeForm);

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

    List<UserOrderDTO> findUserTravelOrder(String userId);

    List<UserOrderDTO> findUserOrderByStatus(Integer status,String userId);


    void deleteByOrderId(String orderId);

    List<OrderDriDTO> findDriverOrderByStatus(Integer status, String dnum);

}
