package com.liancheng.lcweb.service;

import com.liancheng.lcweb.domain.Order;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.List;

public interface OrderService {

    List<Order> findAll();

    Order createOne(Order order);

    Order confirmOne(Order order);

    Order finishOne(Order order);

    Order cancelOne(Order order);

    Order findOne(String OrderId);

    List<Order> findByUserId(String userId);

    List<Order> findByDnum(String dnum);

    List<Order> findAllWait();

    List<Order> findAllProcessing();

    List<Order> findAllDone();

    List<Order> findUserWaitOrder(String userId);

    List<Order> findUserProcessinOrder(String userId);

    List<Order> findUserDoneOrder(String userId);
}
