package com.liancheng.lcweb.controller;

import com.liancheng.lcweb.VO.ResultVO;
import com.liancheng.lcweb.constant.RedisConstant;
import com.liancheng.lcweb.domain.Manager;
import com.liancheng.lcweb.dto.ManagerDTO;
import com.liancheng.lcweb.enums.ResultEnums;
import com.liancheng.lcweb.exception.LcException;
import com.liancheng.lcweb.form.UserLoginForm;
import com.liancheng.lcweb.service.impl.MobileManService;
import com.liancheng.lcweb.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/mobileManager")
public class MobileManController {

    @Autowired
    private MobileManService managerService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    // 做统一验证
    public void getLineId(String token,Integer lineId){
        String tokenValue = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,token))+"";
        if (StringUtils.isEmpty(tokenValue)){
            log.warn("redis中无此管理员信息，无法配合其操作");
            throw new LcException(ResultEnums.USER_TOKEN_EXPIRE);
        }
        Manager manager = managerService.findOne(tokenValue);
        if (!manager.getLineId().equals(lineId)){
            // 不匹配直接说无此线路就行
            throw new LcException(ResultEnums.NO_SUCH_LINENAME);
        }
    }

    @PostMapping("/login")
    public ResultVO mobileManagerLogin(@Valid @RequestBody UserLoginForm form, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("用户(管理员)登入信息不正确");
            return ResultVOUtil.error(
                    ResultEnums.USER_LOGIN_FORM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        ManagerDTO manager =  managerService.login(form);

        // 存管理员的tel信息
        String telNum = manager.getTelNum();

        // 设置token到redis
        String token = UUID.randomUUID().toString();
        Integer expire = RedisConstant.EXPIRE;// 每天要求重新登陆
        //加个前缀显得高端
        //使用telNum可以一线路多管理员支持同时在线
        redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX,token),telNum,expire, TimeUnit.SECONDS);

        manager.setToken(token);
        // 返回一个DTO，前段
        return ResultVOUtil.success(token);
    }



    @PostMapping("/driver/allDrivers")
    public ResultVO allDrivers(){

    }



}
