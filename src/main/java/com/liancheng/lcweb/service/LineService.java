package com.liancheng.lcweb.service;

import com.liancheng.lcweb.domain.Line;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.repository.LineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

        return line.orElse(null);
    }

    public Integer findLineIdByLineName(String lineName) {

        //注册线路两个方向都查一遍
        Line line1 = lineRepository.findByLineName1(lineName);
        if(line1!=null)return line1.getLineId();

        Line line2 = lineRepository.findByLineName2(lineName);
        if(line2!=null)return line2.getLineId();

        throw new LcException(ResultEnums.NO_SUCH_LINENAME);
    }

    public List<String> findAllLineName1AndLineName2(){
        return lineRepository.findAllLineName1AndLineName2();
    }

    public List<String> findAllLineName1(){
        return lineRepository.findAllLineName1();
    }
}
