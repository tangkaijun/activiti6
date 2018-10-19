package com.github.kjtang.activiti.core.service.impl;

import com.github.kjtang.activiti.core.dto.task.GetToDoTaskPagedListDTO;
import com.github.kjtang.activiti.core.service.TaskExtService;
import com.github.kjtang.activiti.core.vo.task.TaskVO;
import com.github.kjtang.activiti.exception.RCodeException;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangkj on 2018/10/16.
 * 流程引擎任务模块扩展
 */
@Component
public class TaskExtServiceImpl implements TaskExtService {

    @Autowired
    private TaskService taskService;

    @Override
    public PageInfo<TaskVO> getToDoTaskPagedList(GetToDoTaskPagedListDTO toDoTaskPageListDTO) {
        TaskQuery taskQuery = taskService.createTaskQuery();
        String assignee = toDoTaskPageListDTO.getAssignee();
        //1.根据获选人或任务处理人查询代办任务
        if(StringUtils.hasLength(assignee)){
           throw new RCodeException(500,"代办人不能为空");
        }
        taskQuery.taskCandidateOrAssigned(assignee);
        Integer pageNum = toDoTaskPageListDTO.getPageNum();
        Integer pageSize = toDoTaskPageListDTO.getPageSize();
        taskQuery.orderByTaskPriority().desc();//根据任务优先级降序
        taskQuery.orderByTaskCreateTime().desc();//根据任务创建时间降序
        Long totalCount = taskQuery.count();//查询总任务数
        List<Task> taskList = taskQuery.listPage((pageNum-1)*pageSize,pageSize);
        List<TaskVO> taskVOList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(taskList)){
            taskList.forEach(task -> {
                TaskVO taskVO = new TaskVO();
                BeanUtils.copyProperties(task,taskVO);
                taskVOList.add(taskVO);
            });
        }
        PageInfo<TaskVO> pageInfo = new PageInfo<>(taskVOList);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(totalCount);
        pageInfo.setList(taskVOList);
        return pageInfo;
    }
}
