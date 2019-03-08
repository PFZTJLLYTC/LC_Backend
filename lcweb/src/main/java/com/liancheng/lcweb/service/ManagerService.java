package com.liancheng.lcweb.service;

import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {
    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private DriverRepository driverRepository;

    public Manager getManager(String name, String password){
        return managerRepository.findByNameAndPassword(name,password);

    }

    public Manager insertManager(Manager manager){
        return managerRepository.save(manager);
    }

}
