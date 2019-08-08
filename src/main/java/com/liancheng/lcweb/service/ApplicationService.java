package com.liancheng.lcweb.service;

import com.liancheng.lcweb.domain.Application;
import com.liancheng.lcweb.form.ApplicationForm;

public interface ApplicationService {

    Application applyFor (ApplicationForm applicationForm);

    void passApply(Integer id);

    void rejectApply(Integer id);

    Application findOne(Integer apply_id);

}
