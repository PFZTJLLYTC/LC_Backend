package com.liancheng.lcweb.exception;


import lombok.Getter;

@Getter
public class ManagerException extends RuntimeException {

    private String url;

    public ManagerException(String msg,String url){
        super(msg);
        this.url=url;
    }

}
