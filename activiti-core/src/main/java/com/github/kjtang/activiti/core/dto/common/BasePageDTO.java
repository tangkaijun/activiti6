package com.github.kjtang.activiti.core.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by kjtang on 2018/10/16.
 */
@Data
public class BasePageDTO implements Serializable{

    @ApiModelProperty("页号")
    private Integer pageNum;

    @ApiModelProperty("分页大小")
    private Integer pageSize;

}