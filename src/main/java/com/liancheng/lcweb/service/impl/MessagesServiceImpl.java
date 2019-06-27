package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.domain.Messages;
import com.liancheng.lcweb.repository.MessagesRepository;
import com.liancheng.lcweb.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessagesServiceImpl implements MessagesService {

    @Autowired
    private MessagesRepository messagesRepository;

    @Override
    public List<Messages> findAll(){
        return messagesRepository.findAll();
    }

    @Override
    public List<Messages> findByTarget(String target){
        return messagesRepository.findByTargetOrderByCreateTimeDesc(target);
    }

    @Override
    public void createMessage(String target,String message){
        Messages messages=new Messages();

        messages.setTarget(target);
        messages.setMessage(message);

        messagesRepository.save(messages);
    }

    @Override
    public void deleteMessage(Integer id){
        messagesRepository.deleteById(id);
    }

    @Override
    public void deleteMessageByTarget(String target){
        messagesRepository.deleteByTarget(target);
    }
}
