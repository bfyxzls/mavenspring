import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

/**
 * 完善的工作流的流程.
 */
public class ActivitiDemoTest {

  /**
   * 初始化数据库语句.
   * @throws Exception
   */
  @Test
  public void initDb() throws Exception {
    ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
    System.out.println(processEngine);
  }
}
