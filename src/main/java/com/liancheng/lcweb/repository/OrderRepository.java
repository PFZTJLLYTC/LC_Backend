package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.UserDoneOrderDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,String> {

    List<Order> findByUserId(String userId);

    List<Order> findByDnum(String dnum);

    //用all来筛选好像还更快一些
//    List<Order> findByLineIdAndOrderStatus(Integer lineId,Integer orderStatus);

    @Query(value = "select * from user_order as o where o.line = (select line from manager  where line_id = ?1 ) ",nativeQuery = true)
    List<Order> findByLineId(Integer lineId);

    List<Order> findByOrderStatus(Integer orderStatus);

    List<Order> findByOrderStatusAndUserId(Integer orderStatus,String userId);

    @Query(value = "SELECT * FROM user_order WHERE user_id = ?3 AND (order_status = ?1 OR order_status = ?2) ",nativeQuery = true)
    List<Order> findByOrderStatusAndUserId(Integer orderStatusOne,Integer orderStatusTwo,String userId);

    @Query(value = "SELECT o.car_num AS carNum,o.source AS source,o.destination AS destination," +
             "o.user_count AS userCount,o.date AS date FROM user_order o " +
            "WHERE o.user_id = ?1 AND o.order_status = 2 " +
            "ORDER BY o.create_time DESC ",nativeQuery = true)
    List<UserDoneOrderDTO> findUserDoneOrderByUserId(String userId);
}
