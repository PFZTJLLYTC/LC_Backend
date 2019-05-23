package com.liancheng.lcweb.converter;

import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.dto.DriverDTO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class Driver2DriverDTOConverter {

    public static DriverDTO convert(Driver driver){

        DriverDTO driverDTO = new DriverDTO();

        BeanUtils.copyProperties(driver,driverDTO);

        return driverDTO;
    }

    public static List<DriverDTO> convert(List<Driver> driverList){

        return driverList.stream().map(e -> convert(e)).collect(Collectors.toList());
    }

}
