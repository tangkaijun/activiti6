package com.github.kjtang.activiti.core.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.stereotype.Component;

/**
 * Created by tangkj@hua-cloud.com.cn on 2018/9/30.
 * activiti全局异常监听器
 */
@Component
public class GlobalActivitiEventListener implements ActivitiEventListener {

    @Override
    public void onEvent(ActivitiEvent event) {

    }

    @Override
    public boolean isFailOnException() {
        return true;
    }


}