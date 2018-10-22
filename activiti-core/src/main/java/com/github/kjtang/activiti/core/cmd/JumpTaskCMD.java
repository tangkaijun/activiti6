package com.github.kjtang.activiti.core.cmd;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.*;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.activiti.bpmn.model.Process;
import java.io.Serializable;

/**
 * Created by 任务跳转命令 on 2018/10/22.
 */
public class JumpTaskCMD implements Command<Object>,Serializable {

    /**
     * 当前任务
     */
    private String currentTaskId;

    /**
     * 历史任务
     */
    private String targetActivityId;


    public JumpTaskCMD(String currentTaskId,String targetActivityId){
        this.currentTaskId = currentTaskId;
        this.targetActivityId = targetActivityId;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        // 获取当前任务的来源任务及来源节点信息
        TaskEntity taskEntity = taskEntityManager.findById(currentTaskId);
        ExecutionEntity executionEntity = executionEntityManager.findById(taskEntity.getExecutionId());
        Process process = ProcessDefinitionUtil.getProcess(executionEntity.getProcessDefinitionId());
        // 删除当前节点
        taskEntityManager.deleteTask(taskEntity, "跳转", true, true);
        // 获取要跳转的目标节点
        FlowElement targetFlowElement = process.getFlowElement(targetActivityId);
        executionEntity.setCurrentFlowElement(targetFlowElement);
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        agenda.planContinueProcessInCompensation(executionEntity);
        return null;
    }
}
