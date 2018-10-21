package com.github.kjtang.activiti.core.vo.task;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by kjtang on 2018/10/16.
 */
@Data
public class TaskVO implements Serializable{

    /**
     * 任务id
     */
    private String id;

    /**
     * 任务名称
     */
    private String name;


    /**
     * Free text description of the task.
     */
    private String description;

    /**
     * Indication of how important/urgent this task is
     */
    private Integer priority;

    /**
     * The {@link User.getId() userId} of the person that is responsible for this task.
     */
    private String owner;

    /**
     * The {@link User.getId() userId} of the person to which this task is delegated.
     */
    private String assignee;

    /**
     * Reference to the process instance or null if it is not related to a process instance.
     */
    private String processInstanceId;

    /**
     * Reference to the path of execution or null if it is not related to a process instance.
     */
    private String executionId;

    /**
     * Reference to the process definition or null if it is not related to a process.
     */
    private String processDefinitionId;

    /** The date/time when this task was created */
    private Date createTime;

    /**
     * The id of the activity in the process defining this task or null if this is not related to a process
     */
    private String taskDefinitionKey;

    /**
     * Due date of the task.
     */
    private Date dueDate;

    /**
     * The category of the task. This is an optional field and allows to 'tag' tasks as belonging to a certain category.
     */
    private String category;

    /**
     * The parent task for which this task is a subtask
     */
    private String parentTaskId;

    /**
     * The tenant identifier of this task
     */
    private String tenantId;

    /**
     * The form key for the user task
     */
    private String formKey;

    /**
     * Returns the local task variables if requested in the task query
     */
    Map<String, Object> taskLocalVariables;

    /**
     * Returns the process variables if requested in the task query
     */
    private Map<String, Object> processVariables;

    /**
     * The claim time of this task
     */
    private Date claimTime;
}
