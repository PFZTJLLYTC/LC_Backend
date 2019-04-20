package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.converter.Driver2DriverDTOConverter;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.dto.DriverDTO;
import com.liancheng.lcweb.enums.DriverStatusEnums;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.service.DriverService;
import com.liancheng.lcweb.service.ManagerService;
import com.liancheng.lcweb.utils.ResultVOUtil;
import javassist.expr.NewArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private DriverService driverService;

    @Override
    public List<Manager> findAll() {
        return managerRepository.findAll();
    }

    @Override
    public Manager findOne(Integer id) {
        return managerRepository.findById(id).get();
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

        if (status<0||status>2){
            log.error("根本没有这个状态");
            throw new LcException(ResultEnums.DRIVER_STATUS_ERROR);
        }
        List<Driver>  driverList= new ArrayList<>();
        if (status.equals(DriverStatusEnums.ATREST.getCode())){
            driverList = driverService.certainLIneAtrest(lineId);
        }
        else if (status.equals(DriverStatusEnums.AVAILABLE.getCode())){
            driverList = driverService.certainLIneAvailable(lineId);
        }
        else {
            driverList = driverService.certainLIneOnroad(lineId);
        }
        List<DriverDTO> driverDTOList =Driver2DriverDTOConverter.convert(driverList);

        return driverDTOList;
    }
}
