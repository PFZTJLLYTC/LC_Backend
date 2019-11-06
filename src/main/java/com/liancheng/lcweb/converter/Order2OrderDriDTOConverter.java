package com.liancheng.lcweb.converter;

import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.dto.OrderDriDTO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class Order2OrderDriDTOConverter {

    public static OrderDriDTO convert(Order order){
        OrderDriDTO dto = new OrderDriDTO();

        BeanUtils.copyProperties(order,dto);

        String[] lineNameArray=order.getLineName().split("-",2);
        dto.setSource(lineNameArray[0]);
        dto.setDestination(lineNameArray[1]);

        return dto;
    }

    public static List<OrderDriDTO> convert(List<Order> orderList){

        return orderList.stream().map(e -> convert(e)).collect(Collectors.toList());
    }
}
