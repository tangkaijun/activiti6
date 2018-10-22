package com.github.kjtang.activiti.core.vo.process;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * Created by kaijun on 2018/10/20.
 */
@Data
public class ProcessInstanceVO {

    @ApiModelProperty("Id")
    private String id;

    @ApiModelProperty("父id")
    private String parentId;

    @ApiModelProperty("流程实例名称")
    private String name;

    @ApiModelProperty("流程实例描述")
    private String description;

    @ApiModelProperty("业务key,可以关联业务数据id")
    private String businessKey;

    @ApiModelProperty("formkey关联表单key")
    private String formKey;

    @ApiModelProperty("活动节点的id")
    private String activityId;

    @ApiModelProperty("流程部署id")
    private String deploymentId;

    @ApiModelProperty("父执行id")
    private String superExecutionId;

    @ApiModelProperty("流程实力id")
    private String processInstanceId;

    @ApiModelProperty("根流程实例Id")
    private String rootProcessInstanceId;

    @ApiModelProperty("流程定义id")
    private String processDefinitionId;

    @ApiModelProperty("流程定义名")
    private String processDefinitionName;

    @ApiModelProperty("流程定义key，对应流程定义id")
    private String processDefinitionKey;

    @ApiModelProperty("流程定义版本")
    private String processDefinitionVersion;

    @ApiModelProperty("流程变量")
    private Map<String,Object> processVariables;

    @ApiModelProperty("本地化名称")
    private String localizedName;

    @ApiModelProperty("本地化描述")
    private String localizedDescription;

    @ApiModelProperty("启动用户id")
    private String startUserId;

    @ApiModelProperty("启动时间")
    private Date startTime;

}
