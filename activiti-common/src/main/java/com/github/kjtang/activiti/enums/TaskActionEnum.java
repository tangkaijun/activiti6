package com.github.kjtang.activiti.enums;

/**
 * Created by kaijun on 2018/10/20.
 */
public enum TaskActionEnum {

    ACTION_COMPLETE("complete", "完成任务"),
    ACTION_CLAIM("claim", "签收任务"),
    ACTION_DELEGATE("delegate", "委托任务"),
    ACTION_RESOLVE("resolve", "指派任务");

    private String type;
    private String name;

    private TaskActionEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static TaskActionEnum valueByName(String name){
        if("complete".equals(name)){
            return ACTION_COMPLETE;
        }

        if("claim".equals(name)){
            return ACTION_CLAIM;
        }
        return null;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
