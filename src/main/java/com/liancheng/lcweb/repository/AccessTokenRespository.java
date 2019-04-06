package com.liancheng.lcweb.repository;

import com.liancheng.lcweb.domain.AccessToken;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccessTokenRespository extends JpaRepository<AccessToken,String> {

    List<AccessToken> findAll();

    Optional<AccessToken> findById(String Id);

    AccessToken findByUserId(String userId);

}
