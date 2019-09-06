package com.liancheng.lcweb.service;

import com.liancheng.lcweb.domain.Messages;


import java.util.List;

public interface MessagesService {


    List<Messages> findAll();

    List<Messages> findByTarget(String target);

    void createMessage(String target,String message,Integer type);

    void deleteMessage(Integer id);

    //删除所有时采用
    void deleteMessageByTarget(String target);


}
