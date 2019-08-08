package com.liancheng.lcweb.service.impl;
import com.liancheng.lcweb.domain.Application;
import com.liancheng.lcweb.domain.Line;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.repository.LineRepository;
import com.liancheng.lcweb.repository.ManagerRepository;
import com.liancheng.lcweb.service.ApplicationService;
import com.liancheng.lcweb.service.RootService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class RootServiceImpl implements RootService {

    @Autowired
    private ManagerRepository managerService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private LineRepository lineRepository;

    //注意调用顺序
    @Override
    public boolean confirmOneLine(Integer apply_id) {
        Application  source = applicationService.findOne(apply_id);
        if (source!=null){
            Line objectLine = new Line();
            //分两份
            String[] adds  = StringUtils.split(source.getLineName(),"-");
            StringBuilder sb = new StringBuilder();
            sb.append(adds[1]).append("-").append(adds[0]);
            String lineName2 = sb.toString();
            objectLine.setLineName1(source.getLineName());
            objectLine.setLineName2(lineName2);
            lineRepository.save(objectLine);
            Line line = lineRepository.findByLineName1(objectLine.getLineName1());
            addManager2CertainLine(line,source);
            applicationService.passApply(apply_id);
            return true;
        }
        else {
            log.error("app_id={}并没有对应的申请项",apply_id);
            return false;
        }
    }

    @Override
    public Manager addManager2CertainLine(Line line, Application application) {
        Manager manager = new Manager();
        manager.setLineId(line.getLineId());
        manager.setName(application.getMan_name());
        manager.setTelNum(application.getTel());
        manager.setPassword(application.getPwd());
        return managerService.save(manager);
    }
}
