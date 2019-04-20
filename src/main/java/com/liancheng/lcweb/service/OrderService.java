package com.liancheng.lcweb.service;

import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.UserDoneOrderDTO;
import com.liancheng.lcweb.form.UserOrderForm;

import java.util.List;

public interface OrderService {

    List<Order> findAll();

    Order createOne(String userId,UserOrderForm order);

    Order confirmOne(Order order);

    Order finishOne(Order order);

    Order cancelOne(Order order);

    Order findOne(String OrderId);

    List<Order> findByUserId(String userId);

    List<Order> findByDnum(String dnum);

    List<Order> findAllWait();

    List<Order> findAllProcessing();

    List<Order> findAllDone();

    List<Order> findUserWaitOrProcessinOrder(String userId);

    List<Order> findUserWaitOrder(String userId);

    List<Order> findUserProcessinOrder(String userId);

    List<UserDoneOrderDTO> findUserDoneOrder(String userId);
}
