package com.github.kjtang.activiti.core.vo.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by kaijun on 2018/10/20.
 */
@Data
public class CommentVO implements Serializable{

    @ApiModelProperty("批注类型：event（事件）comment（意见）")
    private String type;

    @ApiModelProperty("用户id，填写人")
    private String userId;

    @ApiModelProperty("填写时间")
    private Date time;

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("流程实例id")
    private String processInstanceId;

    @ApiModelProperty("操作类型:AddUserLink、DeleteUserLink、AddGroupLink、DeleteGroupLink、AddComment、AddAttachment、DeleteAttachment")
    private String action;

    @ApiModelProperty("处理意见")
    private String message;

    @ApiModelProperty("全部消息")
    private String fullMessage;

}
