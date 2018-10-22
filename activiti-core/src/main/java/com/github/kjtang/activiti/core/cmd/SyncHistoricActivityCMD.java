package com.github.kjtang.activiti.core.cmd;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangkj@hua-cloud.com.cn on 2018/10/22.
 * 同步历史活动节点
 */
public class SyncHistoricActivityCMD implements Command<List<HistoricActivityInstanceEntity>> {

    private String processInstanceId;

    private List<HistoricActivityInstanceEntity> historyActivityEntityList;

    public SyncHistoricActivityCMD(String processInstanceId, List<HistoricActivityInstanceEntity> historyActivityEntityList){
        this.processInstanceId = processInstanceId;
        this.historyActivityEntityList = historyActivityEntityList;
    }

    @Override
    public List<HistoricActivityInstanceEntity> execute(CommandContext commandContext) {
        HistoricActivityInstanceEntityManager historicActivityInstanceEntityManager =
                commandContext.getHistoricActivityInstanceEntityManager();
        // 删除所有的历史活动节点
        historicActivityInstanceEntityManager
                .deleteHistoricActivityInstancesByProcessInstanceId(processInstanceId);
        // 提交到数据库
        commandContext.getDbSqlSession().flush();
        // 添加历史活动节点的
        for (HistoricActivityInstanceEntity hai : historyActivityEntityList) {
                historicActivityInstanceEntityManager.insert(hai);
        }
        // 提交到数据库
        commandContext.getDbSqlSession().flush();
        return null;
    }
}
