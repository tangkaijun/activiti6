package com.github.kjtang.activiti.response;

import lombok.Data;

/**
 * Created by kjtang on 2018/10/19.
 */
@Data
public class ResponseVO<T> {

    private Integer code;

    private String msg;

    private T data;

}