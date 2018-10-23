package com.github.kjtang.activiti.core.service.impl;

import com.github.kjtang.activiti.core.dto.process.StartProcessDTO;
import com.github.kjtang.activiti.core.service.RuntimeExtService;
import com.github.kjtang.activiti.core.vo.process.ProcessInstanceVO;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

/**
 * Created by kjtang on 2018/10/19.
 * https://www.cnblogs.com/leemup/p/activiti6_trace.html
 * https://blog.csdn.net/xiaohelong2005/article/details/78628922?utm_source=blogxgwz6
 */
@Service
public class RuntimeExtServiceImpl implements RuntimeExtService {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessEngine processEngine;

    @Override
    public ProcessInstanceVO startProcess(StartProcessDTO startProcessDTO) {
        Authentication.setAuthenticatedUserId(startProcessDTO.getInitiatorId());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(startProcessDTO.getProcessDefinitionKey(),startProcessDTO.getBusinessKey(),startProcessDTO.getProcessVariables());
        ProcessInstanceVO processInstanceVO = new ProcessInstanceVO();
        BeanUtils.copyProperties(processInstance,processInstanceVO);
        return processInstanceVO;
    }

    @Override
    public InputStream getResourceDiagramInputStream(String processInstanceId) {
        try {
            // 获取历史流程实例
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

            // 获取流程中已经执行的节点，按照执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceId().asc().list();

            // 构造已执行的节点ID集合
            List<String> executedActivityIdList = new ArrayList<String>();
            for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                executedActivityIdList.add(activityInstance.getActivityId());
            }

            // 获取bpmnModel
            BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
            // 获取流程已发生流转的线ID集合
            List<String> flowIds = this.getExecutedFlows(bpmnModel, historicActivityInstanceList);

            // 使用默认配置获得流程图表生成器，并生成追踪图片字符流
            ProcessDiagramGenerator processDiagramGenerator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
            InputStream imageStream = processDiagramGenerator.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds, "宋体", "微软雅黑", "黑体", null, 2.0);
            return imageStream;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> getExecutedFlows(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances) {
        // 流转线ID集合
        List<String> flowIdList = new ArrayList<String>();
        // 全部活动实例
        List<FlowNode> historicFlowNodeList = new LinkedList<FlowNode>();
        // 已完成的历史活动节点
        List<HistoricActivityInstance> finishedActivityInstanceList = new LinkedList<HistoricActivityInstance>();
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            // historicFlowNodeList.add((FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true));
            if (historicActivityInstance.getEndTime() != null) {
                finishedActivityInstanceList.add(historicActivityInstance);
            }
        }

        // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        FlowNode currentFlowNode = null;
        for (HistoricActivityInstance currentActivityInstance : finishedActivityInstanceList) {
            // 获得当前活动对应的节点信息及outgoingFlows信息
            currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId(), true);
            List<SequenceFlow> sequenceFlowList = currentFlowNode.getOutgoingFlows();

            /**
             * 遍历outgoingFlows并找到已已流转的
             * 满足如下条件认为已已流转：
             * 1.当前节点是并行网关或包含网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转
             * 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最近的流转节点视为有效流转
             */
            FlowNode targetFlowNode = null;
            if (BpmsActivityTypeEnum.PARALLEL_GATEWAY.getType().equals(currentActivityInstance.getActivityType())
                    || BpmsActivityTypeEnum.INCLUSIVE_GATEWAY.getType().equals(currentActivityInstance.getActivityType())) {
                // 遍历历史活动节点，找到匹配Flow目标节点的
                for (SequenceFlow sequenceFlow : sequenceFlowList) {
                    // targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef(), true);
                    if (historicFlowNodeList.contains(targetFlowNode)) {
                        flowIdList.add(sequenceFlow.getId());
                    }
                }
            } else {
                List<Map<String, String>> tempMapList = new LinkedList<Map<String,String>>();
                // 遍历历史活动节点，找到匹配Flow目标节点的
                for (SequenceFlow sequenceFlow : sequenceFlowList) {
                    for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                        if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                            tempMapList.add(toMap("flowId", sequenceFlow.getId(), "activityStartTime", String.valueOf(historicActivityInstance.getStartTime().getTime())));
                        }
                    }
                }

                // 遍历匹配的集合，取得开始时间最早的一个
                long earliestStamp = 0L;
                String flowId = null;
                for (Map<String, String> map : tempMapList) {
                    long activityStartTime = Long.valueOf(map.get("activityStartTime"));
                    if (earliestStamp == 0 || earliestStamp >= activityStartTime) {
                        earliestStamp = activityStartTime;
                        flowId = map.get("flowId");
                    }
                }
                flowIdList.add(flowId);
            }
        }
        return flowIdList;
    }

    public static enum BpmsActivityTypeEnum {

        START_EVENT("startEvent", "开始事件"),
        END_EVENT("endEvent", "结束事件"),
        USER_TASK("userTask", "用户任务"),
        EXCLUSIVE_GATEWAY("exclusiveGateway", "排他网关"),
        PARALLEL_GATEWAY("parallelGateway", "并行网关"),
        INCLUSIVE_GATEWAY("inclusiveGateway", "包含网关");

        private String type;
        private String name;

        private BpmsActivityTypeEnum(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    public static <V, V1 extends V, V2 extends V> Map<String, V> toMap(String name1, V1 value1, String name2, V2 value2) {
        return populateMap(new HashMap<String, V>(), name1, value1, name2, value2);
    }
    @SuppressWarnings("unchecked")
    private static <K, V> Map<String, V> populateMap(Map<String, V> map, Object... data) {
        for (int i = 0; i < data.length;) {
            map.put((String) data[i++], (V) data[i++]);
        }
        return map;
    }

}
