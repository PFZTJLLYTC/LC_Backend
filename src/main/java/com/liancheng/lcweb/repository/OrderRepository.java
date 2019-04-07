package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.enums.OrderStatusEnums;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,String> {

    List<Order> findByUserId(String userId);

    List<Order> findByDnum(String dnum);

    List<Order> findByLine(String line);

    List<Order> findByOrderStatus(Integer orderStatus);

    List<Order> findByOrderStatusAndUserId(Integer orderStatus,String userId);


}
