package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,String> {

    List<Order> findByUnum(String unum);

    List<Order> findByDnum(String dnum);

    List<Order> findByLine(String line);

    List<Order> findByOrderStatus(Integer orderStatus);
}
