package com.github.kjtang.activiti.config;

import com.github.kjtang.activiti.core.listener.GlobalActivitiEventListener;
import com.github.kjtang.activiti.core.listener.ProcessTaskAssigneeEventHandler;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author liys
 * @date 2018/6/1217:14
 */
@Component
public class SpringProcessEngineConfigurationPostProcessor implements BeanPostProcessor {

    @Autowired
    private GlobalActivitiEventListener globalActivitiEventListener;

    @Autowired
    private ProcessTaskAssigneeEventHandler processTaskAssigneeEventHandler;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
         return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof SpringProcessEngineConfiguration){
            SpringProcessEngineConfiguration springProcessEngineConfiguration=(SpringProcessEngineConfiguration)bean;
            springProcessEngineConfiguration.setActivityFontName("宋体");
            springProcessEngineConfiguration.setLabelFontName("宋体");
            springProcessEngineConfiguration.setActivityFontName("宋体");
           // springProcessEngineConfiguration.setProcessDiagramGenerator(new ExtDefaultProcessDiagramGenerator());
            //此处也可以配置全局监听器
        }
        if(bean instanceof RuntimeService){
            RuntimeService runtimeService = (RuntimeService)bean;
            globalActivitiEventListener.addHandlers(ActivitiEventType.TASK_CREATED,processTaskAssigneeEventHandler);
            runtimeService.addEventListener(globalActivitiEventListener);
        }
        return bean;
    }
}
