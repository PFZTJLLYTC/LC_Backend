package com.liancheng.lcweb.config;

import com.liancheng.lcweb.domain.Line;
import com.liancheng.lcweb.domain.LineTotal;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.repository.LineRepository;
import com.liancheng.lcweb.repository.LineTotalRepository;
import com.liancheng.lcweb.repository.OrderRepository;
import com.liancheng.lcweb.service.LineService;
import com.liancheng.lcweb.service.ManagerService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Component
public class ScheduleTask {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private LineTotalRepository lineTotalRepository;

    @Autowired
    private LineRepository lineService;


    //test
//    @Scheduled(cron = "0/1 * * * * ? ")
//    public void testTask(){
//        System.out.println(lineService.findAll());
//    }


    //每天凌晨一点操作
    @Scheduled(cron = "0 0 1 1/1 * ? ")
    public void dailyTask(){
        List<Line> lines = lineService.findAll();
        LocalDate now = LocalDate.now();
        String ldate = now.minusDays(1).toString();
        String lldate = now.minusDays(2).toString();
        for (Line line : lines){
            LineTotal lineTotal = new LineTotal();
            lineTotal.setLineId(line.getLineId());
            lineTotal.setDate(ldate);
            //日任务
            lineTotal.setType(0);
            //找到今日线路总的订单list
            List<Order> orders = orderRepository.findByLineIdAndDate(line.getLineId(),ldate);
            Integer orderCount = orders.size();
            lineTotal.setOrderCount(orderCount);
            Integer userCount = 0;
            for (Order o : orders){
                userCount+=o.getUserCount();
            }
            lineTotal.setUserCount(userCount);
            LineTotal lineTotal1 = lineTotalRepository.
                    findByLineIdAndDateAndType(
                            line.getLineId()
                            ,lldate
                            ,0
                    );
            //根据userCount来比较
            if (lineTotal1!=null){
                lineTotal.setCompare((lineTotal.getUserCount()-lineTotal1.getUserCount())/lineTotal1.getUserCount()*100+"%");
            }
            else{
                lineTotal.setCompare(0+"");
            }

            lineTotalRepository.save(lineTotal);

        }
    }

    //每月一号操作


    //每年一月操作


}
