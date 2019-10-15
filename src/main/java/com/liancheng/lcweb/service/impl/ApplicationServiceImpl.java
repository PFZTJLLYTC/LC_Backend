package com.liancheng.lcweb.service.impl;


import com.liancheng.lcweb.domain.Application;
import com.liancheng.lcweb.form.ApplicationForm;
import com.liancheng.lcweb.repository.ApplicationRepository;
import com.liancheng.lcweb.service.ApplicationService;
import com.liancheng.lcweb.service.RootService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Application applyFor(ApplicationForm application) {
        Application new_one = new Application();
        BeanUtils.copyProperties(application,new_one);
        new_one.setIsDeal(0);
        new_one.setPwd(passwordEncoder.encode(application.getPwd()));
        return applicationRepository.save(new_one);
    }

    @Override
    public void passApply(Integer id) {
        Application find = findOne(id);
        if (find!=null){
            log.info("设置线路{}已经通过",id);
            //联系与设置pwd另写
            find.setIsDeal(1);
            applicationRepository.save(find);
        }
    }

    @Override
    public void rejectApply(Integer id) {
        Application find = findOne(id);
        if (find!=null){
            log.info("设置线路{}未通过",id);
            find.setIsDeal(2);
            applicationRepository.save(find);
        }
    }

    @Override
    public Application findOne(Integer apply_id) {
        Optional<Application> find = applicationRepository.findById(apply_id);

        return  find.orElse(null);
    }
}
