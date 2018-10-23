package com.github.kjtang.activiti.core.service;

import com.github.kjtang.activiti.core.dto.task.BackTaskDTO;
import com.github.kjtang.activiti.core.dto.task.ExecuteTaskActionDTO;
import com.github.kjtang.activiti.core.dto.task.GetToDoSignTaskPagedListDTO;
import com.github.kjtang.activiti.core.dto.task.GetToDoTaskPagedListDTO;
import com.github.kjtang.activiti.core.vo.task.TaskVO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Created by kjtang on 2018/10/16.
 */
public interface TaskExtService {

    /**
     * 查询代办任务
     * @return
     */
    PageInfo<TaskVO> getToDoTaskPagedList(GetToDoTaskPagedListDTO toDoTaskPageListDTO);

    /**
     * 查询代签收任务（包括别人签收和未签收任务）
     * @param toDoSignTaskPagedListDTO
     * @return
     */
    PageInfo<TaskVO> getToDoSignTaskPagedList(GetToDoSignTaskPagedListDTO toDoSignTaskPagedListDTO);

    /**
     * 处理任务
     * @param executeTaskActionDTO
     */
    void executeTaskAction(@RequestBody ExecuteTaskActionDTO executeTaskActionDTO);

    /**
     * 回退任务
     * @param backTaskDTO
     */
    void backTask(BackTaskDTO backTaskDTO);

    /**
     * 查询某个流程的代办任务
     * @param processInstanceId
     * @return
     */
    List<TaskVO> getProcessToDoTask(String processInstanceId);

}