package com.github.kjtang.activiti.controller;

import com.github.kjtang.activiti.core.dto.task.BackTaskDTO;
import com.github.kjtang.activiti.core.dto.task.ExecuteTaskActionDTO;
import com.github.kjtang.activiti.core.dto.task.GetToDoTaskPagedListDTO;
import com.github.kjtang.activiti.core.vo.task.TaskVO;
import com.github.pagehelper.PageInfo;
import com.github.kjtang.activiti.core.service.TaskExtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kjtang on 2018/10/17.
 */
@Api("activiti/v1.0.0")
@RestController
@RequestMapping("activiti6/task")
public class TaskController {

    @Autowired
    private TaskExtService taskExtService;

    @ApiOperation("查询代办任务")
    @RequestMapping(value="getToDoTaskPagedList",method = RequestMethod.POST)
    public ResponseEntity<PageInfo<TaskVO>> getToDoTaskPagedList(@RequestBody GetToDoTaskPagedListDTO toDoTaskPageListDTO){
        return ResponseEntity.ok(taskExtService.getToDoTaskPagedList(toDoTaskPageListDTO));
    }

    @ApiOperation("操作任务")
    @RequestMapping(value="executeTaskAction",method = RequestMethod.POST)
    public void executeTaskAction(@RequestBody ExecuteTaskActionDTO executeTaskActionDTO){
            taskExtService.executeTaskAction(executeTaskActionDTO);
    }

    @ApiOperation("回退任务")
    @RequestMapping(value="backTask",method = RequestMethod.POST)
    public void backTask(@RequestBody BackTaskDTO backTaskDTO){
           taskExtService.backTask(backTaskDTO);
    }

}