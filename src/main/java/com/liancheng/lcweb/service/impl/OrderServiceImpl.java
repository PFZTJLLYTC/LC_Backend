package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.converter.Order2UserOrderDTOConverter;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.domain.User;
import com.liancheng.lcweb.dto.DriverDoneOrderDTO;
import com.liancheng.lcweb.dto.OrderDriDTO;
import com.liancheng.lcweb.dto.UserOrderDTO;
import com.liancheng.lcweb.enums.OrderStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.exception.ManagerException;
import com.liancheng.lcweb.form.UserOrderForm;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.repository.OrderRepository;
import com.liancheng.lcweb.repository.UserRepository;
import com.liancheng.lcweb.service.*;
import com.liancheng.lcweb.utils.KeyUtil;
import com.liancheng.lcweb.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.liancheng.lcweb.constant.MessagesConstant.changeStatus;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private LineService lineService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessagesService messagesService;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override//创建的时候自动为新订单
    public void createOne(String userId,UserOrderForm userOrderForm) {
        Order result = new Order();
        BeanUtils.copyProperties(userOrderForm,result);
        result.setUserId(userId);
        result.setLineId(lineService.findLineIdByLineName(userOrderForm.getLineName()));
        result.setOrderId(KeyUtil.genUniquekey());
        result.setOrderStatus(OrderStatusEnums.WAIT.getCode());

        orderRepository.save(result);

        //通过lineId找到管理员们
        //websocket进行manager端提醒实现,传给manager

//        这个findOneByName()方法只按一个方向找，但用户下单时两个方向均可
//        Integer lineId = lineService.findOneByName(userOrderForm.getLineName()).getLineId();
        Integer lineId=lineService.findLineIdByLineName(userOrderForm.getLineName());
        try {
            webSocketService.sendInfo("新的订单消息",lineId+"");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public  List<UserOrderDTO> findUserTravelOrder(String userId){
        return Order2UserOrderDTOConverter
                .convert(orderRepository.findUserTravelOrders(userId));
    }
    @Override
    public List<UserOrderDTO> findUserOrderByStatus(Integer status,String userId){

        return Order2UserOrderDTOConverter
                .convert(orderRepository.
                        findByOrderStatusAndUserIdOrderByUpdateTimeDesc(
                        status, userId));

    }

    /***********************************************/

    @Override
    public Order confirmOne(Order order, Driver driver) {
        //订单加上司机信息
        order.setCarNum(driver.getCarNum());
        order.setDnum(driver.getDnum());
        order.setDriverName(driver.getName());
        order.setDate(LocalDate.now().toString());

        //改变司机信息并保存
        Integer original = driver.getAvailableSeats();
        driver.setAvailableSeats(original-order.getUserCount());
        driverRepository.save(driver);

        //改变订单状态
        order.setOrderStatus(OrderStatusEnums.PROCESSIN.getCode());

        return orderRepository.save(order);
    }

    @Override
    public Order finishOne(Order order) {
        // 订单完成后司机的worktime也需要增加
        order.setOrderStatus(OrderStatusEnums.DONE.getCode());
        order.setDate(LocalDate.now().toString());
        Driver driver = driverService.findOne(order.getDnum());
        User user = userService.findOne(order.getUserId());
        user.setTakeTimes(user.getTakeTimes()+1);
        //finish 才加一
        driver.setWorkTimes(driver.getWorkTimes()+1);
        //  座位数加回来
        if(driver.getSeatType()==0&&driver.getAvailableSeats()+order.getUserCount()>=4){
            driver.setAvailableSeats(4);
        }else if (driver.getSeatType()==1&&driver.getAvailableSeats()+order.getUserCount()>=7){
            driver.setAvailableSeats(7);
        }else {
            driver.setAvailableSeats(driver.getAvailableSeats()+order.getUserCount());
        }
        orderRepository.save(order);
        driverRepository.save(driver);
        userRepository.save(user);

        List<Manager> managers = managerRepository.findByLineId(order.getLineId());

        String msg = changeStatus+managers.get(0).getTelNum();

        //进行对用户的通知
        try {
            messagesService.createMessage(order.getUserId(),msg);
            webSocketService.sendInfo(msg,order.getUserId());
        } catch (IOException e) {
            log.warn("向用户发订单状态更新消息失败,userId = {}",order.getUserId());
        }
        try {
            messagesService.createMessage(order.getDnum(),msg);
            webSocketService.sendInfo(msg,order.getDnum());
        } catch (IOException e) {
            log.warn("向司机发订单状态更新消息失败,userId = {}",order.getDnum());
        }

        return order;
    }

    @Override
    public void cancelOne(Order order) {
        if (order.getDnum()!=null&&!StringUtils.isEmpty(order.getDnum())){
            Driver driver = driverService.findOne(order.getDnum());
            //状态不改变
            driver.setAvailableSeats(driver.getAvailableSeats()+order.getUserCount());
            driverRepository.save(driver);
        }
        //User user = userService.findOne(order.getUserId());
        orderRepository.delete(order);
    }

    @Override
    public Order findOne(String OrderId) {
        Optional<Order> order = orderRepository.findById(OrderId);
//        if (!order.isPresent()){
//            log.info("无此订单");
//            return null;
//            //不能在这里抛异常！！都要用这个函数
////            throw new ManagerException(ResultEnums.ORDER_NOT_FOUND.getMsg(),"manager/alldeals");
//        }
        return order.orElse(null);
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

    @Override
    public Integer findDriverTodayOrders(String dnum, LocalDate today){
        return orderRepository.findDriverTodayOrders(dnum,today);
    }

    @Override
    public Integer findDriverTodayUsers(String dnum,LocalDate today){
        return orderRepository.findDriverTodayUsers(dnum,today);
    }

    @Override
    public void deleteByOrderId(String orderId){
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<UserOrderDTO> findDriverOrderByStatus(Integer status,String dnum){
        return Order2UserOrderDTOConverter
                .convert(orderRepository.
                        findByOrderStatusAndDnumOrderByUpdateTimeDesc(
                                status,dnum));
    }
}
