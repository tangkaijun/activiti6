package com.github.kjtang.activiti.exception;

/**
 * Created by kjtang on 2018/10/17.
 */
public class RCodeException extends RuntimeException{

    private Integer code;

    private String msg;

    public RCodeException(){

    }

    public RCodeException(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

}
