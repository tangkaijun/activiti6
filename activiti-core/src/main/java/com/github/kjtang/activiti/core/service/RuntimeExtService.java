package com.github.kjtang.activiti.core.service;

import com.github.kjtang.activiti.core.dto.process.StartProcessDTO;
import com.github.kjtang.activiti.core.vo.process.ProcessInstanceVO;
import java.io.InputStream;

/**
 * Created by kjtang on 2018/10/19.
 * 该模块包括运行中流程实例及历史流程实例功能扩展
 */
public interface RuntimeExtService {

    /**
     * 启动流程
     * @param startProcessDTO
     * @return
     */
    ProcessInstanceVO startProcess(StartProcessDTO startProcessDTO);


    InputStream getResourceDiagramInputStream(String processInstanceId);
}