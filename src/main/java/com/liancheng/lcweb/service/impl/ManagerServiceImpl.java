package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.converter.Driver2DriverDTOConverter;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.DriverDTO;
import com.liancheng.lcweb.dto.TotalInfoDTO;
import com.liancheng.lcweb.enums.DriverStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.exception.ManagerException;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.repository.OrderRepository;
import com.liancheng.lcweb.service.DriverService;
import com.liancheng.lcweb.service.ManagerService;
import com.liancheng.lcweb.utils.ResultVOUtil;
import javassist.expr.NewArray;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.util.LangUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    private OrderRepository orderRepository;

    @Override
    public List<Manager> findAll() {
        return managerRepository.findAll();
    }

    @Override
    public Manager findOne(Integer id) {
        Optional<Manager> manager = managerRepository.findById(id);
        if (!manager.isPresent()){
            return null;
        }
        return manager.get();
    }

    /*登陆用*/
    @Override
    public Manager getManager(Integer lineId, String password) {
        if (managerRepository.findByLineIdAndPassword(lineId,password)==null){
            log.error("没有此线路负责人");
            return null;
        }
        return managerRepository.findByLineIdAndPassword(lineId,password);
    }


    @Override
    public Manager addManager(Manager manager) {
        Manager result = new Manager();
        BeanUtils.copyProperties(manager,result);
        return managerRepository.save(result);
    }

    @Override
    public ResultVO deleteOne(Integer lineId) {
        if (findOne(lineId)!=null){
            log.error("删除管理员失败,无此管理员");
            throw new LcException(ResultEnums.NO_SUCH_MANAGER);
        }
        //所属司机全部删除，但是原有订单不删除。
        for (Driver driver : driverService.findbyLineId(lineId)){
            driverService.deleteOne(driver.getDnum());
        }
        managerRepository.deleteById(lineId);
        return ResultVOUtil.success();
    }

    @Override
    public List<DriverDTO> getDriversByStatus(Integer lineId, Integer status) {

        if (status<-1||status>2){
            log.error("根本没有这个状态");
            throw new ManagerException(ResultEnums.DRIVER_STATUS_ERROR.getMsg(),"manager/drivers");
        }
        List<Driver>  driverList= new ArrayList<>();
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
    public List<DriverDTO> getAllDrivers(Integer lineId) {
        if (findOne(lineId)==null){
            return null;
        }
        List<Driver> driverList = driverService.findbyLineId(lineId);

        List<DriverDTO> driverDTOList = Driver2DriverDTOConverter.convert(driverList);

        return driverDTOList;

    }

    @Override
    public List<Order> getAllOrders(Integer lineId) {
        List<Order> orders = orderRepository.findByLineId(lineId);
        if (CollectionUtils.isEmpty(orders))return null;
        return orders;

    }
}
