package com.liancheng.lcweb.service.impl;


import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.service.ManagerService;
import com.liancheng.lcweb.service.RootService;
import com.liancheng.lcweb.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RootServiceImpl implements RootService {

    @Autowired
    private ManagerService managerService;




}
