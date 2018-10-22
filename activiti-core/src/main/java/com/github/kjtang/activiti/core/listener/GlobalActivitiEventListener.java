package com.github.kjtang.activiti.core.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangkj@hua-cloud.com.cn on 2018/9/30.
 * activiti全局异常监听器
 */
@Component
public class GlobalActivitiEventListener implements ActivitiEventListener {

    //事件处理器
    private Map<ActivitiEventType, ProcessEventHandler> handlers = new HashMap<>();

    @Override
    public void onEvent(ActivitiEvent event) {
        ProcessEventHandler handler = handlers.get(event.getType());
        if (handler != null) {
            handler.handle(event);
        }
    }

    @Override
    public boolean isFailOnException() {
        return true;
    }

    public Map<ActivitiEventType, ProcessEventHandler> getHandlers() {
        return handlers;
    }

    public void addHandlers(ActivitiEventType eventType, ProcessEventHandler handler) {
        handlers.put(eventType,handler);
    }


}