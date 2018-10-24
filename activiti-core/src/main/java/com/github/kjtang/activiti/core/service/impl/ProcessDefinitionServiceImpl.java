package com.github.kjtang.activiti.core.service.impl;

import com.github.kjtang.activiti.core.service.ProcessDefinitionService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by kjtang on 2018/10/19.
 */
@Service
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public ProcessDefinition getProcessDefinition(String processDefinitionKey) {
        ProcessDefinitionQuery processDefinitionQuery =  repositoryService.createProcessDefinitionQuery();
        return processDefinitionQuery.processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
    }
}
