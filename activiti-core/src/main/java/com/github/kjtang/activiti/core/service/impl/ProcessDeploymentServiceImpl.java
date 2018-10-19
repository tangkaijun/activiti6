package com.github.kjtang.activiti.core.service.impl;

import com.github.kjtang.activiti.core.service.ProcessDeploymentService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * Created by tangkj on 2018/10/19.
 */
@Service
public class ProcessDeploymentServiceImpl implements ProcessDeploymentService {

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public void deployment(String processName, String processResourceName, InputStream processResource) throws Exception {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        //1.验证流程定义文件名格式
        if(StringUtils.isEmpty(processName)){
            deploymentBuilder.name(processResourceName);
        }else{
            deploymentBuilder.name(processName);
        }
        //2.根据资源文件名调用不同的部署方法
        if(StringUtils.endsWith(processResourceName,".zip")|| StringUtils.endsWith(processResourceName,".bar")){
            ZipInputStream zipInputStream = new ZipInputStream(processResource);
            deploymentBuilder.addZipInputStream(zipInputStream);
        }else if(StringUtils.endsWith(processResourceName,".bpmn20.xml")||StringUtils.endsWith(processResourceName,".bpmn")){
            deploymentBuilder.addInputStream(processResourceName,processResource);
        }else{
           // throw CommonException.exception(CommonErrorCode.PARAM_ERROR_CODE,"部署文件格式错误");
        }
        //3.部署流程
        Deployment deployment = deploymentBuilder.deploy();
       // DeploymentVO deploymentVO = CommonUtils.newInstance(deployment,DeploymentVO.class);
        //LOGGER.info("流程部署成功:{}",deploymentVO);
        //return deploymentVO;
    }

}
