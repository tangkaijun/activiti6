package com.github.kjtang.activiti.core.dto.task;

import com.github.kjtang.activiti.core.dto.common.BasePageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by tangkj@hua-cloud.com.cn on 2018/10/24.
 */
@Data
@ApiModel("查询已办任务")
public class GetHaveDoneTaskPageListDTO extends BasePageDTO {

    @ApiModelProperty("任务办理人")
    private String assignee;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;
}
