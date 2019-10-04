package com.liancheng.lcweb.config;

import com.liancheng.lcweb.domain.Driver;
import com.liancheng.lcweb.domain.Line;
import com.liancheng.lcweb.domain.LineTotal;
import com.liancheng.lcweb.domain.Order;
import com.liancheng.lcweb.repository.LineRepository;
import com.liancheng.lcweb.repository.LineTotalRepository;
import com.liancheng.lcweb.repository.OrderRepository;
import com.liancheng.lcweb.service.DriverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class ScheduleTask {

    @Autowired
    private DriverService driverService;

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


    //每天凌晨一点操作 type = 0
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
                if (lineTotal1.getUserCount()==0){
                    lineTotal.setCompare("0%");//第一天或者是前一天无乘客，算作第一天，不展示增减
                }else{
                    lineTotal.setCompare((lineTotal.getUserCount()-lineTotal1.getUserCount())/(float)lineTotal1.getUserCount()*100+"%");

                }
            }
            else{
                lineTotal.setCompare("0%");
            }

            lineTotalRepository.save(lineTotal);
            log.info("线路"+line.getLineName1()+"日详情打印完毕。");
        }
    }

    //每月一号两点操作 type =1
    @Scheduled(cron = "0 0 2 1 1/1 ? ")
    public void monthlyTask(){
        List<Line> lines = lineService.findAll();
        LocalDate now = LocalDate.now();
        LocalDate lmonth = now.minusMonths(1);
        for (Line line : lines){
            LineTotal lineTotal = new LineTotal();
            lineTotal.setLineId(line.getLineId());
            lineTotal.setDate(lmonth.toString());

            lineTotal.setType(1);
            //截取年份加月份
            String month = lmonth.toString().substring(0,7);
            List<Order> orders = orderRepository.findByLineIdAndMonthOrYearDate(line.getLineId(),month);
            lineTotal.setOrderCount(orders.size());

            Integer userCount =0;
            for (Order o : orders){
                userCount+=o.getUserCount();
            }
            lineTotal.setUserCount(userCount);

            LineTotal lineTotal1 = lineTotalRepository.
                    findByLineIdAndDateAndType(
                            line.getLineId()
                            ,lmonth.minusMonths(1).toString()
                            ,1
                    );
            //根据userCount来比较
            if (lineTotal1!=null){
                if (lineTotal1.getUserCount()==0){
                    lineTotal.setCompare("0%");
                }
                else {
                    lineTotal.setCompare((lineTotal.getUserCount()-lineTotal1.getUserCount())/(float)lineTotal1.getUserCount()*100+"%");
                }
            }
            else{
                lineTotal.setCompare("0%");
            }

            lineTotalRepository.save(lineTotal);
            log.info("线路"+line.getLineName1()+"月详情打印完毕。");
        }
        for (Driver driver : driverService.findAll()){
            // 变成司机的月完成单数
            // 考核？1.抓紧时间 2.另外方法
            driver.setWorkTimes(0);
            driverService.saveOne(driver);
        }
        log.info("司机上月订单数重置。");

    }

    //每年一月一号三点操作操作
//    @Scheduled(cron = "0 0 3 1 1 ? *") spring不支持年定时
    @Scheduled(cron = "0 0 3 1 1 ? ")
    public void yearlyTask(){
        List<Line> lines = lineService.findAll();
        LocalDate now = LocalDate.now();
        LocalDate lyear = now.minusYears(1);

        for (Line line : lines){
            LineTotal lineTotal = new LineTotal();
            lineTotal.setLineId(line.getLineId());

            lineTotal.setDate(lyear.toString());

            lineTotal.setType(2);
            //只截取年份,可行2019-01-01
            String year = lyear.toString().substring(0,4);
            //利用LineTotal来找效率更高才对！
            //找这一年的每个月的数据
            List<LineTotal> lineTotals = lineTotalRepository.
                    findByLineIdAndTypeAndYeardate(
                            line.getLineId(),
                            1,
                            year
                    );
            Integer orderCount,userCount;
            orderCount = userCount = 0;
            if (!CollectionUtils.isEmpty(lineTotals)){
                for (LineTotal l : lineTotals){
                    orderCount+=l.getOrderCount();
                    userCount+=l.getUserCount();
                }
            }
            lineTotal.setUserCount(userCount);
            lineTotal.setOrderCount(orderCount);

            LineTotal lineTotal1 = lineTotalRepository.
                    findByLineIdAndDateAndType(
                            line.getLineId()
                            ,lyear.minusYears(1).toString()
                            ,2
                    );
            //根据userCount来比较
            if (lineTotal1!=null){
                if (lineTotal1.getUserCount()==0){
                    lineTotal.setCompare("0%");
                }
                else {
                    lineTotal.setCompare((lineTotal.getUserCount()-lineTotal1.getUserCount())/(float)lineTotal1.getUserCount()*100+"%");
                }
            }
            else{
                lineTotal.setCompare("0%");
            }

            lineTotalRepository.save(lineTotal);
            log.info("线路"+line.getLineName1()+"年详情打印完毕。");
        }

    }


}
