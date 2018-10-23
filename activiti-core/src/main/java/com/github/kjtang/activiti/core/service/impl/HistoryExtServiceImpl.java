package com.github.kjtang.activiti.core.service.impl;

import com.github.kjtang.activiti.core.dto.process.GetMyRequestListDTO;
import com.github.kjtang.activiti.core.service.HistoryExtService;
import com.github.kjtang.activiti.core.vo.history.HistoricProcessInstanceVO;
import com.github.kjtang.activiti.core.vo.task.TaskVO;
import com.github.kjtang.activiti.enums.ProcessStateEnum;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
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

    @Override
    public PageInfo<HistoricProcessInstanceVO> getMyRequestPagedList(GetMyRequestListDTO myRequestListDTO) {
        String initiatorId = myRequestListDTO.getInitiatorId();
        Integer pageNum = myRequestListDTO.getPageNum();
        Integer pageSize = myRequestListDTO.getPageSize();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        historicProcessInstanceQuery.startedBy(initiatorId);//必须
        Date startTime = myRequestListDTO.getStartTime();
        //根据开始时间查询
        if(startTime!=null){
            historicProcessInstanceQuery.startedAfter(startTime);
        }
        //根据结束时间查询
        Date endTime = myRequestListDTO.getEndTime();
        if(endTime!=null){
            historicProcessInstanceQuery.startedBefore(endTime);
        }
        Integer processState = myRequestListDTO.getProcessState();
        if(processState!=null && processState.equals(ProcessStateEnum.PROCESS_FINISHED.getCode())){
            historicProcessInstanceQuery.finished();
        }else if(processState!=null && processState.equals(ProcessStateEnum.PROCESS_UNFINISHED.getCode())){
            historicProcessInstanceQuery.unfinished();
        }
        historicProcessInstanceQuery.orderByProcessInstanceStartTime().desc();
        Long totalCount = historicProcessInstanceQuery.count();
        historicProcessInstanceQuery.listPage((pageNum-1)*pageSize,pageSize);
        List<HistoricProcessInstance> historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery().startedBy(initiatorId).list();
        List<HistoricProcessInstanceVO> myProcessList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(historicProcessInstanceList)){
            historicProcessInstanceList.forEach(historicProcessInstance -> {
                HistoricProcessInstanceVO historicProcessInstanceVO = new HistoricProcessInstanceVO();
                BeanUtils.copyProperties(historicProcessInstance,historicProcessInstanceVO);
            });
        }
        PageInfo<HistoricProcessInstanceVO> pageInfo = new PageInfo<>(myProcessList);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(totalCount);
        pageInfo.setList(myProcessList);
        return pageInfo;
    }

}
