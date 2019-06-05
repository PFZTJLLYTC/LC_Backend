package com.liancheng.lcweb.service.impl;
import com.liancheng.lcweb.converter.Driver2DriverDTOConverter;
import com.liancheng.lcweb.converter.String2DateConverter;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.DriverDTO;
import com.liancheng.lcweb.dto.TotalInfoDTO;
import com.liancheng.lcweb.enums.DriverStatusEnums;
import com.liancheng.lcweb.enums.OrderStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.exception.ManagerException;
import com.liancheng.lcweb.form.DriverInfoForm;
import com.liancheng.lcweb.form.addDriverFormForManager;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.repository.OrderRepository;
import com.liancheng.lcweb.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private LineService lineService;

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

        try {
            driver.setBirthday(String2DateConverter.convert(driverInfoForm.getBirthday()));
        } catch (ParseException e) {
            //暂时不抛回去，保证用户友好
            log.error("日期格式转换错误,lineId={}",lineId);
            log.error(e.getMessage());
        }
        driver.setLineId(lineId);
        //刚刚注册都置为休息状态
        driver.setStatus(DriverStatusEnums.ATREST.getCode());
        driver.setWorkTimes(0);
        driverRepository.save(driver);
    }



    @Override
    public Page<DriverDTO> getDriversByStatus(Integer lineId, Integer status,Pageable pageable) {

        if (status<-1||status>2){
            log.error("根本没有这个状态");
            throw new ManagerException(ResultEnums.DRIVER_STATUS_ERROR.getMsg(),"/manager/drivers");
        }
        Page<Driver>  driverPage;
        if (status.equals(DriverStatusEnums.ATREST.getCode())){
            driverPage = driverService.certainLIneAtrest(lineId,pageable);
        }
        else if (status.equals(DriverStatusEnums.AVAILABLE.getCode())){
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

        if (status<-1||status>2){
            log.error("根本没有这个状态");
            throw new ManagerException(ResultEnums.DRIVER_STATUS_ERROR.getMsg(),"/manager/drivers");
        }
        List<Driver>  driverList ;
        if (status.equals(DriverStatusEnums.ATREST.getCode())){
            driverList = driverService.certainLIneAtrest(lineId);
        }
        else if (status.equals(DriverStatusEnums.AVAILABLE.getCode())){
            driverList = driverService.certainLIneAvailable(lineId);
        }
        else if (status.equals(DriverStatusEnums.ONROAD.getCode())){
            driverList = driverService.certainLIneOnroad(lineId);
        }
        else {
            driverList = driverService.certainLIneToVerify(lineId);
        }
        List<DriverDTO> driverDTOList =Driver2DriverDTOConverter.convert(driverList);

        return driverDTOList;
    }


    @Override
    public TotalInfoDTO getTotal(Integer lineId) {
        TotalInfoDTO totalInfoDTO = new TotalInfoDTO();

        //得时间
        Date currentDate =new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentDate);
        totalInfoDTO.setDate(dateString);

        //找当前活跃司机总数
        List<DriverDTO> driverList1 = getDriversByStatus(lineId,DriverStatusEnums.ONROAD.getCode());
        List<DriverDTO> driverList2 = getDriversByStatus(lineId,DriverStatusEnums.AVAILABLE.getCode());
        Integer liveNum = driverList1.size()+driverList2.size();
        totalInfoDTO.setLiveDrivers(liveNum);


        //目前直接假数据填充
        //todo 找当前总载客人数,通过订单时期入手
        totalInfoDTO.setTotalUserNum(802);

        //todo 找当天当前订单总数，考虑是否计算get
        totalInfoDTO.setOrderNum(105);

        totalInfoDTO.setTotalGet(2456);


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
    public List<Order> getAllOrders(Integer lineId) {
        List<Order> orders = orderRepository.findByLineId(lineId);
        if (CollectionUtils.isEmpty(orders))return null;
        return orders;

    }

    //实现分页的全部线路订单
    @Override
    public Page<Order> getAllOrders(Integer lineId, Pageable pageable) {
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

        //验证司机是否符合条件
        if (driver.getStatus().equals(DriverStatusEnums.AVAILABLE.getCode())&&
            driver.getAvailableSeats()>=order.getUserCount()){
            Order result =  orderService.confirmOne(order,driver);
        }
        else if(!driver.getStatus().equals(DriverStatusEnums.AVAILABLE.getCode())){
            log.error("分配订单时司机状态错误");
            throw new ManagerException(ResultEnums.DRIVER_STATUS_ERROR.getMsg(),"/manager/order/findByStatus?status="+ OrderStatusEnums.WAIT.getCode());
        }
        else {
            log.error("分配时司机可用座位数目不足");
            throw new ManagerException(ResultEnums.SEATS_NOT_ENOUGH.getMsg(),"/manager/order/findByStatus?status="+ OrderStatusEnums.WAIT.getCode());
        }

        try {
            webSocketService.sendInfo("订单状态改变",dnum);

        } catch (IOException e) {
            log.warn("向司机发送即时消息失败,dnum={},message={}",dnum,e.getMessage());
        }
        try {
            webSocketService.sendInfo("订单状态改变",order.getUserId());
        } catch (IOException e) {
            log.warn("向乘客发送即时消息失败,userId={},message={}",order.getUserId(),e.getMessage());
        }


    }

    //保留，以后说不定有用
    @Override
    public List<Order> getOrdersByStatus(Integer lineId, Integer status) {

        List<Order> allOrders = getAllOrders(lineId);
        if (allOrders==null)return null;
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
    }
}
