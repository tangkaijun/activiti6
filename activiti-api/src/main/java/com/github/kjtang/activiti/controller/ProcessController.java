package com.github.kjtang.activiti.controller;

import com.github.kjtang.activiti.core.service.ProcessDeploymentService;
import com.github.kjtang.activiti.core.vo.deployment.DeploymentVO;
import com.github.kjtang.activiti.response.ResponseVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by kjtang on 2018/10/19.
 */
@RestController
@RequestMapping("activiti6/api/process")
public class ProcessController {

    @Autowired
    private ProcessDeploymentService processDeploymentService;

    @ApiOperation("流程部署(流程图片引擎自动生成，支持.zip,.bar,.bpmn20.xml,.bpmn文件格式)")
    @RequestMapping(value = "/deploymentProcess",method = RequestMethod.POST)
    public ResponseVO<DeploymentVO> deployment(@RequestParam("processName") String processName, @RequestParam("processSource") MultipartFile processSource) throws Exception{
         processDeploymentService.deployment(processName,processSource.getOriginalFilename(),processSource.getInputStream());
         return null;
    }

}