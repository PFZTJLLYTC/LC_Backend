package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.constant.MessagesConstant;
import com.liancheng.lcweb.constant.PushModuleConstant;
import com.liancheng.lcweb.converter.Order2OrderDriDTOConverter;
import com.liancheng.lcweb.converter.Order2UserOrderDTOConverter;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.domain.User;
import com.liancheng.lcweb.dto.OrderDriDTO;
import com.liancheng.lcweb.dto.PushDTO;
import com.liancheng.lcweb.dto.UserOrderDTO;
import com.liancheng.lcweb.enums.OrderStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.form.UserOrderForm;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.repository.OrderRepository;
import com.liancheng.lcweb.repository.UserRepository;
import com.liancheng.lcweb.service.*;
import com.liancheng.lcweb.utils.KeyUtil;
import io.lettuce.core.models.role.RedisSlaveInstance;
import lombok.extern.slf4j.Slf4j;
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
    private PushServiceWithImpl pushServiceWithImpl;

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


        Integer lineId=lineService.findLineIdByLineName(userOrderForm.getLineName());
        // 电脑端消息推送
        try {
            webSocketService.sendInfo("新的订单消息",lineId+"");
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        // 移动端消息推送
        // todo 两种方法--- 1. 一个线路一个群组 2. 找到这条线路所有管理员，分别推送
        // 目前考虑一条线路管理员不多，采取方法2
        List<Manager> managers = managerRepository.findByLineId(lineId);
        try {
            for (Manager manager : managers){
                PushDTO managerPushDTO = new PushDTO(
                        PushModuleConstant.MANAGER_TITLE1,
                        "请即时处理新的订单请求",
                        2,
                        PushModuleConstant.manager_platform,
                        "",
                        manager.getTelNum());
                pushServiceWithImpl.pushMessage2Manager(managerPushDTO);
            }

        }catch (Exception e){
            // 其实大概率没有失败，只是有个异常而已
            log.warn("向线路管理员发送即时消息失败,lineId={},message={}",lineId,e.getMessage());
        }
    }

    @Override
    public void changeOneInfo(String userId, String orderId, UserOrderForm changeForm) {
        Order order = findOne(orderId);
        if(order==null || !order.getUserId().equals(userId)){
            throw new LcException(ResultEnums.ORDER_NOT_FOUND);
        }
        if (!order.getOrderStatus().equals(OrderStatusEnums.WAIT.getCode())){
            throw new LcException(ResultEnums.ORDER_STATUS_ERROR);
        }
        order.setUserPhone(changeForm.getUserPhone());
        order.setTime(changeForm.getTime());
        order.setUserCount(changeForm.getUserCount());
        order.setDetailAddress(changeForm.getDetailAddress());
        order.setDetailDestination(changeForm.getDetailDestination());
        order.setStartLon(changeForm.getStartLon());
        order.setStartLat(changeForm.getStartLat());
        order.setEndLon(changeForm.getEndLon());
        order.setEndLat(changeForm.getEndLat());
        order.setRemark(changeForm.getRemark());

        orderRepository.save(order);
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

    /*********************操作相关**************************/

    @Override
    public Order confirmOne(Order order, Driver driver) {
        //订单加上司机信息
        order.setCarNum(driver.getCarNum());
        order.setDnum(driver.getDnum());
        order.setDriverName(driver.getName());
//        order.setDate(LocalDate.now().toString());

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
//        order.setDate(LocalDate.now().toString());
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
            messagesService.createMessage(order.getUserId(),msg, MessagesConstant.type1);
            PushDTO userPushDTO = new PushDTO(PushModuleConstant.TITLE,msg,2,PushModuleConstant.platform,"",order.getUserId());
            pushServiceWithImpl.pushMessage2User(userPushDTO);
//            webSocketService.sendInfo(msg,order.getUserId());
        } catch (Exception e) {
            log.warn("向用户发订单状态更新消息失败,userId = {}",order.getUserId());
        }
        try {
            messagesService.createMessage(order.getDnum(),msg,MessagesConstant.type1);
            PushDTO driverPushDTO = new PushDTO(PushModuleConstant.TITLE,msg,2,PushModuleConstant.platform,"", order.getDnum());
            pushServiceWithImpl.pushMessage2Driver(driverPushDTO);
//            webSocketService.sendInfo(msg,order.getDnum());
        } catch (Exception e) {
            log.warn("向司机发订单状态更新消息失败,userId = {}",order.getDnum());
        }

        return order;
    }

    @Override
    public void cancelOne(Order order) {
        if (order.getDnum()!=null&&!StringUtils.isEmpty(order.getDnum())){
            Driver driver = driverService.findOne(order.getDnum());

            // 状态不改变, 座位数改变
            if(driver.getSeatType()==0&&driver.getAvailableSeats()+order.getUserCount()>=4){
                driver.setAvailableSeats(4);
            }else if (driver.getSeatType()==1&&driver.getAvailableSeats()+order.getUserCount()>=7){
                driver.setAvailableSeats(7);
            }else {
                driver.setAvailableSeats(driver.getAvailableSeats()+order.getUserCount());
            }
//            driver.setAvailableSeats(driver.getAvailableSeats()+order.getUserCount());
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
        return orderRepository.findByOrderStatusOrderByUpdateTimeDesc(OrderStatusEnums.WAIT.getCode());
    }

    @Override
    public List<Order> findAllProcessing() {
        return orderRepository.findByOrderStatusOrderByUpdateTimeDesc(OrderStatusEnums.PROCESSIN.getCode());
    }

    @Override
    public List<Order> findAllDone() {
        return orderRepository.findByOrderStatusOrderByUpdateTimeDesc(OrderStatusEnums.DONE.getCode());
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
//        Order order = findOne(orderId);
//        if (order!=null) {
//            cancelOne(order);
//        }
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<OrderDriDTO> findDriverOrderByStatus(Integer status, String dnum){
        return Order2OrderDriDTOConverter
                .convert(orderRepository.
                        findByOrderStatusAndDnumOrderByUpdateTimeDesc(
                                status,dnum));
    }
}
