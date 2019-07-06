package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LineRepository extends JpaRepository<Line,Integer> {

    Line findByLineName1(String lineName);

    Line findByLineName2(String lineName);

    @Query(value = "SELECT line_name1 FROM line " +
            "UNION " +
            "SELECT line_name2 FROM line",nativeQuery = true)
    List<String> findAllLineName1AndLineName2();

    @Query(value = "SELECT line_name1 FROM line ",nativeQuery = true)
    List<String> findAllLineName1();
}
