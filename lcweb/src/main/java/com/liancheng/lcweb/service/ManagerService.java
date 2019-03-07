package com.liancheng.lcweb.service;

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

}
