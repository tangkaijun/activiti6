package com.github.kjtang.activiti.core.dto.process;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.annotation.security.DenyAll;
import java.util.Map;

/**
 * Created by kaijun on 2018/10/20.
 */
@Data
public class StartProcessDTO {

    @ApiModelProperty("流程定义名称")
    private String processName;

    @ApiModelProperty("流程定义标识")
    private String processDefinitionKey;

    @ApiModelProperty("业务标识")
    private String businessKey;

    @ApiModelProperty("流程启动者Id")
    private String initiatorId;

    @ApiModelProperty("流程启动变量")
    private Map<String,Object> processVariables;

}