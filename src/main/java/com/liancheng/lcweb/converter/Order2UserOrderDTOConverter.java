package com.liancheng.lcweb.converter;

import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.UserOrderDTO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class Order2UserOrderDTOConverter {

    public static UserOrderDTO convert(Order order){

        UserOrderDTO userOrderDTO=new UserOrderDTO();

        BeanUtils.copyProperties(order,userOrderDTO);

        String[] lineNameArray=order.getLineName().split("-",2);
        userOrderDTO.setSource(lineNameArray[0]);
        userOrderDTO.setDestination(lineNameArray[1]);
        return userOrderDTO;
    }

    public static List<UserOrderDTO> convert(List<Order> orderList){

        return orderList.stream().map(e -> convert(e)).collect(Collectors.toList());
    }
}
