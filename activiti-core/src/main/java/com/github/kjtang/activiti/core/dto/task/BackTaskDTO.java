package com.github.kjtang.activiti.core.dto.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by kaijun on 2018/10/20.
 */
@Data
public class BackTaskDTO implements Serializable{

    @ApiModelProperty("任务处理人")
    private String assignee;

    @ApiModelProperty("当前任务id")
    private String currentTaskId;

    @ApiModelProperty("目标任务id")
    private String targetTaskId;

    @ApiModelProperty("回退类型:1表示回退到发起节点，2表示回退到上一个节点，3表示回退到任意已经审批的环节")
    private Integer backType;

    @ApiModelProperty("任务局部变量")
    private Map<String,Object> taskVariable;

    @ApiModelProperty("流程全局变量")
    private Map<String,Object> processVariable;

}
