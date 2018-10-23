package com.github.kjtang.activiti.core.vo.history;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by kaijun on 2018/10/23.
 */
@Data
@ApiModel
public class HistoricProcessInstanceVO implements Serializable{

    @ApiModelProperty("")
    private String id;

    private String businessKey;

    private String processDefinitionId;

    private String  processDefinitionName;

    private String processDefinitionKey;

    private Integer processDefinitionVersion;

    private String deploymentId;

    private Date startTime;

    private Date endTime;

    private Long durationInMillis;

    private String endActivityId;

    private String startUserId;

    private String startActivityId;

    private String getDeleteReason;

    private String superProcessInstanceId;

    private String tenantId;

    private String name;

    private String description;

    private Map<String, Object> processVariables;

}
