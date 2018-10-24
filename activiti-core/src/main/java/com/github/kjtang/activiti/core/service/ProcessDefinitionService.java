package com.github.kjtang.activiti.core.service;

import org.activiti.engine.repository.ProcessDefinition;

/**
 * Created by kjtang on 2018/10/19.
 * 流程定义相关功能扩展
 */
public interface ProcessDefinitionService {


    ProcessDefinition getProcessDefinition(String processDefini);

}