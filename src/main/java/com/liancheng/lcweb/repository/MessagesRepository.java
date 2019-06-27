package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Messages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessagesRepository extends JpaRepository<Messages,Integer> {

    List<Messages> findAll();

    List<Messages> findByTargetOrderByCreateTimeDesc(String target);

    void deleteByTarget(String target);
}
