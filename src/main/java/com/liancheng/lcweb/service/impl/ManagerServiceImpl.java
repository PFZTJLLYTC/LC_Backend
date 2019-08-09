package com.liancheng.lcweb.service.impl;
import com.liancheng.lcweb.constant.MessagesConstant;
import com.liancheng.lcweb.constant.PushModuleConstant;
import com.liancheng.lcweb.converter.Driver2DriverDTOConverter;
import com.liancheng.lcweb.converter.String2DateConverter;
import com.liancheng.lcweb.domain.*;
import com.liancheng.lcweb.dto.DriverDTO;
import com.liancheng.lcweb.dto.MessageNumDTO;
import com.liancheng.lcweb.dto.PushDTO;
import com.liancheng.lcweb.dto.TotalInfoDTO;
import com.liancheng.lcweb.enums.DriverStatusEnums;
import com.liancheng.lcweb.enums.OrderStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.exception.ManagerException;
import com.liancheng.lcweb.form.DriverInfoForm;
import com.liancheng.lcweb.form.Message2DriverForm;
import com.liancheng.lcweb.form.addDriverFormForManager;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.repository.LineTotalRepository;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.repository.OrderRepository;
import com.liancheng.lcweb.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ManagerServiceImpl implements ManagerService {

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
    private WebSocketService webSocketService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private LineService lineService;

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private LineTotalRepository lineTotalRepository;

    @Autowired
    private PushServiceWithImpl pushServiceWithImpl;




    @Override
    public MessageNumDTO getMessages(Integer lineId) {



        Integer orderMessages = getOrdersByStatus(lineId,OrderStatusEnums.WAIT.getCode()).size();

        Integer driverMessages = getDriversByStatus(lineId,DriverStatusEnums.TO_BE_VERIFIED.getCode()).size();

        return new MessageNumDTO(driverMessages,orderMessages);


    }

    @Override
    public List<Manager> findAll() {
        return managerRepository.findAll();
    }


    @Override
    public Manager findOne(String telNum) {

        Optional<Manager> manager = managerRepository.findById(telNum);
        if (!manager.isPresent()){
            return null;
        }
        return manager.get();
    }

    @Override
    public List<Manager> findAllByLineId(Integer lineId) {
        return managerRepository.findByLineId(lineId);
    }

    /*登陆用*/
    @Override
    public Manager getManager(String telNum, String password) {
        Manager manager = managerRepository.findByTelNumAndPassword(telNum,password);
        if (manager==null){
            log.error("没有此线路负责人");
            return null;
        }
        return manager;
    }


    @Override
    public void deleteOne(String telNum) {
        if (findOne(telNum)!=null){
            log.error("删除管理员失败,无此管理员");
            throw new LcException(ResultEnums.NO_SUCH_MANAGER);
        }
//        //所属司机全部删除，但是原有订单不删除。
//        for (Driver driver : driverService.findbyLineId(lineId)){
//            driverService.deleteOne(driver.getDnum());
//        }
        //级联删除
        managerRepository.deleteById(telNum);
    }

    @Override
    public Manager addManager(Manager manager) {
        Manager result = new Manager();
        BeanUtils.copyProperties(manager,result);
        return managerRepository.save(result);
    }

    @Override
    public void AddOneDriver(addDriverFormForManager driverInfoForm, Integer lineId) {

        Driver driver = new Driver();
//        String lineName = lineService.findOne(lineId).getLineName1();
        //因为根本不传line字段，它根本不能添加其他线路的司机
        BeanUtils.copyProperties(driverInfoForm,driver);
        if (driver.getAvailableSeats()==4){
            driver.setSeatType(0);
        }else if (driver.getAvailableSeats()==7){
            driver.setSeatType(1);
        }else {
            throw new ManagerException(ResultEnums.SEATS_TYPE_ERROR.getMsg(),"/manager/driver/goToAddDriver");
        }

        try {
            driver.setBirthday(String2DateConverter.convert(driverInfoForm.getBirthday()));
        } catch (ParseException e) {
            //暂时不抛回去，保证用户友好
            log.error("日期格式转换错误,lineId={}",lineId);
            log.error(e.getMessage());
        }
        driver.setLineId(lineId);
        //不要再确认一次，极高权限！
        driver.setStatus(DriverStatusEnums.ATREST.getCode());
        driver.setWorkTimes(0);
        driverRepository.save(driver);
        messagesService.createMessage(driver.getDnum(),MessagesConstant.welcomeDriver);
    }



    @Override
    public Page<DriverDTO> getDriversByStatus(Integer lineId, Integer status,Pageable pageable) {

        if (status<-1||status>3){
            log.error("根本没有这个状态");
            throw new ManagerException(ResultEnums.DRIVER_STATUS_ERROR.getMsg(),"/manager/drivers");
        }
        Page<Driver>  driverPage;
        if (status.equals(DriverStatusEnums.ATREST.getCode())){
            driverPage = driverService.certainLIneAtrest(lineId,pageable);
        }
        else if (status.equals(DriverStatusEnums.AVAILABLE.getCode())||status.equals(DriverStatusEnums.AVAILABLE2.getCode())){
            driverPage = driverService.certainLIneAvailable(lineId,pageable);
        }
        else if (status.equals(DriverStatusEnums.ONROAD.getCode())){
            driverPage = driverService.certainLIneOnroad(lineId,pageable);
        }
        else {
            driverPage = driverService.certainLIneToVerify(lineId,pageable);
        }
        List<DriverDTO> driverDTOList =Driver2DriverDTOConverter.convert(driverPage.getContent());

        Page<DriverDTO> driverDTOPage = new PageImpl<>(driverDTOList,pageable,driverPage.getTotalElements());
        return driverDTOPage;
    }

    @Override
    public List<DriverDTO> getDriversByStatus(Integer lineId, Integer status) {

        if (status<-1||status>3){
            log.error("根本没有这个状态");
            throw new ManagerException(ResultEnums.DRIVER_STATUS_ERROR.getMsg(),"/manager/drivers");
        }
        List<Driver>  driverList ;
        if (status.equals(DriverStatusEnums.ATREST.getCode())){
            driverList = driverService.certainLIneAtrest(lineId);
        }
        else if (status.equals(DriverStatusEnums.AVAILABLE.getCode())||status.equals(DriverStatusEnums.AVAILABLE2.getCode())){
            driverList = driverService.certainLIneAvailable(lineId);
        }
        else if (status.equals(DriverStatusEnums.ONROAD.getCode())){
            driverList = driverService.certainLIneOnroad(lineId);
        }
        else {
            driverList = driverService.certainLIneToVerify(lineId);
        }
        List<DriverDTO> driverDTOList = Driver2DriverDTOConverter.convert(driverList);

        return driverDTOList;
    }


    @Override
    //今日的情况,今日当场算
    public TotalInfoDTO getTotal(Integer lineId) {
        TotalInfoDTO totalInfoDTO = new TotalInfoDTO();

        //得时间
//        Date currentDate =new Date();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        String dateString = formatter.format(currentDate);
        LocalDate date = LocalDate.now();
        totalInfoDTO.setDate(date.toString());

        //找当前活跃司机总数
        List<DriverDTO> driverList1 = getDriversByStatus(lineId,DriverStatusEnums.ONROAD.getCode());
        //没问题，查一个状态就能出两个的
        List<DriverDTO> driverList2 = getDriversByStatus(lineId,DriverStatusEnums.AVAILABLE.getCode());
        Integer liveNum = driverList1.size()+driverList2.size();
        totalInfoDTO.setLiveDrivers(liveNum);

        //当天当前订单总数，根据日期来找
        List<Order> orders = orderRepository.findByLineIdAndDate(lineId,date.toString());

        totalInfoDTO.setOrderNum(orders.size());

        //当前总载客人数,通过订单时期和订单人数来找
        Integer userCount = 0;
        for (Order o :orders){
            userCount+=o.getUserCount();
        }
        totalInfoDTO.setTotalUserNum(userCount);
        totalInfoDTO.setCompareLWithLL("0%");
        //昨日概况里面找
        LineTotal lineTotal = lineTotalRepository.findByLineIdAndDateAndType(lineId,date.minusDays(1).toString(),0);
        if (lineTotal!=null){
            totalInfoDTO.setCompareLWithLL(lineTotal.getCompare());
        }

//        log.info(totalInfoDTO.getDate().substring(0,7));
        List<LineTotal> lineTotals = lineTotalRepository.
                findByLineIdAndTypeAndYeardate(
                        lineId,
                        0,
                        totalInfoDTO.getDate().substring(0,7)
                );
        Integer monthOrderCount =0;
        Integer monthUserCount = 0;
        for (LineTotal l : lineTotals){
            monthOrderCount+=l.getOrderCount();
            monthUserCount+=l.getUserCount();
        }
        //加上今天的情况
        totalInfoDTO.setMonthOrderCount(monthOrderCount+totalInfoDTO.getOrderNum());
        totalInfoDTO.setMonthUserCount(monthUserCount+totalInfoDTO.getTotalUserNum());

        return totalInfoDTO;

    }

    @Override
    public Page<DriverDTO> getAllDrivers(Integer lineId, Pageable pageable) {
        if (lineService.findOne(lineId)==null){
            return null;
        }
        Page<Driver> driverPage = driverService.findbyLineId(lineId,pageable);

        List<DriverDTO> driverDTOList = Driver2DriverDTOConverter.convert(driverPage.getContent());

        Page<DriverDTO> driverDTOPage = new PageImpl<>(driverDTOList,pageable,driverPage.getTotalElements());

        return driverDTOPage;

    }

    @Override
    public List<Driver> getAllDrivers(Integer lineId) {
        if (lineService.findOne(lineId)==null){
            return null;
        }
        return driverService.findbyLineId(lineId);

    }

    @Override
    public List<Order> getAllOrders(Integer lineId) {
        List<Order> orders = orderRepository.findByLineId(lineId);
        return orders;

    }

    //实现分页的全部线路订单
    @Override
    public Page<Order> getAllOrders(Integer lineId, Pageable pageable) {
//        return orderRepository.findByLineId(lineId,pageable);
        return orderRepository.findByLineId(lineId,pageable);
    }

    @Override
    public void DeleteOneDriver(String dnum, Integer lineId) {
        Driver driver = driverService.findOne(dnum);
        if (!driverService.findbyLineId(lineId).contains(driver)){
            log.error("不可删非本线路司机，line={},dnum={}",lineId,dnum);
            //需要后台警告一下md，直接显示没有这个司机比较好
            throw new ManagerException(ResultEnums.NO_SUCH_DRIVER.getMsg(),"/manager/driver/allDrivers");
        }
        //前面进行了判断，因此对driverservice简化
        driverService.deleteOne(dnum);
    }

    @Override
    public void confirmOneOrder(Order order,String dnum) {

        //找司机
        Driver driver = driverService.findOne(dnum);

        Line line = lineService.findOne(order.getLineId());

        if (line.getLineName1().equals(order.getLineName())){
            //待出行司机
            //验证司机是否符合条件
            if (driver.getStatus().equals(DriverStatusEnums.AVAILABLE.getCode())&&
                    driver.getAvailableSeats()>=order.getUserCount()){
                Order result =  orderService.confirmOne(order,driver);
            }
            else if(!driver.getStatus().equals(DriverStatusEnums.AVAILABLE.getCode())){
                log.error("分配订单时司机状态错误");
                throw new ManagerException(ResultEnums.DRIVER_STATUS_ERROR.getMsg()+"，需要待出行状态司机!","/manager/order/findByStatus?status="+ OrderStatusEnums.WAIT.getCode());
            }
            else {
                log.error("分配时司机可用座位数目不足");
                throw new ManagerException(ResultEnums.SEATS_NOT_ENOUGH.getMsg(),"/manager/order/findByStatus?status="+ OrderStatusEnums.WAIT.getCode());
            }
        }else {
            //待返程司机
            //验证司机是否符合条件
            if (driver.getStatus().equals(DriverStatusEnums.AVAILABLE2.getCode())&&
                    driver.getAvailableSeats()>=order.getUserCount()){
                Order result =  orderService.confirmOne(order,driver);
            }
            else if(!driver.getStatus().equals(DriverStatusEnums.AVAILABLE2.getCode())){
                log.error("分配订单时司机状态错误");
                throw new ManagerException(ResultEnums.DRIVER_STATUS_ERROR.getMsg()+"，需要待返程状态司机!","/manager/order/findByStatus?status="+ OrderStatusEnums.WAIT.getCode());
            }
            else {
                log.error("分配时司机可用座位数目不足");
                throw new ManagerException(ResultEnums.SEATS_NOT_ENOUGH.getMsg(),"/manager/order/findByStatus?status="+ OrderStatusEnums.WAIT.getCode());
            }
        }

        List<Manager> managers = managerService.findAllByLineId(order.getLineId());
        Manager manager = managers.get(0);

        String msg = MessagesConstant.changeStatus+manager.getTelNum();

        try {
            messagesService.createMessage(dnum,msg);
            PushDTO driverPushDTO = new PushDTO(PushModuleConstant.TITLE,msg,2,PushModuleConstant.platform,"", dnum);
            pushServiceWithImpl.pushMessage2Driver(driverPushDTO);
//            webSocketService.sendInfo(msg,dnum);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("向司机发送即时消息失败,dnum={},message={}",dnum,e.getMessage());
        }
        //发通知
        try {
            messagesService.createMessage(order.getUserId(),msg);
            PushDTO userPushDTO = new PushDTO(PushModuleConstant.TITLE,msg,2,PushModuleConstant.platform,"",order.getUserId());
            pushServiceWithImpl.pushMessage2User(userPushDTO);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("向乘客发送即时消息失败,userId={},message={}",order.getUserId(),e.getMessage());
        }
//        try {
//            messagesService.createMessage(order.getUserId(),msg);
//            webSocketService.sendInfo(msg,order.getUserId());
//        } catch (IOException e) {
//            log.warn("向乘客发送即时消息失败,userId={},message={}",order.getUserId(),e.getMessage());
//        }
    }

    @Override
    public void cancelOneOrder(String orderId, Integer lineId) {
        Order order = orderService.findOne(orderId);

        if (!order.getLineId().equals(lineId)){
            log.error("非本线路的订单，将返回查无此订单的错误,lineId={}！",lineId);
            throw new ManagerException(ResultEnums.ORDER_NOT_FOUND.getMsg(),"/manager/order/findByStatus?status="+OrderStatusEnums.PROCESSIN.getCode());
        }

//        String userId = order.getUserId();
        String dnum = order.getDnum();
        orderService.cancelOne(order);

        List<Manager> managers = managerService.findAllByLineId(order.getLineId());
        Manager manager = managers.get(0);

        String msg = MessagesConstant.cancelStatus+manager.getTelNum();

        //如果已经分配了司机的情况：
        if (dnum!=null&& !StringUtils.isEmpty(dnum)){
            try {
                //没有写groupname，直接推到单独的司机头上
                PushDTO driverPushDTO = new PushDTO(PushModuleConstant.TITLE,msg,2,PushModuleConstant.platform,"", dnum);
                messagesService.createMessage(dnum,msg);
//                webSocketService.sendInfo(msg,dnum);
                pushServiceWithImpl.pushMessage2Driver(driverPushDTO);
            }catch (Exception e){
                e.printStackTrace();
                log.warn("向司机发送即时消息失败,dnum={},errormessage={}",dnum,e.getMessage());
            }
        }

        try {
            PushDTO userPushDTO = new PushDTO(PushModuleConstant.TITLE,msg,2,PushModuleConstant.platform,"",order.getUserId());
            messagesService.createMessage(order.getUserId(),msg);
            pushServiceWithImpl.pushMessage2User(userPushDTO);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("向乘客发送即时消息失败,userId={},message={}",order.getUserId(),e.getMessage());
        }
//        try {
//            messagesService.createMessage(userId,msg);
//            webSocketService.sendInfo(msg,userId);
//        }catch (IOException e){
//            log.warn("向乘客发送即时消息失败,userId={},errormessage={}",userId,e.getMessage());
//        }
    }

    //保留，以后说不定有用
    @Override
    public List<Order> getOrdersByStatus(Integer lineId, Integer status) {

        List<Order> allOrders = getAllOrders(lineId);
        if (CollectionUtils.isEmpty(allOrders))return allOrders;
        List<Order> result = new ArrayList<>();
        switch (status){
            //未处理
            case 0:
                for (Order order : allOrders){
                    if (order.getOrderStatus().equals(0)){
                        result.add(order);
                    }
                }
                break;
            //进行中
            case 1:
                for (Order order : allOrders){
                    if (order.getOrderStatus().equals(1)){
                        result.add(order);
                    }
                }
                break;
            //已完成
            case 2:
                for (Order order : allOrders){
                    if (order.getOrderStatus().equals(2)){
                        result.add(order);
                    }
                }
                break;
            default:
                throw new ManagerException(ResultEnums.ORDER_STATUS_ERROR.getMsg(),"/manager/order/allOrders");

        }
        return result;

    }

    @Override
    public Page<Order> getOrdersByStatus(Integer lineId, Integer status, Pageable pageable) {

        if (status<0||status>2){
            throw new ManagerException(ResultEnums.ORDER_STATUS_ERROR.getMsg(),"/manager/order/allOrders");
        }

        //未处理订单就是应该早的放前面！
        if (status.equals(OrderStatusEnums.WAIT.getCode())){
            PageRequest pageRequest = new PageRequest(pageable.getPageNumber(),pageable.getPageSize(),new Sort(Sort.Direction.ASC,"createTime"));
            return orderRepository.findByLineIdAndOrderStatus(lineId,status,pageRequest);
        }
        return orderRepository.findByLineIdAndOrderStatus(lineId,status,pageable);

    }

    @Override
    public void confirmOneDriver(String dnum, Integer lineId) {

        Driver driver = driverService.findOne(dnum);
        if (!driver.getLineId().equals(lineId)){
            log.error("线路{}，想确定其他路的司机注册信息,dum={}，建议安排它一手！",lineId,dnum);
            throw new ManagerException(ResultEnums.NO_SUCH_DRIVER.getMsg(),"/manager/driver/findBysStatus?status="+DriverStatusEnums.TO_BE_VERIFIED.getCode());

        }
        //将状态设置为休息中，代表成功确定
        driver.setStatus(DriverStatusEnums.ATREST.getCode());

        driver.setWorkTimes(0);

        //保存更改,正式生效(是否对driver进行通知？)
        driverRepository.save(driver);
        messagesService.createMessage(dnum,MessagesConstant.welcomeDriver);
    }


    @Override
    public void postMessages(Integer lineId, Message2DriverForm message2DriverForm) {
        if (message2DriverForm.getMessage().isEmpty()){
            log.error("线路{}，想发送无内容消息，建议安排它一手！",lineId);
            throw new ManagerException(ResultEnums.NO_CERTAIN_CONTENT_MESSAGE.getMsg(),"/manager/goContactAndHelp");
        }
        //存 message
        //注意，先存库再发送！
        if((message2DriverForm.getDnum()!=null)&&!message2DriverForm.getDnum().isEmpty()){
            Driver driver = driverService.findOne(message2DriverForm.getDnum());
            if (driver!=null&&driver.getLineId().equals(lineId)){
                try {
//                    Messages messages = new Messages();
//                    messages.setTarget(driver.getDnum());
//                    messages.setMessage(message2DriverForm.getMessage());
                    messagesService.createMessage(driver.getDnum(),message2DriverForm.getMessage());
                    PushDTO driverPushDTO = new PushDTO(PushModuleConstant.TITLE,message2DriverForm.getMessage(),2,PushModuleConstant.platform,"", message2DriverForm.getDnum());
                    pushServiceWithImpl.pushMessage2Driver(driverPushDTO);
//                    webSocketService.sendInfo(message2DriverForm.getMessage(),driver.getDnum());
                }catch (Exception e){
                    log.warn("向指定司机发送及时消息失败, dnum ={}",driver.getDnum());
                }
            }
            else {
                throw new ManagerException(ResultEnums.NO_SUCH_DRIVER.getMsg(),"/manager/goContactAndHelp");
            }
        }else{
            List<Driver> driverList = driverRepository.findByLineId(lineId);
            for (Driver driver : driverList){
                messagesService.createMessage(driver.getDnum(),message2DriverForm.getMessage());
            }
            PushDTO driverPushDTO = new PushDTO(PushModuleConstant.TITLE, message2DriverForm.getMessage(), 2, PushModuleConstant.platform, lineId.toString(), "");
            try {
                pushServiceWithImpl.pushMessage2Driver(driverPushDTO);
            }catch (Exception e){
                //反正发出去了，异常捕获不抛出
                log.info("向线路{}所有司机推送消息，把异常截取以免爆了",lineId);
            }

//            List<Driver> driverList = driverRepository.findByLineId(lineId);
//            for (Driver driver : driverList){
//                try {
//                    webSocketService.sendInfo(message2DriverForm.getMessage(),driver.getDnum());
//                }catch (IOException e){
//                    log.warn("向司机发送及时消息失败, dnum ={}",driver.getDnum());
//                    //不过问题不大，因为存库了
//                }
//            }
        }

    }
}
