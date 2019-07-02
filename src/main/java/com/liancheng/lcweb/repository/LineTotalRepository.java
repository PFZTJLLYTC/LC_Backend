package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.LineTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LineTotalRepository extends JpaRepository<LineTotal,Integer> {

    List<LineTotal> findByLineIdAndType(Integer lineId, Integer type);

    LineTotal findByLineIdAndDateAndType(Integer lineId, String date, Integer type);

    @Query(value = "select * from line_total where line_id = ?1 and type = 0 and date between ?2 and ?3 order by date desc ",nativeQuery = true)
    List<LineTotal> find5Days(Integer lineId,String date1, String date2);

    //找出一年的所有月份统计数据
    @Query(value = "select * from line_total where line_id = ?1 and type = ?2 and date like concat('%',?3,'%') " , nativeQuery = true)
    List<LineTotal> findByLineIdAndTypeAndYeardate(Integer lineId, Integer type, String yearOrMonthDate);



}
