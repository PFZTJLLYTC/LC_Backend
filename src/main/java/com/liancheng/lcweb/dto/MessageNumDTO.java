package com.liancheng.lcweb.dto;

import lombok.Data;

@Data
public class MessageNumDTO {

    private Integer driverMessages = 0;

    private Integer orderMessages = 0;

    private Integer allMessages = 0;

    public MessageNumDTO(Integer driverMessages, Integer orderMessages) {
        this.driverMessages = driverMessages;
        this.orderMessages = orderMessages;
        this.allMessages = driverMessages+orderMessages;
    }
}
