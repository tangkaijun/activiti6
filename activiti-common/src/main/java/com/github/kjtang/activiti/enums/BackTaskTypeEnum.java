package com.github.kjtang.activiti.enums;

/**
 * Created by kaijun on 2018/10/20.
 * 任务回退类型枚举定义
 */
public enum BackTaskTypeEnum {

    BACK_TYPE_START_NODE("回退到第一个发起环节",1),
    BACK_TYPE_PRE_NODE("回退到上一环节",2),
    BACK_TYPE_FINISH_NODE("回退到任意已经审批的环节",3);

    private String name;

    private Integer code;

    private BackTaskTypeEnum(String name,Integer code){
        this.name = name;
        this.code = code;
    }

}
