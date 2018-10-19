package com.github.kjtang.activiti.core.dto.task;

import com.github.kjtang.activiti.core.dto.common.BasePageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by kjtang on 2018/10/16.
 */
@Data
@ApiModel("代办任务查询DTO")
public class GetToDoTaskPagedListDTO extends BasePageDTO {

    @ApiModelProperty("候选人或任务处理人")
    private String assignee;

}