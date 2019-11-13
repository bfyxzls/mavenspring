package com.lind.mavenspring.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("model")
public class TestController {
  static final String MODEL_ID = "modelId";
  static final String MODEL_NAME = "name";
  static final String MODEL_REVISION = "revision";
  static final String MODEL_DESCRIPTION = "description";
  @Autowired
  ObjectMapper objectMapper;

  /**
   * 建立页面，现时也保存.
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

      ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

      RepositoryService repositoryService = processEngine.getRepositoryService();

      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode editorNode = objectMapper.createObjectNode();
      editorNode.put("id", "canvas");
      editorNode.put("resourceId", "canvas");
      ObjectNode stencilSetNode = objectMapper.createObjectNode();
      stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
      editorNode.put("stencilset", stencilSetNode);
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
   * 部署
   *
   * @param id
   * @return
   */
  @RequestMapping(value = "/deploy/{id}", method = RequestMethod.GET)
  public String deploy(@PathVariable String id) {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

    RepositoryService repositoryService = processEngine.getRepositoryService();
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
      Deployment deployment = repositoryService.createDeployment()
          .name(modelData.getName())
          .addString(processName, new String(bpmnBytes, "UTF-8"))
          .deploy();

      // 设置流程分类 保存扩展流程至数据库
      List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();

      for (ProcessDefinition pd : list) {
        log.info(pd.getName());
      }
    } catch (Exception e) {
      log.error(e.toString());
      return "部署失败";
    }

    return "部署成功";
  }
}
