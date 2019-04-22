package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.UserDoneOrderDTO;
import com.liancheng.lcweb.enums.OrderStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.ManagerException;
import com.liancheng.lcweb.form.UserOrderForm;
import com.liancheng.lcweb.repository.OrderRepository;
import com.liancheng.lcweb.service.OrderService;
import com.liancheng.lcweb.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
        Optional<Order> order = orderRepository.findById(OrderId);
        if (!order.isPresent()){
            log.info("无此订单");
            return null;
            //不能在这里抛异常！！都要用这个函数
//            throw new ManagerException(ResultEnums.ORDER_NOT_FOUND.getMsg(),"manager/alldeals");
        }
        return order.get();
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
