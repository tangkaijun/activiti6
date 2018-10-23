package com.github.kjtang.activiti.enums;

/**
 * Created by kaijun on 2018/10/23.
 */
public enum ProcessStateEnum {

    PROCESS_FINISHED("流程已经结束",1),
    PROCESS_UNFINISHED("流程未结束",0);

    private String name;

    private Integer code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    private ProcessStateEnum(String name, Integer code){
        this.name = name;
        this.code = code;
    }


}
