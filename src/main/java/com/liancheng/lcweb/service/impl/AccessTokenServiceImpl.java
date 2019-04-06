package com.liancheng.lcweb.service.impl;

import com.liancheng.lcweb.domain.AccessToken;
import com.liancheng.lcweb.repository.AccessTokenRespository;
import com.liancheng.lcweb.service.AccessTokenService;
import com.liancheng.lcweb.utils.KeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.List;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    @Autowired
    private AccessTokenRespository accessTokenRespository;

    @Override
    public List<AccessToken> findAll(){return accessTokenRespository.findAll();}

    @Override
    public AccessToken findOne(String id){return accessTokenRespository.findById(id).get();}

    @Override
    public AccessToken findByUserId(String userId){
        return accessTokenRespository.findByUserId(userId);
    }

    @Override
    public AccessToken createAccessToken(String userId){
        AccessToken accessToken=new AccessToken();
        accessToken.setId(KeyUtil.getSHA256StrJava(userId));
        accessToken.setUserId(userId);
        accessToken.setTtl(1209600);
        return accessTokenRespository.save(accessToken);
    }
}
