package com.github.kjtang.activiti.core.service.impl;

import com.github.kjtang.activiti.core.cmd.SyncHistoricActivityCMD;
import com.github.kjtang.activiti.core.cmd.JumpTaskCMD;
import com.github.kjtang.activiti.core.dto.task.BackTaskDTO;
import com.github.kjtang.activiti.core.dto.task.ExecuteTaskActionDTO;
import com.github.kjtang.activiti.core.dto.task.GetToDoSignTaskPagedListDTO;
import com.github.kjtang.activiti.core.dto.task.GetToDoTaskPagedListDTO;
import com.github.kjtang.activiti.core.service.HistoryExtService;
import com.github.kjtang.activiti.core.service.TaskExtService;
import com.github.kjtang.activiti.core.vo.task.TaskVO;
import com.github.kjtang.activiti.enums.TaskActionEnum;
import com.github.kjtang.activiti.exception.RCodeException;
import com.github.pagehelper.PageInfo;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntityManager;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private HistoryExtService historyExtService;

    @Autowired
    private ManagementService managementService;

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

    @Transactional
    @Override
    public void backTask(BackTaskDTO backTaskDTO) {

        //查询目标任务
        String targetTaskId = backTaskDTO.getTargetTaskId();
        HistoricTaskInstance targetTask = historyExtService.getHisoricTask(targetTaskId);
        //当前任务
        String currentTaskId = backTaskDTO.getCurrentTaskId();
        HistoricTaskInstance currentTask =  historyExtService.getHisoricTask(currentTaskId);
        // 流程实例ID
        String processInstanceId = currentTask.getProcessInstanceId();
        // 获取已经完成的任务列表
        List<HistoricTaskInstance> historyTaskList = historyExtService.getHisoricTaskList(processInstanceId);
        // 获取已经完成的所有环节列表
        List<HistoricActivityInstance> historyActivityList = historyExtService.getHistoricActivityList(processInstanceId);
        //任务完成后要删除的历史任务集合
        List<HistoricTaskInstance> deleteTaskList = new ArrayList<>();
        Integer targetTaskNo = Integer.valueOf(targetTaskId);
        for (HistoricTaskInstance historicTask : historyTaskList) {
            Integer historicTaskNo = Integer.valueOf(historicTask.getId());
            if (targetTaskNo <= historicTaskNo) {
                deleteTaskList.add(historicTask);
            }
        }
        //任务完成后要添加的历史节点集合
        List<HistoricActivityInstanceEntity> insertHistoryActivityList = new ArrayList<>();
        for (int i = 0; i < historyActivityList.size() - 1; i++) {
            HistoricActivityInstance historicActivity = historyActivityList.get(i);
            // 历史活动节点的任务编号
            Integer historicActivityTaskId;
            String taskId = historicActivity.getTaskId();
            if (taskId != null) {
                historicActivityTaskId = Integer.valueOf(taskId);
                if (historicActivityTaskId <= targetTaskNo) {
                    insertHistoryActivityList.add((HistoricActivityInstanceEntity) historicActivity);
                }
            } else {
                if (historicActivity.getActivityType().equals("startEvent")) {
                    insertHistoryActivityList.add((HistoricActivityInstanceEntity) historicActivity);
                } else if (historicActivity.getActivityType().equals("exclusiveGateway")) {
                    insertHistoryActivityList.add((HistoricActivityInstanceEntity) historicActivity);
                }
            }
        }
        // 获取流程定义的节点信息
        String processDefinitionId = currentTask.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
        // 用于保存正在执行的任务节点信息
        FlowNode currentActivity = null;
        // 用于保存原来的任务节点的出口信息
        SequenceFlow oldSequenceFlow = null;

        /**
        for(FlowElement flowElement:flowElements){
           if(currentTask.getTaskDefinitionKey().equals(flowElement.getId())){
                currentActivity = (FlowNode) flowElement;
                oldSequenceFlow = currentActivity.getOutgoingFlows().get(0);
               ((FlowNode) flowElement).getOutgoingFlows().clear();
            }
        }
         */
        FlowNode targetActivity = (FlowNode)bpmnModel.getMainProcess().getFlowElement(targetTask.getTaskDefinitionKey());
        // 流程跳转
        managementService.executeCommand(new JumpTaskCMD(currentTask.getId(),targetActivity.getId()));
        HistoricActivityInstanceEntityManager historicActivityInstanceEntityManager = Context.getCommandContext().getHistoricActivityInstanceEntityManager();
        historicActivityInstanceEntityManager.deleteHistoricActivityInstancesByProcessInstanceId(processInstanceId);
        for (HistoricActivityInstanceEntity historicActivityInstance : insertHistoryActivityList) {
            historicActivityInstanceEntityManager.insert(historicActivityInstance);
        }
        /**
        HistoricActivityInstance destActivity = historyService.createHistoricActivityInstanceQuery().singleResult();
        FlowNode targetFlowNode = (FlowNode)bpmnModel.getMainProcess().getFlowElement(destActivity.getActivityId());
        Execution execution = runtimeService.createExecutionQuery().executionId(currentTask.getExecutionId()).singleResult();
        String activityId = execution.getActivityId();
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);
        //记录原活动方向
        List<SequenceFlow> oriSequenceFlows = new ArrayList<SequenceFlow>();
        oriSequenceFlows.addAll(currentFlowNode.getOutgoingFlows());
        //清理活动方向
        currentFlowNode.getOutgoingFlows().clear();
        //建立新方向
        List<SequenceFlow> newSequenceFlowList = new ArrayList<SequenceFlow>();
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(targetFlowNode);
        newSequenceFlowList.add(newSequenceFlow);
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);
        Authentication.setAuthenticatedUserId(backTaskDTO.getAssignee());
         */
        //恢复原方向
       // currentFlowNode.setOutgoingFlows(oriSequenceFlows);
        //Authentication.setAuthenticatedUserId(backTaskDTO.getAssignee());
        //taskService.addComment(currentTaskId, currentTask.getProcessInstanceId(), "撤回");
        //完成任务
        //taskService.complete(currentTask.getId(),new HashMap<String,Object>());
        // 删除历史任务
        /**
        for (HistoricTaskInstance historicTaskInstance : deleteTaskList) {
            historyService.deleteHistoricTaskInstance(historicTaskInstance.getId());
        }*/
        // 同步历史活动节点
        /**
        managementService.executeCommand((Command<List<HistoricActivityInstanceEntity>>) commandContext -> {
            HistoricActivityInstanceEntityManager historicActivityInstanceEntityManager =
                    commandContext.getHistoricActivityInstanceEntityManager();
            // 删除所有的历史活动节点
            historicActivityInstanceEntityManager
                    .deleteHistoricActivityInstancesByProcessInstanceId(processInstanceId);
            // 提交到数据库
            //commandContext.getDbSqlSession().flush();
            // 添加历史活动节点的
            for (HistoricActivityInstanceEntity historicActivityInstance : insertHistoryActivityList) {
                historicActivityInstanceEntityManager.insert(historicActivityInstance);
            }
           // commandContext.getDbSqlSession().flush();
            return null;
        });*/
    }

}
