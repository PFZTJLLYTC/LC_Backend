package com.liancheng.lcweb.VO;


import lombok.Data;

import java.io.Serializable;

@Data
public class ResultVO<T> implements Serializable {


    private static final long serialVersionUID = -9078190975308856406L;

    private Integer code;

    private String msg;

    private T data;

}
