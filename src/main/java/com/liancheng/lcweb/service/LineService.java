package com.liancheng.lcweb.service;

import com.liancheng.lcweb.domain.Line;
import com.liancheng.lcweb.repository.LineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class LineService {

    @Autowired
    private LineRepository lineRepository;

    public Line findOneByName(String name){

        return lineRepository.findByLineName1(name);
    }

    public Line findOne(Integer lineId){
        Optional<Line> line = lineRepository.findById(lineId);

        if (!line.isPresent()){
            return null;
        }
        return line.get();
    }


}
