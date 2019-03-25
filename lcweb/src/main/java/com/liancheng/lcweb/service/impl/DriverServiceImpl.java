package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.repository.DriverRepository;
import com.liancheng.lcweb.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverRepository driverRepository;
}
