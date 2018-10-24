package com.github.kjtang.activiti.controller;

import com.github.kjtang.activiti.core.dto.process.GetMyRequestListDTO;
import com.github.kjtang.activiti.core.dto.process.StartProcessDTO;
import com.github.kjtang.activiti.core.service.HistoryExtService;
import com.github.kjtang.activiti.core.service.ProcessDeploymentService;
import com.github.kjtang.activiti.core.service.RuntimeExtService;
import com.github.kjtang.activiti.core.vo.deployment.DeploymentVO;
import com.github.kjtang.activiti.core.vo.history.HistoricProcessInstanceVO;
import com.github.kjtang.activiti.core.vo.process.ProcessInstanceVO;
import com.github.kjtang.activiti.response.ResponseVO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kjtang on 2018/10/19.
 */
@Api("流程管理")
@RestController
@RequestMapping("activiti6/process")
public class ProcessController {

    @Autowired
    private ProcessDeploymentService processDeploymentService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RuntimeExtService processInstanceService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    protected ProcessEngineConfiguration processEngineConfiguration;

    @Autowired
    private HistoryExtService historyExtService;

    @ApiOperation("流程部署(流程图片引擎自动生成，支持.zip,.bar,.bpmn20.xml,.bpmn文件格式)")
    @RequestMapping(value = "/deploymentProcess",method = RequestMethod.POST)
    public ResponseVO<DeploymentVO> deployment(@RequestParam("processName") String processName, @RequestParam("processSource") MultipartFile processSource) throws Exception{
         processDeploymentService.deployment(processName,processSource.getOriginalFilename(),processSource.getInputStream());

         return null;
    }

    @ApiOperation("查询我的请求")
    @RequestMapping(value = "/getMyRequestPagedList",method = RequestMethod.POST)
    public ResponseEntity<PageInfo<HistoricProcessInstanceVO>> getMyRequestPagedList(@RequestBody GetMyRequestListDTO myRequestListDTO){
       return ResponseEntity.ok(historyExtService.getMyRequestPagedList(myRequestListDTO));
    }

    @ApiOperation("查看流程实例图")
    @RequestMapping(value="getProcessInstanceDiagram",method = RequestMethod.GET)
    public void getProcessInstanceDiagram(@RequestParam("processInstanceId") String processInstanceId, HttpServletResponse response) throws Exception{
        InputStream inputStream =  processInstanceService.getResourceDiagramInputStream(processInstanceId);
        response.setContentType("image/png"); //设置返回的文件类型
        int i=inputStream.available(); //得到文件大小

        byte data[]=new byte[i];

        inputStream.read(data); //读数据

        inputStream.close();

        response.setContentType("image/png"); //设置返回的文件类型

        OutputStream toClient=response.getOutputStream(); //得到向客户端输出二进制数据的对象

        toClient.write(data); //输出数据

        toClient.close();
    }

    @RequestMapping(value="getProcessInstanceDiagram1",method = RequestMethod.GET)
    public void getProcessInstanceDiagram1(@RequestParam("processInstanceId") String processInstanceId, HttpServletResponse response) throws Exception{
//logger.info("[开始]-获取流程图图像");
        try {
            //  获取历史流程实例
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();

            if (historicProcessInstance == null) {
                //throw new BusinessException("获取流程实例ID[" + pProcessInstanceId + "]对应的历史流程实例失败！");
            }
            else
            {
                // 获取流程定义
                ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                        .getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());

                // 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
                List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(processInstanceId).orderByHistoricActivityInstanceId().asc().list();

                // 已执行的节点ID集合
                List<String> executedActivityIdList = new ArrayList<>();
                for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                    executedActivityIdList.add(activityInstance.getActivityId());
                }

                BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

                // 已执行的线集合(高亮)
                List<String> flowIds = getHighLightedFlows(bpmnModel,processDefinition, historicActivityInstanceList);
                // 获取流程图图像字符流
                ProcessDiagramGenerator pec = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
                //配置字体
                InputStream imageStream = pec.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds,"宋体","微软雅黑","黑体",null,2.0);

                response.setContentType("image/png");
                OutputStream os = response.getOutputStream();
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = imageStream.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                imageStream.close();
            }
            //logger.info("[完成]-获取流程图图像");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //logger.error("【异常】-获取流程图失败！" + e.getMessage());
            //throw new BusinessException("获取流程图失败！" + e.getMessage());
        }
    }

    public List<String> getHighLightedFlows(BpmnModel bpmnModel,ProcessDefinitionEntity processDefinitionEntity,List<HistoricActivityInstance> historicActivityInstances)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //24小时制
        List<String> highFlows = new ArrayList<>();// 用以保存高亮的线flowId

        for(HistoricActivityInstance historicActivityInstance:historicActivityInstances){
            //historicActivityInstance.getEndTime()==null;表示为代办任务并没有结束
            // 获得当前活动对应的节点信息及outgoingFlows信息
            FlowNode currentFlowNode = (FlowNode)bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId());
            List<SequenceFlow> sequenceFlowList = currentFlowNode.getOutgoingFlows();
        }
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            // 对历史流程节点进行遍历
            // 得到节点定义的详细信息
            FlowNode activityImpl = (FlowNode)bpmnModel.getMainProcess().getFlowElement(historicActivityInstances.get(i).getActivityId());
            List<FlowNode> sameStartTimeNodes = new ArrayList<>();// 用以保存后续开始时间相同的节点
            FlowNode sameActivityImpl1 = null;

            HistoricActivityInstance activityImpl_ = historicActivityInstances.get(i);// 第一个节点
            HistoricActivityInstance activityImp2_ ;

            for(int k = i + 1 ; k <= historicActivityInstances.size() - 1; k++)
            {
                activityImp2_ = historicActivityInstances.get(k);// 后续第1个节点

                if ( activityImpl_.getActivityType().equals("userTask") && activityImp2_.getActivityType().equals("userTask") &&
                        df.format(activityImpl_.getStartTime()).equals(df.format(activityImp2_.getStartTime()))   ) //都是usertask，且主节点与后续节点的开始时间相同，说明不是真实的后继节点
                {

                }
                else
                {
                    sameActivityImpl1 = (FlowNode)bpmnModel.getMainProcess().getFlowElement(historicActivityInstances.get(k).getActivityId());//找到紧跟在后面的一个节点
                    break;
                }

            }
            sameStartTimeNodes.add(sameActivityImpl1); // 将后面第一个节点放在时间相同节点的集合里
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++)
            {
                HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);// 后续第一个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);// 后续第二个节点

                if (df.format(activityImpl1.getStartTime()).equals(df.format(activityImpl2.getStartTime()))  )
                {// 如果第一个节点和第二个节点开始时间相同保存
                    FlowNode sameActivityImpl2 = (FlowNode)bpmnModel.getMainProcess().getFlowElement(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                }
                else
                {// 有不相同跳出循环
                    break;
                }
            }
            List<SequenceFlow> pvmTransitions = activityImpl.getOutgoingFlows() ; // 取出节点的所有出去的线

            for (SequenceFlow pvmTransition : pvmTransitions)
            {// 对所有的线进行遍历
                FlowNode pvmActivityImpl = (FlowNode)bpmnModel.getMainProcess().getFlowElement( pvmTransition.getTargetRef());// 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }

        }
        return highFlows;

    }

    @ApiOperation("启动流程实例")
    @RequestMapping(value="startProcess",method = RequestMethod.POST)
    public ProcessInstanceVO startProcess(@RequestBody StartProcessDTO startProcessDTO){
        return processInstanceService.startProcess(startProcessDTO);
    }

    @RequestMapping(value="getProcessInstanceDiagram2",method = RequestMethod.GET)
    public ResponseEntity<byte[]> getProcessInstanceDiagram2(@RequestParam("processInstanceId") String processInstanceId){
        ProcessInstance processInstance =  runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if(processInstance==null){
            throw new RuntimeException("不存在该流程实例");
        }
        ProcessDefinition pde = this.repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
        if(pde != null && pde.hasGraphicalNotation()) {
            BpmnModel bpmnModel = this.repositoryService.getBpmnModel(pde.getId());
            ProcessDiagramGenerator diagramGenerator = this.processEngineConfiguration.getProcessDiagramGenerator();
            InputStream resource = diagramGenerator.generateDiagram(bpmnModel, "png", this.runtimeService.getActiveActivityIds(processInstance.getId()), Collections.emptyList(), this.processEngineConfiguration.getActivityFontName(), this.processEngineConfiguration.getLabelFontName(), this.processEngineConfiguration.getAnnotationFontName(), this.processEngineConfiguration.getClassLoader(), 1.0D);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type", "image/png");
            try {
                return new ResponseEntity(IOUtils.toByteArray(resource), responseHeaders, HttpStatus.OK);
            } catch (Exception var10) {
                throw new ActivitiIllegalArgumentException("Error exporting diagram", var10);
            }
        } else {
            throw new ActivitiIllegalArgumentException("Process instance with id '" + processInstance.getId() + "' has no graphical notation defined.");
        }
    }




}