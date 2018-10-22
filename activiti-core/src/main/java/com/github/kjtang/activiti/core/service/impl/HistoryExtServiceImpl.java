package com.github.kjtang.activiti.core.service.impl;

import com.github.kjtang.activiti.core.service.HistoryExtService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kaijun on 2018/10/20.
 */
@Service
public class HistoryExtServiceImpl implements HistoryExtService{

    @Autowired
    private HistoryService historyService;

    @Override
    public HistoricTaskInstance getHisoricTask(String taskId) {
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();
        return historicTaskInstanceQuery.taskId(taskId).includeProcessVariables().includeTaskLocalVariables().singleResult();
    }

    @Override
    public HistoricActivityInstance getHistoricActivity(String activityId) {
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();
        return historicActivityInstanceQuery.activityId(activityId).singleResult();
    }

    @Override
    public List<HistoricTaskInstance> getHisoricTaskList(String processInstanceId) {
        return historyService.createHistoricTaskInstanceQuery()
                              .processInstanceId(processInstanceId)
                              .includeTaskLocalVariables()
                              .includeProcessVariables()
                              .orderByHistoricTaskInstanceStartTime()
                              .asc()
                              .list();
    }

    @Override
    public List<HistoricActivityInstance> getHistoricActivityList(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery()
                             .processInstanceId(processInstanceId)
                             .orderByHistoricActivityInstanceStartTime()
                             .asc()
                             .list();
    }

}
