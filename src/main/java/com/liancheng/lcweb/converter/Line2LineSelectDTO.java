package com.liancheng.lcweb.converter;

import com.liancheng.lcweb.domain.Line;
import com.liancheng.lcweb.dto.LineSelectDTO;

import java.util.List;
import java.util.stream.Collectors;

public class Line2LineSelectDTO {

    public static LineSelectDTO convert(Line line){
        LineSelectDTO lineSelectDTO=new LineSelectDTO();

        lineSelectDTO.setLineId(line.getLineId());
        lineSelectDTO.setName(line.getLineName1());
        lineSelectDTO.setCustomerTypes(line.getPrice());

        return lineSelectDTO;
    }

    public static List<LineSelectDTO> convert(List<Line> lineList){
        return lineList.stream().map(e->convert(e)).collect(Collectors.toList());
    }
}
