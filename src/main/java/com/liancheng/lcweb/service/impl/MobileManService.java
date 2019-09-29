package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.constant.MessagesConstant;
import com.liancheng.lcweb.constant.PushModuleConstant;
import com.liancheng.lcweb.converter.Driver2DriverDTOConverter;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Line;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.*;
import com.liancheng.lcweb.enums.DriverStatusEnums;
import com.liancheng.lcweb.enums.OrderStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.exception.ManagerException;
import com.liancheng.lcweb.form.Message2DriverForm;
import com.liancheng.lcweb.form.UserLoginForm;
import com.liancheng.lcweb.form.addDriverFormForManager;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.repository.LineTotalRepository;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.repository.OrderRepository;
import com.liancheng.lcweb.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MobileManService implements ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private DriverService driverService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private LineService lineService;

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private LineTotalRepository lineTotalRepository;

    @Autowired
    private PushServiceWithImpl pushServiceWithImpl;

    @Override
    public List<Manager> findAll() {
        return managerRepository.findAll();
    }

    @Override
    public List<Manager> findAllByLineId(Integer lineId) {
        return managerRepository.findByLineId(lineId);
    }

    @Override
    public Manager findOne(String telNum) {
        Optional<Manager> manager = managerRepository.findById(telNum);

        return manager.orElse(null);
    }

    public ManagerDTO login(UserLoginForm form){
        Manager manager = findOne(form.getMobile());
        if (manager==null){
            throw new LcException(ResultEnums.NO_SUCH_MANAGER);
        }else if (!manager.getPassword().equals(form.getPassword())){
            throw new LcException(ResultEnums.PASSWORD_MATCHES_ERROR);
        }else {
            Line line = lineService.findOne(manager.getLineId());
            ManagerDTO m = new ManagerDTO();
            m.setLineId(line.getLineId());
            m.setLineName(line.getLineName1());
            m.setName(manager.getName());
            m.setTelNum(manager.getTelNum());
            return m;
        }

    }

    @Override
    public Line setLinePrice(Integer lineId, String price) {
        Line line = lineService.findOne(lineId);
        line.setPrice(price);
        return lineService.saveOne(line);
    }

    @Override
    public List<DriverDTO> getDriversByStatus(Integer lineId, Integer status) {
        List<Driver> drivers = new ArrayList<>();
        switch (status){
            case -1 :
                drivers.addAll(driverService.certainLIneToVerify(lineId));
                break;
            case  0 :
                drivers.addAll(driverService.certainLIneAtrest(lineId));
                break;
            case  1 :
                drivers.addAll(driverService.certainLIneOnroad(lineId));
                break;
            case  2 : // 待出行
                drivers.addAll(driverService.certainLineAvailable1(lineId));
                break;
            case  3 : // 待返程
                drivers.addAll(driverService.certainLineAvailable2(lineId));
                break;
            default:
                throw new LcException(ResultEnums.DRIVER_STATUS_ERROR);
        }
        return Driver2DriverDTOConverter.convert(drivers);
    }

    @Override
    public List<DriverDTO> getAllDrivers(Integer lineId) {
        return Driver2DriverDTOConverter.convert(driverService.findbyLineId(lineId));
    }

    @Override
    public List<Order> getOrdersByStatus(Integer lineId, Integer status) {
        return orderRepository.findByOrderStatusAndLineIdOrderByUpdateTimeDesc(status, lineId);

    }

    @Override
    public List<Order> getAllOrders(Integer lineId) {
        return orderRepository.findByLineId(lineId);
    }



    @Override
    @Transactional
    public void confirmOneOrder(Order order, String dnum) {

        Driver driver = driverService.findOne(dnum);

        Line line = lineService.findOne(order.getLineId());

        // A->B
        if (line.getLineName1().equals(order.getLineName())){
            // 需要待出行司机
            // 验证司机是否符合条件
            if (driver.getStatus().equals(DriverStatusEnums.AVAILABLE.getCode())&&
                    driver.getAvailableSeats()>=order.getUserCount()){
                orderService.confirmOne(order,driver);
            }
            else if(!driver.getStatus().equals(DriverStatusEnums.AVAILABLE.getCode())){
                log.error("分配订单时司机状态错误");
                throw new LcException(ResultEnums.DRIVER_STATUS_ERROR.getCode(),
                                      ResultEnums.DRIVER_STATUS_ERROR.getMsg()+", 需要待出行状态司机");
            }
            else {
                // 分配时自己座位数不足
                throw new LcException(ResultEnums.SEATS_NOT_ENOUGH);
            }
        }else {
            // 需要待返程司机
            // 验证司机是否符合条件
            if (driver.getStatus().equals(DriverStatusEnums.AVAILABLE2.getCode())&&
                    driver.getAvailableSeats()>=order.getUserCount()){
                orderService.confirmOne(order,driver);
            }
            else if(!driver.getStatus().equals(DriverStatusEnums.AVAILABLE2.getCode())){
                log.error("分配订单时司机状态错误");
                throw new LcException(ResultEnums.DRIVER_STATUS_ERROR.getCode(),
                        ResultEnums.DRIVER_STATUS_ERROR.getMsg()+", 需要待返程状态司机");
            }
            else {
                // 分配时司机可用座位数目不足
                throw new LcException(ResultEnums.SEATS_NOT_ENOUGH);            }
        }

        List<Manager> managers = findAllByLineId(order.getLineId());
        Manager manager = managers.get(0);

        String msg = MessagesConstant.changeStatus+manager.getTelNum();

        try {
            messagesService.createMessage(dnum,msg,MessagesConstant.type1);
            PushDTO driverPushDTO = new PushDTO(PushModuleConstant.TITLE,msg,2,PushModuleConstant.platform,"", dnum);
            pushServiceWithImpl.pushMessage2Driver(driverPushDTO);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("向司机发送即时消息失败,dnum={},message={}",dnum,e.getMessage());
        }

        try {
            messagesService.createMessage(order.getUserId(),msg,MessagesConstant.type1);
            PushDTO userPushDTO = new PushDTO(PushModuleConstant.TITLE,msg,2,PushModuleConstant.platform,"",order.getUserId());
            pushServiceWithImpl.pushMessage2User(userPushDTO);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("向乘客发送即时消息失败,userId={},message={}",order.getUserId(),e.getMessage());
        }
    }

    @Override
    public void cancelOneOrder(String orderId, Integer lineId) {

        Order order = orderService.findOne(orderId);

        if (!order.getLineId().equals(lineId)){
            log.error("非本线路的订单，将返回查无此订单的错误,lineId={}！",lineId);
            throw new LcException(ResultEnums.ORDER_NOT_FOUND);
        }

        orderService.cancelOne(order);

        List<Manager> managers = findAllByLineId(order.getLineId());
        Manager manager = managers.get(0);

        String msg = MessagesConstant.cancelStatus+manager.getTelNum();
        String dnum = order.getDnum();

        // 如果已经分配了司机的情况：
        if (dnum!=null&& !StringUtils.isEmpty(dnum)){
            try {
                // 没有写groupname，直接推到单独的司机头上
                PushDTO driverPushDTO = new PushDTO(PushModuleConstant.TITLE,msg,2,PushModuleConstant.platform,"", dnum);
                messagesService.createMessage(dnum,msg,MessagesConstant.type1);
                pushServiceWithImpl.pushMessage2Driver(driverPushDTO);
            }catch (Exception e){
                e.printStackTrace();
                log.warn("向司机发送即时消息失败,dnum={},errormessage={}",dnum,e.getMessage());
            }
        }

        try {
            PushDTO userPushDTO = new PushDTO(PushModuleConstant.TITLE,msg,2,PushModuleConstant.platform,"",order.getUserId());
            messagesService.createMessage(order.getUserId(),msg,MessagesConstant.type1);
            pushServiceWithImpl.pushMessage2User(userPushDTO);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("向乘客发送即时消息失败,userId={},message={}",order.getUserId(),e.getMessage());
        }
    }
    @Override
    public TotalInfoDTO getTotal(Integer lineId) {
        // 完整的建议电脑端查看，移动端只填充部分字段
        // 其他字段将显示"请于电脑端查看"
        return null;
    }


    @Override
    public MessageNumDTO getMessages(Integer lineId) {
        // 计算未读消息，电脑端实时计算用
        return null;
    }
    @Override
    public void postMessages(Integer lineId, Message2DriverForm message2DriverForm) {
        // 建议电脑端使用
    }
    @Override
    public Page<Order> getOrdersByStatus(Integer lineId, Integer status, Pageable pageable) { return null;}
    @Override
    public Page<Order> getAllOrders(Integer lineId, Pageable pageable) { return null; }
    @Override
    public void AddOneDriver(addDriverFormForManager driverInfoForm, Integer lineId) {}
    @Override
    public void DeleteOneDriver(String dnum, Integer lineId) {}
    @Override
    public void confirmOneDriver(String dnum, Integer lineId) {}
    @Override
    public Manager getManager(String telNum, String password) {return null;}
    @Override
    public Manager addManager(Manager manager) {return null;}
    @Override
    public Page<DriverDTO> getDriversByStatus(Integer lineId, Integer status, Pageable pageable) {return null;}
    @Override
    public Page<DriverDTO> getAllDrivers(Integer lineId, Pageable pageable) {return null;}
}
