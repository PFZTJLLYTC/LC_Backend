package com.liancheng.lcweb.service.impl;


import com.liancheng.lcweb.domain.Application;
import com.liancheng.lcweb.form.ApplicationForm;
import com.liancheng.lcweb.repository.ApplicationRepository;
import com.liancheng.lcweb.service.ApplicationService;
import com.liancheng.lcweb.service.RootService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public Application applyFor(ApplicationForm application) {
        Application new_one = new Application();
        BeanUtils.copyProperties(application,new_one);
        new_one.setIsDeal(0);
        return applicationRepository.save(new_one);
    }

    @Override
    public void passApply(Integer id) {
        Optional<Application> find = applicationRepository.findById(id);
        if (find.isPresent()){
            Application application = find.get();
            log.info("设置线路{}已经通过",id);
            //联系与设置pwd另写
            application.setIsDeal(1);
            applicationRepository.save(application);
        }
    }

    @Override
    public void rejectApply(Integer id) {
        Optional<Application> find = applicationRepository.findById(id);
        if (find.isPresent()){
            Application application = find.get();
            log.info("设置线路{}未通过",id);
            application.setIsDeal(2);
            applicationRepository.save(application);
        }
    }
}
