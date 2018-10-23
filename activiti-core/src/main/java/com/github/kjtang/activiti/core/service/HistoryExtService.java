package com.github.kjtang.activiti.core.service;

import com.github.kjtang.activiti.core.dto.process.GetMyRequestListDTO;
import com.github.kjtang.activiti.core.vo.history.HistoricProcessInstanceVO;
import com.github.kjtang.activiti.core.vo.process.ProcessInstanceVO;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;

import java.util.List;

/**
 * Created by kaijun on 2018/10/20.
 */
public interface HistoryExtService {

    /**
     * 通过流程任务id查询历史任务
     * @param taskId
     * @return
     */
    HistoricTaskInstance getHisoricTask(String taskId);

    /**
     * 查询历史环节
     * @param activityId
     * @return
     */
    HistoricActivityInstance getHistoricActivity(String activityId);

    /**
     * 查询历史任务列表
     * @param processInstanceId
     * @return
     */
    List<HistoricTaskInstance> getHisoricTaskList(String processInstanceId);

    /**
     * 查询历史环节列表
     * @param processInstanceId
     * @return
     */
    List<HistoricActivityInstance> getHistoricActivityList(String processInstanceId);

    /**
     * 查询我的请求
     * @param myRequestListDTO
     * @return
     */
    PageInfo<HistoricProcessInstanceVO> getMyRequestPagedList(GetMyRequestListDTO myRequestListDTO);

}