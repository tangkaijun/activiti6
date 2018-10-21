package com.github.kjtang.activiti.core.listener;

import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

/**
 * Created by tangkj@hua-cloud.com.cn on 2018/9/30.
 */
@Component
public class ProcessTaskAssigneeEventHandler implements ProcessEventHandler{


    @Override
    public void handle(ActivitiEvent event) {
        ActivitiEntityEvent entityEvent=(ActivitiEntityEvent)event;
        TaskEntity taskEntity=(TaskEntity)entityEvent.getEntity();
        taskEntity.setAssignee("admin");
    }

}