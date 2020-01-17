package com.lind.mavenspringcore;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;

/**
 * 工作流操作类，https://blog.csdn.net/qq_41129811/article/details/84647328
 */
public class BaseBpmn {
  // 获取核心对象 服务大管家
  protected ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
  // 获取运行的service服务
  protected RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
  // 获取TaskService
  protected TaskService taskService = defaultProcessEngine.getTaskService();

  /**
   * 部署文件
   *
   * @param resource
   * @param name
   */
  public void deploy(String resource, String name) {
    // 获取核心对象 服务大管家
    // 获取服务
    RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
    // 部署对象
    DeploymentBuilder createDeployment = repositoryService.createDeployment();
    // 上传资源
    InputStream inputStream = this.getClass().getResourceAsStream(resource + ".bpmn");
    InputStream inputStream2 = this.getClass().getResourceAsStream(resource + ".png");
    createDeployment.addInputStream(resource + ".bpmn", inputStream);
    createDeployment.addInputStream(resource + ".png", inputStream2);
    createDeployment.name(name);
    // 部署
    createDeployment.deploy();
  }

  /**
   * 启动实例带流程变量
   *
   * @param key
   * @param variables
   * @return
   */
  public ProcessInstance start(String key, Map<String, Object> variables) {
    //设置变量
    ProcessInstance startProcessInstanceByKey = runtimeService.startProcessInstanceByKey(key, variables);
    return startProcessInstanceByKey;
  }

  /**
   * 启动实例不带流程变量
   *
   * @param key
   * @param variables
   * @return
   */
  public ProcessInstance start(String key) {
    //设置变量
    ProcessInstance startProcessInstanceByKey = runtimeService.startProcessInstanceByKey(key);
    return startProcessInstanceByKey;
  }

  /**
   * 完成任务，带流程变量，变量流转到下一个节点
   *
   * @param taskId
   */
  public void compleTask(String taskId, Map<String, Object> variables) {
    taskService.complete(taskId, variables);
  }

  /**
   * 完成任务，不带带流程变量
   *
   * @param taskId
   */
  public void compleTask(String taskId) {
    taskService.complete(taskId);
  }

  /**
   * 完成任务，实例id 和人的名称查询信息
   *
   * @param id
   * @param name
   */
  public void compleTaskByPIdAndName(String id, String name) {
    //获取查询对象
    TaskQuery createTaskQuery = taskService.createTaskQuery();
    //获取任务id
    Task task = createTaskQuery.processInstanceId(id).taskAssignee(name).singleResult();
    //完成任务
    taskService.complete(task.getId());
  }

  /**
   * 完成任务，实例id 和人的名称 带参数查询信息
   *
   * @param id
   * @param name
   */
  public void compleTaskByPIdAndName(String id, String name, Map<String, Object> variables) {
    //获取查询对象
    TaskQuery createTaskQuery = taskService.createTaskQuery();
    //获取任务id
    Task task = createTaskQuery.processInstanceId(id).taskAssignee(name).singleResult();
    //完成任务
    taskService.complete(task.getId(), variables);
  }

  /**
   * 根据名称查询当前任务
   *
   * @param name
   * @return
   */
  public List<Task> queryTaskList(String name) {
    //获取查询对象
    TaskQuery createTaskQuery = taskService.createTaskQuery();
    //设置查询条件
    createTaskQuery.taskAssignee(name);
    //查询列表
    List<Task> list = createTaskQuery.list();
    return list;
  }

  /**
   * 根据名称查询当前任务
   *
   * @param name
   * @return
   */
  public Task queryTask(String name, String id) {
    //获取查询对象
    TaskQuery createTaskQuery = taskService.createTaskQuery();
    //设置查询条件
    createTaskQuery.taskAssignee(name);
    createTaskQuery.processInstanceId(id);
    //查询列表
    Task task = createTaskQuery.singleResult();
    return task;
  }

  /**
   * 查询当前指定对象（当前活动节点）
   *
   * @param id
   * @param actid
   * @return
   */
  public Execution queryExcution(String id, String actid) {
    //获取查询对象
    ExecutionQuery createExecutionQuery = runtimeService.createExecutionQuery();
    //设置查询条件
    Execution execution = createExecutionQuery.processInstanceId(id).activityId(actid).singleResult();
    return execution;
  }

  public void isEnd(String id) {
    //获取查询对象
    ProcessInstanceQuery createProcessInstanceQuery = runtimeService.createProcessInstanceQuery();
    //根据id查询
    ProcessInstance singleResult = createProcessInstanceQuery.processInstanceId(id).singleResult();
    //判断singleResult
    if (singleResult != null) {
      System.out.println("流程未结束或不存在");
    } else {
      System.out.println("流程结束");
    }

  }


}
