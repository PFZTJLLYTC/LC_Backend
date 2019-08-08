package com.liancheng.lcweb.service;

import com.liancheng.lcweb.domain.Application;
import com.liancheng.lcweb.domain.Line;
import com.liancheng.lcweb.domain.Manager;

public interface RootService {

    boolean confirmOneLine(Integer apply_id);

    Manager addManager2CertainLine(Line line, Application application);
}
