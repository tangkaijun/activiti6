package com.github.kjtang.activiti.core.service;

import java.io.InputStream;

/**
 * Created by 流程部署相关功能扩展 on 2018/10/19.
 */
public interface ProcessDeploymentService {


    /**
     * 流程部署（流程图片由）
     * @param processResourceName 流程资源文件
     * @param processName 部署流程名称
     * @return DeploymentVO 部署的流程信息（对应activitiDB中Deployement表中的信息）
     * @throws Exception 在部署过程中出现任何异常，则向上抛出，如文件名格式不正确等异常
     */
    void deployment(String processName,String processResourceName,InputStream processResource) throws Exception;

}