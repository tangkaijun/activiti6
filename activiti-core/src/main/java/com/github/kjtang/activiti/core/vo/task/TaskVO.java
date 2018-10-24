package com.github.kjtang.activiti.core.vo.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by kjtang on 2018/10/16.
 */
@Data
@ApiModel("任务信息")
public class TaskVO implements Serializable{

    /**
     * 任务id
     */
    @ApiModelProperty("任务id")
    private String id;

    /**
     * 任务名称
     */
    @ApiModelProperty("任务名称")
    private String name;


    /**
     * Free text description of the task.
     */
    @ApiModelProperty("任务描述")
    private String description;

    /**
     * Indication of how important/urgent this task is
     */
    @ApiModelProperty("任务优先级")
    private Integer priority;

    /**
     * The {@link User.getId() userId} of the person that is responsible for this task.
     */
    @ApiModelProperty("任务拥有者")
    private String owner;

    /**
     * The {@link User.getId() userId} of the person to which this task is delegated.
     */
    @ApiModelProperty("任务办理人")
    private String assignee;

    /**
     * Reference to the process instance or null if it is not related to a process instance.
     */
    @ApiModelProperty("流程实例id")
    private String processInstanceId;

    /**
     * Reference to the path of execution or null if it is not related to a process instance.
     */
    @ApiModelProperty("当前执行id")
    private String executionId;

    /**
     * Reference to the process definition or null if it is not related to a process.
     */
    @ApiModelProperty("流程定义id")
    private String processDefinitionId;

    /** The date/time when this task was created */
    @ApiModelProperty("任务创建时间")
    private Date createTime;

    /**
     * The id of the activity in the process defining this task or null if this is not related to a process
     */
    @ApiModelProperty("任务定义key")
    private String taskDefinitionKey;

    /**
     * Due date of the task.
     */
    @ApiModelProperty("任务过期时间")
    private Date dueDate;

    /**
     * The category of the task. This is an optional field and allows to 'tag' tasks as belonging to a certain category.
     */
    @ApiModelProperty("任务所属分类")
    private String category;

    /**
     * The parent task for which this task is a subtask
     */
    @ApiModelProperty("父任务")
    private String parentTaskId;

    /**
     * The tenant identifier of this task
     */
    @ApiModelProperty("任务标识")
    private String tenantId;

    /**
     * The form key for the user task
     */
    @ApiModelProperty("任务表单key")
    private String formKey;

    /**
     * Returns the local task variables if requested in the task query
     */
    @ApiModelProperty("任务局部变量")
    Map<String, Object> taskLocalVariables;

    /**
     * Returns the process variables if requested in the task query
     */
    @ApiModelProperty("流程全部变量")
    private Map<String, Object> processVariables;

    /**
     * The claim time of this task
     */
    @ApiModelProperty("任务签收时间")
    private Date claimTime;

}
