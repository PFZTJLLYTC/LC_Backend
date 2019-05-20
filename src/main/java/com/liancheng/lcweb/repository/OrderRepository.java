package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.DriverDoneOrderDTO;
import com.liancheng.lcweb.dto.OrderDriDTO;
import com.liancheng.lcweb.dto.UserDoneOrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    //todo 虽然我依旧完成了分页，但是我觉得应该改order类目的line！
    @Query(value = "select * from user_order as o where o.line = (select line from manager  where line_id = ?1 ) \n#pageable\n  ",
            countQuery = "select count(*) from user_order as o where o.line = (select line from manager  where line_id = ?1 )" ,
            nativeQuery = true)
    Page<Order> findByLineId(Integer lieId, Pageable pageable);

    List<Order> findByOrderStatus(Integer orderStatus);

    List<Order> findByOrderStatusAndUserId(Integer orderStatus,String userId);

    @Query(value = "SELECT * FROM user_order WHERE user_id = ?3 AND (order_status = ?1 OR order_status = ?2) ",nativeQuery = true)
    List<Order> findByOrderStatusAndUserId(Integer orderStatusOne,Integer orderStatusTwo,String userId);

    @Query(value = "SELECT o.car_num ,o.source ,o.destination," +
            "o.user_count,o.date FROM user_order o " +
            "WHERE o.user_id = ?1 AND o.order_status = 2 " +
            "ORDER BY o.create_time DESC ",nativeQuery = true)
    List<UserDoneOrderDTO> findUserDoneOrderByUserId(String userId);

    List<Order> findByOrderStatusAndDnum(Integer orderStatus,String dnum);

    @Query(value = "SELECT o.car_num ,o.source ,o.destination," +
            "o.user_count,o.date FROM user_order o " +
            "WHERE o.dnum = ?1 AND o.order_status = 2 " +
            "ORDER BY o.create_time DESC ",nativeQuery = true)
    List<DriverDoneOrderDTO> findDriverDoneOrderByDnum(String dnum);
}
