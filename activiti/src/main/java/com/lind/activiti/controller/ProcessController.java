package com.lind.activiti.controller;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.lind.activiti.ActivitiConfig;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("model")
public class ProcessController {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ProcessEngine processEngine;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    HistoryService historyService;
    @Autowired
    TaskService taskService;
    @Autowired
    ProcessEngineConfiguration processEngineConfiguration;
    @Autowired
    ActivitiConfig.ActivitiExtendProperties properties;
    @Autowired
    HttpMessageConverters httpMessageConverters;

    /**
     * 建立页面，同时也保存.
     *
     * @param request
     * @param response
     */
    @GetMapping("create")
    public void createModel(HttpServletRequest request, HttpServletResponse response) {
        try {
            String modelName = "modelName";
            String modelKey = "modelKey";
            String description = "description";


            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.putPOJO("stencilset", stencilSetNode);
            Model modelData = repositoryService.newModel();

            ObjectNode modelObjectNode = objectMapper.createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, modelName);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            modelData.setMetaInfo(modelObjectNode.toString());
            modelData.setName(modelName);
            modelData.setKey(modelKey);

            //保存模型
            repositoryService.saveModel(modelData);
            repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
            response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + modelData.getId());
        } catch (Exception e) {
        }
    }

    /**
     * 部署.
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/deploy/{id}", method = RequestMethod.GET)
    public String deploy(@PathVariable String id) {

        // 获取模型
        Model modelData = repositoryService.getModel(id);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());

        if (bytes == null) {
            return "模型数据为空，请先成功设计流程并保存";
        }

        try {
            JsonNode modelNode = new ObjectMapper().readTree(bytes);
            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            if (model.getProcesses().size() == 0) {
                return "模型不符要求，请至少设计一条主线流程";
            }
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
            // 部署发布模型流程
            String processName = modelData.getName() + ".bpmn20.xml";
            Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).addString(processName, new String(bpmnBytes, "UTF-8")).deploy();

            // 设置流程分类 保存扩展流程至数据库
            List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();

            for (ProcessDefinition pd : list) {
                log.info(pd.getName());
            }
        } catch (Exception e) {
            log.error(e.toString());
            return "fail";
        }

        return "success";
    }

    @RequestMapping(value = "/getRunningProcess", method = RequestMethod.GET)
    @ApiOperation(value = "获取运行中的流程实例")
    public Object getRunningProcess(@RequestParam(required = false) String name, @RequestParam(required = false) String categoryId, @RequestParam(required = false) String key) {


        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().orderByProcessInstanceId().desc();

        if (StrUtil.isNotBlank(name)) {
            query.processInstanceNameLike("%" + name + "%");
        }
        if (StrUtil.isNotBlank(categoryId)) {
            query.processDefinitionCategory(categoryId);
        }
        if (StrUtil.isNotBlank(key)) {
            query.processDefinitionKey(key);
        }
        List<ProcessInstance> processInstanceList = query.listPage(1, 10);
        return processInstanceList;
    }

    /**
     * 激活流程定义.
     *
     * @param id procDefId  ACT_RE_PROCDEF.ID_
     * @return
     */
    @RequestMapping(value = "/active/{id}", method = RequestMethod.GET)
    public String active(@PathVariable String id) {
        repositoryService.activateProcessDefinitionById(id, true, new Date());
        return "激活成功";
    }

    /**
     * 启动流程实例
     * 数据在ACT_RU_TASK和ACT_RU_JOB表生成记录
     *
     * @param procDefId act_re_procdef.ID_
     * @param title
     * @return
     */
    @RequestMapping(value = "/instance-create/{procDefId}", method = RequestMethod.GET)
    public String createInstance(@PathVariable String procDefId, @RequestParam String title) {
        // 启动流程
        ProcessInstance pi = runtimeService.startProcessInstanceById(procDefId);
        // 设置流程实例名称
        runtimeService.setProcessInstanceName(pi.getId(), title);
        return "create instance success";
    }

    /**
     * 通过流程定义id获取流程节点.
     *
     * @param id procDefId
     * @return
     */
    @RequestMapping(value = "/getProcessNode/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "通过流程定义id获取流程节点")
    public Object getProcessNode(@ApiParam("流程定义id") @PathVariable String id) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(id);
        List<Process> processes = bpmnModel.getProcesses();
        return processes;
    }

    /**
     * 第一个流程节点
     *
     * @param procDefId act_re_procdef.ID_
     * @return
     */
    @RequestMapping(value = "/getFirstNode/{procDefId}", method = RequestMethod.GET)
    @ApiOperation(value = "通过流程定义id获取第一个任务节点")
    public Object getFirstNode(@ApiParam("流程定义id") @PathVariable String procDefId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);
        List<Process> processes = bpmnModel.getProcesses();
        Collection<FlowElement> elements = processes.get(0).getFlowElements();
        // 流程开始节点
        StartEvent startEvent = null;
        for (FlowElement element : elements) {
            if (element instanceof StartEvent) {
                startEvent = (StartEvent) element;
                break;
            }
        }
        FlowElement e = null;
        // 判断开始后的流向节点
        SequenceFlow sequenceFlow = startEvent.getOutgoingFlows().get(0);
        for (FlowElement element : elements) {
            if (element.getId().equals(sequenceFlow.getTargetRef())) {
                if (element instanceof UserTask) {
                    return element;
                } else {
                    throw new IllegalArgumentException("流程设计错误，开始节点后只能是用户任务节点");
                }
            }
        }

        return null;
    }

    /**
     * 获取运行实例的下一个节点.
     *
     * @param procInstId ACT_RU_TASK.PROC_INST_ID_
     * @return
     */
    @RequestMapping(value = "/getNextNode/{procInstId}", method = RequestMethod.GET)
    @ApiOperation(value = "通过流程实例获取下一个节点")
    public Object getNextNode(@ApiParam("流程实例id") @PathVariable String procInstId) {
        // 当前执行节点id
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        String currActId = pi.getActivityId();
        ProcessDefinitionEntity dfe = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(pi.getProcessDefinitionId());
        // 获取所有节点
        List<ActivityImpl> activitiList = dfe.getActivities();
        // 判断出当前流程所处节点，根据路径获得下一个节点实例
        for (ActivityImpl activityImpl : activitiList) {
            if (activityImpl.getId().equals(currActId)) {
                // 获取下一个节点
                List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();

                PvmActivity pvmActivity = pvmTransitions.get(0).getDestination();
                String type = pvmActivity.getProperty("type").toString();
                if ("userTask".equals(type)) {
                    // 用户任务节点
                    return pvmActivity.getId();
                } else if ("endEvent".equals(type)) {
                    // 结束
                    return "结束节点";
                } else if ("exclusiveGateway".equals(type)) {
                    //网关
                    try {
                        TaskDefinition taskInfo = getNextTaskInfo(procInstId);
                        return taskInfo;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new IllegalArgumentException("流程设计错误，无法处理的节点");
                }
                break;
            }
        }
        return null;
    }

    public TaskDefinition getNextTaskInfo(String processInstanceId) throws Exception {

        ProcessDefinitionEntity processDefinitionEntity = null;

        String id = null;

        TaskDefinition task = null;

        //获取流程发布Id信息
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();

        processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(definitionId);

        ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        //当前流程节点Id信息
        String activitiId = execution.getActivityId();

        //获取流程所有节点信息
        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();

        //遍历所有节点信息
        for (ActivityImpl activityImpl : activitiList) {
            id = activityImpl.getId();
            if (activitiId.equals(id)) {
                //获取下一个节点信息
                task = nextTaskDefinition(activityImpl, activityImpl.getId(), null, processInstanceId);
                break;
            }
        }
        return task;
    }

    /**
     * 下一个任务节点信息,
     * <p>
     * 如果下一个节点为用户任务则直接返回,
     * <p>
     * 如果下一个节点为排他网关, 获取排他网关Id信息, 根据排他网关Id信息和execution获取流程实例排他网关Id为key的变量值,
     * 根据变量值分别执行排他网关后线路中的el表达式, 并找到el表达式通过的线路后的用户任务
     *
     * @param activityImpl      流程节点信息
     * @param activityId        当前流程节点Id信息
     * @param elString          排他网关顺序流线段判断条件
     * @param processInstanceId 流程实例Id信息
     * @return
     */
    private TaskDefinition nextTaskDefinition(ActivityImpl activityImpl, String activityId, String elString, String processInstanceId) {

        PvmActivity ac = null;

        Object s = null;

        // 如果遍历节点为用户任务并且节点不是当前节点信息
        if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            // 获取该节点下一个节点信息
            TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior()).getTaskDefinition();
            return taskDefinition;
        } else {
            // 获取节点所有流向线路信息
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            List<PvmTransition> outTransitionsTemp = null;
            for (PvmTransition tr : outTransitions) {
                ac = tr.getDestination(); // 获取线路的终点节点
                // 如果流向线路为排他网关
                if ("exclusiveGateway".equals(ac.getProperty("type"))) {
                    outTransitionsTemp = ac.getOutgoingTransitions();

                    // 如果网关路线判断条件为空信息
                    if (StringUtils.isEmpty(elString)) {
                        // 获取流程启动时设置的网关判断条件信息
                        elString = getGatewayCondition(ac.getId(), processInstanceId);
                    }

                    // 如果排他网关只有一条线路信息
                    if (outTransitionsTemp.size() == 1) {
                        return nextTaskDefinition((ActivityImpl) outTransitionsTemp.get(0).getDestination(), activityId, elString, processInstanceId);
                    } else if (outTransitionsTemp.size() > 1) { // 如果排他网关有多条线路信息
                        for (PvmTransition tr1 : outTransitionsTemp) {
                            s = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
                            // 判断el表达式是否成立
                            if (isCondition(ac.getId(), StringUtils.trim(s.toString()), elString)) {
                                return nextTaskDefinition((ActivityImpl) tr1.getDestination(), activityId, elString, processInstanceId);
                            }
                        }
                    }
                } else if ("userTask".equals(ac.getProperty("type"))) {
                    return ((UserTaskActivityBehavior) ((ActivityImpl) ac).getActivityBehavior()).getTaskDefinition();
                } else {
                }
            }
            return null;
        }
    }

    /**
     * 查询流程启动时设置排他网关判断条件信息
     *
     * @param gatewayId         排他网关Id信息, 流程启动时设置网关路线判断条件key为网关Id信息
     * @param processInstanceId 流程实例Id信息
     * @return
     */
    private String getGatewayCondition(String gatewayId, String processInstanceId) {
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).singleResult();
        Object object = runtimeService.getVariable(execution.getId(), gatewayId);
        return object == null ? "" : object.toString();
    }

    /**
     * 根据key和value判断el表达式是否通过信息
     *
     * @param key   el表达式key信息
     * @param el    el表达式信息
     * @param value el表达式传入值信息
     * @return
     */
    private boolean isCondition(String key, String el, String value) {
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        context.setVariable(key, factory.createValueExpression(value, String.class));
        ValueExpression e = factory.createValueExpression(context, el, boolean.class);
        return (Boolean) e.getValue(context);
    }

    @RequestMapping(value = "/getHighlightImg/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取高亮实时流程图")
    public void getHighlightImg(@ApiParam("流程实例id") @PathVariable String id, HttpServletResponse response) {

        InputStream inputStream = null;
        ProcessInstance pi = null;
        String picName = "";
        // 查询历史
        HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
        if (hpi != null && hpi.getEndTime() != null) {
            // 已经结束流程获取原图
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(hpi.getProcessDefinitionId()).singleResult();
            picName = pd.getDiagramResourceName();
            inputStream = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getDiagramResourceName());
        } else {
            pi = runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
            List<String> highLightedActivities = new ArrayList<String>();
            // 高亮任务节点
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(id).list();
            for (Task task : tasks) {
                highLightedActivities.add(task.getTaskDefinitionKey());
            }

            List<String> highLightedFlows = new ArrayList<String>();
            ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
            inputStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivities, highLightedFlows, properties.getActivityFontName(), properties.getLabelFontName(), properties.getLabelFontName(), null, 1.0);
            picName = pi.getName() + ".png";
        }
        OutputStream o = null;
        try {
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(picName, "UTF-8"));
            o = response.getOutputStream();
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                o.write(buf, 0, bytesRead);
                o.flush();
            }
            inputStream.close();
            o.close();
            response.flushBuffer();
        } catch (IOException e) {
            log.error(e.toString());
            throw new IllegalArgumentException("读取流程图片失败");
        }
    }

    /**
     * 流程实例的任务的审批.
     *
     * @param id         任务ID ACT_RU_TASK.ID_
     * @param procInstId 流程实例ID ACT_RU_TASK.PROC_INST_ID_
     * @param assignees  分配人
     * @param comment    备注
     * @return
     */
    @RequestMapping(value = "/pass/{procInstId}/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "任务节点审批通过")
    public Object pass(@ApiParam("流程实例id") @PathVariable String procInstId,
                       @ApiParam("任务id") @PathVariable String id,
                       @ApiParam("下个节点审批人") @RequestParam(required = false) String[] assignees,
                       @ApiParam("备注") @RequestParam(required = false) String comment,
                       @RequestParam(required = false) Integer day) {


        if (StrUtil.isBlank(comment)) {
            comment = "";
        }
        taskService.addComment(id, procInstId, comment);
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        Task task = taskService.createTaskQuery().taskId(id).singleResult();
        Map map = new HashMap();
        map.put("day", day);
        taskService.complete(id, map);

        //判读是否会签结束，如果结束则给下一个节点赋  审批人
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        Map<String, Object> variablesMap = taskService.getVariables(tasks.get(0).getId());
        Object signResult = variablesMap.get("SignResult");
        //说明这里是会签过来的，可以直接从历史记录中获取之前的审批人作为审批人
        if (signResult != null && StringUtils.isNotBlank(signResult.toString())) {
            //查询历史审批记录获取当前节点的历史审批人
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(procInstId).finished().orderByTaskCreateTime().desc().list();
            String assignss = "";
            for (HistoricTaskInstance value : list) {
                //说明找到最近的
                if ("undertaking_department".equals(value.getTaskDefinitionKey())) {
                    assignss = value.getAssignee();
                    break;
                }
            }
            if (StringUtils.isNotBlank(assignss)) {
                if (tasks.size() > 0) {
                    taskService.setAssignee(tasks.get(0).getId(), assignss);
                }
            }
        }
        return "success";
    }

    @RequestMapping(value = "/back/{procInstId}/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "任务节点审批驳回")
    public Object back(@ApiParam("流程实例id") @PathVariable String procInstId, @ApiParam("任务id") @PathVariable String id, @ApiParam("意见评论") @RequestParam(required = false) String comment) {

        if (StrUtil.isBlank(comment)) {
            comment = "";
        }
        taskService.addComment(id, procInstId, comment);
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        // 删除流程实例
        runtimeService.deleteProcessInstance(procInstId, "backed");
        return "success";
    }

    @RequestMapping(value = "/delete/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除任务")
    public Object delete(@ApiParam("任务id") @PathVariable String[] ids, @ApiParam("原因") @RequestParam(required = false) String reason) {

        if (StrUtil.isBlank(reason)) {
            reason = "";
        }
        for (String id : ids) {
            taskService.deleteTask(id, reason);
        }
        return "success";
    }

    @RequestMapping(value = "/deleteHistoric/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除任务历史")
    public Object deleteHistoric(@ApiParam("任务id") @PathVariable String[] ids) {

        for (String id : ids) {
            historyService.deleteHistoricTaskInstance(id);
        }
        return "success";
    }

    @RequestMapping(value = "/delInsByIds/{ids}", method = RequestMethod.GET)
    @ApiOperation(value = "通过id删除运行中的实例")
    public Object delInsByIds(@PathVariable String[] ids, @RequestParam(required = false) String reason) {

        if (StrUtil.isBlank(reason)) {
            reason = "";
        }
        for (String id : ids) {
            // 关联业务状态结束
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
            runtimeService.deleteProcessInstance(id, reason);
        }
        return "success";
    }

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Object modelist() {
        List<Model> list = processEngine.getRepositoryService().createModelQuery()
                .orderByCreateTime()
                .desc()
                .list();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Model item : list) {
            result.add(ImmutableMap.of(
                    "id", item.getId(),
                    "version", item.getVersion(),
                    "name", item.getName()
            ));
        }
        return list;

    }

    /**
     * 流程列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deployment/list", method = RequestMethod.GET)
    public Object deployment() {
        List<Deployment> list = processEngine.getRepositoryService().createDeploymentQuery()
                .orderByDeploymentId()
                .desc()
                .list();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Deployment item : list) {
            ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery()
                    .deploymentId(item.getId())
                    .singleResult();
            result.add(ImmutableMap.of(
                    "id", item.getId(),
                    "time", item.getDeploymentTime(),
                    "name", item.getName(),
                    "proDefId", processDefinition.getId()
            ));
        }
        return result;

    }

    /**
     * 当前流程实例列表
     *
     * @return
     */
    @RequestMapping(value = "/execution/list", method = RequestMethod.GET)
    public Object execution() {
        List<ProcessInstance> list =
                runtimeService.createProcessInstanceQuery()
                        .orderByProcessInstanceId()
                        .desc()
                        .list();
        List<Map<String, Object>> result = new ArrayList<>();

        for (ProcessInstance item : list) {
            log.info("execution.id={},proc_inst_id={},proc_def_id={},isSuspended={}", item.getId(), item.getProcessInstanceId(), item.getProcessDefinitionId(), item.isSuspended());
            Task task =
                    taskService.createTaskQuery()
                            .active()
                            .processInstanceId(item.getId())
                            .singleResult();
            result.add(ImmutableMap.of(
                    "id", item.getId(),
                    "proDefId", item.getProcessDefinitionId(),
                    "isSuspended", item.isSuspended(),
                    "taskId", task.getId(),
                    "taskName", task.getName()
            ));
        }
        return result;
    }


    /**
     * 当前任务列表
     *
     * @return
     */
    @RequestMapping(value = "/task/list", method = RequestMethod.GET)
    public Object tasks() {
        List<Task> list =
                taskService.createTaskQuery()
                        .orderByExecutionId()
                        .desc()
                        .list();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Task item : list) {
            log.info("task.id={},proc_inst_id={},proc_def_id={}", item.getId(), item.getProcessInstanceId(), item.getProcessDefinitionId());
            result.add(ImmutableMap.of(
                    "id", item.getId(),
                    "procInstId", item.getProcessInstanceId(),
                    "procDefId", item.getProcessDefinitionId()
            ));
        }

        return result;
    }

}
