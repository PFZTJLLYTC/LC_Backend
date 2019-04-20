package com.liancheng.lcweb.service;


import com.liancheng.lcweb.domain.AccessToken;

import java.util.List;

public interface AccessTokenService {

    List<AccessToken> findAll();

    AccessToken findOne(String id);

    AccessToken findByUserId(String userId);

    AccessToken createAccessToken(String userId);

}
