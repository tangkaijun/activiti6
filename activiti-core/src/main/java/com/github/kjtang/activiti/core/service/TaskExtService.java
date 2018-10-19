package com.github.kjtang.activiti.core.service;

import com.github.kjtang.activiti.core.dto.task.GetToDoTaskPagedListDTO;
import com.github.kjtang.activiti.core.vo.task.TaskVO;
import com.github.pagehelper.PageInfo;

/**
 * Created by kjtang on 2018/10/16.
 */
public interface TaskExtService {

    /**
     * 查询代办任务
     * @return
     */
    PageInfo<TaskVO> getToDoTaskPagedList(GetToDoTaskPagedListDTO toDoTaskPageListDTO);

}