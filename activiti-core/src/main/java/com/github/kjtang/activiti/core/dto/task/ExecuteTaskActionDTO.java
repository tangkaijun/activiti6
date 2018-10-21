package com.github.kjtang.activiti.core.dto.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * Created by kaijun on 2018/10/20.
 */
@Data
public class ExecuteTaskActionDTO {

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("处理人")
    private String assignee;

    @ApiModelProperty("执行动作")
    private String executeAction;

    @ApiModelProperty("批注信息")
    private String comment;

    @ApiModelProperty("任务局部变量")
    private Map<String,Object> taskVariable;

    @ApiModelProperty("流程全局变量")
    private Map<String,Object> processVariable;

}