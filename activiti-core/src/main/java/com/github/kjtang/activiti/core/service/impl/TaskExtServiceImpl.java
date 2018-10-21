package com.github.kjtang.activiti.core.service.impl;

import com.github.kjtang.activiti.core.dto.task.BackTaskDTO;
import com.github.kjtang.activiti.core.dto.task.ExecuteTaskActionDTO;
import com.github.kjtang.activiti.core.dto.task.GetToDoSignTaskPagedListDTO;
import com.github.kjtang.activiti.core.dto.task.GetToDoTaskPagedListDTO;
import com.github.kjtang.activiti.core.service.TaskExtService;
import com.github.kjtang.activiti.core.vo.task.TaskVO;
import com.github.kjtang.activiti.enums.TaskActionEnum;
import com.github.kjtang.activiti.exception.RCodeException;
import com.github.pagehelper.PageInfo;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by tangkj on 2018/10/16.
 * 流程引擎任务模块扩展
 */
@Component
public class TaskExtServiceImpl implements TaskExtService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public PageInfo<TaskVO> getToDoTaskPagedList(GetToDoTaskPagedListDTO toDoTaskPageListDTO) {
        TaskQuery taskQuery = taskService.createTaskQuery();
        String assignee = toDoTaskPageListDTO.getAssignee();
        //1.根据获选人或任务处理人查询代办任务
        if(!StringUtils.hasLength(assignee)){
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

    @Override
    public PageInfo<TaskVO> getToDoSignTaskPagedList(GetToDoSignTaskPagedListDTO toDoSignTaskPagedListDTO) {
        return null;
    }

    @Override
    public void executeTaskAction(ExecuteTaskActionDTO executeTaskActionDTO) {
        String action = executeTaskActionDTO.getExecuteAction();
        TaskActionEnum taskActionEnum = TaskActionEnum.valueByName(action);
        String taskId = executeTaskActionDTO.getTaskId();
        Authentication.setAuthenticatedUserId(executeTaskActionDTO.getAssignee());
        switch (taskActionEnum){
            case ACTION_COMPLETE:
                TaskQuery taskQuery = taskService.createTaskQuery();
                Task task = taskQuery.taskId(taskId).singleResult();
                taskService.addComment(taskId,task.getProcessInstanceId(),executeTaskActionDTO.getComment());//新增批注
                Attachment attachment = taskService.createAttachment("title", taskId, task.getProcessInstanceId(), "descirption", "content", "http://www.baidu.com/index.html");
                taskService.saveAttachment(attachment);
                taskService.complete(executeTaskActionDTO.getTaskId());
                break;
        }
    }

    @Override
    public void backTask(BackTaskDTO backTaskDTO) {

        //查询目标节点
        String targetTaskId = backTaskDTO.getTargetTaskId();
        HistoricTaskInstance destinationTaskInstance = historyService
                .createHistoricTaskInstanceQuery()
                .taskId(targetTaskId)
                .includeProcessVariables()
                .includeTaskLocalVariables()
                .singleResult();
        //当前任务
        String currentTaskId = backTaskDTO.getCurrentTaskId();

        Task task = taskService.createTaskQuery().taskId(currentTaskId).singleResult();

        HistoricTaskInstance currentTaskInstance = historyService
                .createHistoricTaskInstanceQuery()
                .taskId(currentTaskId)
                .includeProcessVariables()
                .includeTaskLocalVariables()
                .singleResult();

        // 流程定义ID
        String processDefinitionId = currentTaskInstance.getProcessDefinitionId();
        // 流程实例ID
        String processInstanceId = currentTaskInstance.getProcessInstanceId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        // 获取已经完成的任务列表
        List<HistoricTaskInstance> finishTaskList = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .includeProcessVariables()
                .includeTaskLocalVariables()
                .orderByTaskCreateTime()
                .asc()
                .list();

        //获取已经完成的所有环节列表
        List<HistoricActivityInstance> finishActivityList = historyService
                        .createHistoricActivityInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .orderByHistoricActivityInstanceStartTime()
                        .asc()
                        .list();

        HistoricActivityInstance destActivity = historyService.createHistoricActivityInstanceQuery().singleResult();
        FlowNode targetFlowNode = (FlowNode)bpmnModel.getMainProcess().getFlowElement(destActivity.getActivityId());
        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        String activityId = execution.getActivityId();
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);
        //记录原活动方向
        List<SequenceFlow> oriSequenceFlows = new ArrayList<SequenceFlow>();
        oriSequenceFlows.addAll(currentFlowNode.getOutgoingFlows());
        //清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();



        //建立新方向
        List<SequenceFlow> newSequenceFlowList = new ArrayList<SequenceFlow>();
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(targetFlowNode);
        newSequenceFlowList.add(newSequenceFlow);
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);
        Authentication.setAuthenticatedUserId(backTaskDTO.getAssignee());
        taskService.addComment(task.getId(), task.getProcessInstanceId(), "撤回");
        //完成任务
        taskService.complete(task.getId(),new HashMap<String,Object>());
        //恢复原方向
        currentFlowNode.setOutgoingFlows(oriSequenceFlows);

    }

}
