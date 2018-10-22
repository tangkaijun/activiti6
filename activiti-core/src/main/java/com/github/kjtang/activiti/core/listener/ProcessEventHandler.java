package com.github.kjtang.activiti.core.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;

/**
 * Created by tangkj@hua-cloud.com.cn on 2018/9/30.
 * 流程事件处理
 */
public interface ProcessEventHandler {

    /**
     * 事件处理
     * @param event
     */
    void handle(ActivitiEvent event);

}