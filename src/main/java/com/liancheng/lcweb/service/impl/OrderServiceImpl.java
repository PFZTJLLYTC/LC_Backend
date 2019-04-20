package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.UserDoneOrderDTO;
import com.liancheng.lcweb.enums.OrderStatusEnums;
import com.liancheng.lcweb.form.UserOrderForm;
import com.liancheng.lcweb.repository.OrderRepository;
import com.liancheng.lcweb.service.OrderService;
import com.liancheng.lcweb.utils.KeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override//创建的时候自动为新订单
    public Order createOne(String userId,UserOrderForm userOrderForm) {
        Order result = new Order();
        BeanUtils.copyProperties(userOrderForm,result);
        result.setUserId(userId);
        result.setOrderId(KeyUtil.genUniquekey());
        //result.setOrderStatus(OrderStatusEnums.WAIT.getCode());
        return orderRepository.save(result);
    }


    /**
     * 用户查询操作
     * @param userId
     * @return Order
     */
    @Override
    public List<Order> findUserWaitOrProcessinOrder(String userId) {
        return orderRepository.findByOrderStatusAndUserId(
                OrderStatusEnums.WAIT.getCode(),
                OrderStatusEnums.PROCESSIN.getCode(),
                userId);
    }

    @Override
    public List<Order> findUserWaitOrder(String userId){
        return orderRepository.findByOrderStatusAndUserId(OrderStatusEnums.WAIT.getCode(),userId);
    }

    @Override
    public List<Order> findUserProcessinOrder(String userId){
        return orderRepository.findByOrderStatusAndUserId(OrderStatusEnums.PROCESSIN.getCode(),userId);
    }

    @Override
    public List<UserDoneOrderDTO> findUserDoneOrder(String userId){
        return orderRepository.findUserDoneOrderByUserId(userId);
    }

    /***********************************************/

    @Override
    public Order confirmOne(Order order) {
        return null;
    }

    @Override
    public Order finishOne(Order order) {
        return null;
    }

    @Override
    public Order cancelOne(Order order) {
        return null;
    }

    @Override
    public Order findOne(String OrderId) {
        return null;
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> findByDnum(String dnum) {
        return orderRepository.findByDnum(dnum);
    }

    @Override
    public List<Order> findAllWait() {
        return orderRepository.findByOrderStatus(OrderStatusEnums.WAIT.getCode());
    }

    @Override
    public List<Order> findAllProcessing() {
        return orderRepository.findByOrderStatus(OrderStatusEnums.PROCESSIN.getCode());
    }

    @Override
    public List<Order> findAllDone() {
        return orderRepository.findByOrderStatus(OrderStatusEnums.DONE.getCode());
    }
}
