package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line,Integer> {

    Line findByLineName1(String lineName);

    Line findByLineName2(String lineName);
}
