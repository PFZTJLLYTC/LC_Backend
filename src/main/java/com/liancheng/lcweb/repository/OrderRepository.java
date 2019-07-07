package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.DriverDoneOrderDTO;
import lombok.Value;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,String> {

    List<Order> findByUserId(String userId);

    List<Order> findByDnum(String dnum);

    //用all来筛选好像还更快一些
//    List<Order> findByLineIdAndOrderStatus(Integer lineId,Integer orderStatus);

    //@Query(value = "select * from user_order as o where o.line = (select line from manager  where line_id = ?1 ) ",nativeQuery = true)
    List<Order> findByLineId(Integer lineId);

//    @Query(value = "select * from user_order as o where o.line = (select line from manager  where line_id = ?1 ) \n#pageable\n  ",
//            countQuery = "select count(*) from user_order as o where o.line = (select line from manager  where line_id = ?1 )" ,
//            nativeQuery = true)
    Page<Order> findByLineId(Integer lieId, Pageable pageable);

    //按照月份和线路id进行查询
    @Query(value = " select * from user_order where line_id = ?1 and date like concat('%',?2,'%')", nativeQuery = true)
    List<Order> findByLineIdAndMonthOrYearDate(Integer lineId, String monthDate);

    //定时任务需要
    List<Order> findByLineIdAndDate(Integer lineId, String date);

    List<Order> findByOrderStatus(Integer orderStatus);

    List<Order> findByOrderStatusAndUserId(Integer orderStatus,String userId);

    List<Order> findByOrderStatusAndDnum(Integer orderStatus,String dnum);

    Page<Order> findByLineIdAndOrderStatus(Integer lineId,Integer orderStatus,Pageable pageable);

    @Query(value = "SELECT * FROM user_order WHERE user_id = ?3 AND (order_status = ?1 OR order_status = ?2) ",nativeQuery = true)
    List<Order> findByOrderStatusAndUserId(Integer orderStatusOne,Integer orderStatusTwo,String userId);


    //todo 需要改了，这两个方法
    @Query(value = "SELECT car_num ,source ,destination," +
            "user_count,user_order.date FROM user_order  " +
            "WHERE dnum = ?1 AND order_status = 2 " +
            "ORDER BY create_time DESC ",nativeQuery = true)
    List<DriverDoneOrderDTO> findDriverDoneOrderByDnum(String dnum);


    @Query(value = "SELECT COUNT(*) FROM  user_order WHERE order_status=2 and dnum=?1 and date=?2",nativeQuery=true)
    Integer findDriverTodayOrders(String dnum, LocalDate today);

    @Query(value ="SELECT IFNULL(SUM(user_count),0) FROM user_order WHERE order_status=2 and dnum=?1 and date=?2",nativeQuery = true)
    Integer findDriverTodayUsers(String dnum,LocalDate today);

}
