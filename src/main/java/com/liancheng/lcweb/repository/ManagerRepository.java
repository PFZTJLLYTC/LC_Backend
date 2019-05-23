package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Manager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManagerRepository extends JpaRepository<Manager,String> {

    List<Manager> findByLineId(Integer lineId);

    Page<Manager> findByLineId(Integer linId, Pageable pageable);

    Manager findByTelNumAndPassword(String telNum, String password);
}
