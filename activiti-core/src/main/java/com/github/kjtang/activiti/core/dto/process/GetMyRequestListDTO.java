package com.github.kjtang.activiti.core.dto.process;

import com.github.kjtang.activiti.core.dto.common.BasePageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by kaijun on 2018/10/23.
 */
@Data
@ApiModel("查询我的请求")
public class GetMyRequestListDTO extends BasePageDTO {

    /**
     * 流程启动者id
     */
    @ApiModelProperty("流程启动者")
    private String initiatorId;

    @ApiModelProperty("查询开始时间")
    private Date startTime;

    @ApiModelProperty("查询结束时间")
    private Date endTime;

    @ApiModelProperty("流程状态：1表示已经结束，0表示未结束,不传递查我的所有请求")
    private Integer processState;

    public String getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(String initiatorId) {
        this.initiatorId = initiatorId;
    }

}
