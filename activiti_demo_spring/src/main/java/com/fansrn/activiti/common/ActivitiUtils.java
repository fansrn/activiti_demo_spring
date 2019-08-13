package com.fansrn.activiti.common;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * description
 *
 * @author fansrn
 * @date 17:13 2019/8/6
 */
@Slf4j
public class ActivitiUtils {

    private static boolean PLUGIN_START = false;

    /*
     * private static final String MYSQL = "mysql";
     * private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
     * private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/activiti_demo?useUnicode=true&characterEncoding=UTF-8";
     * private static final String JDBC_USERNAME = "root";
     * private static final String JDBC_PASSWORD = "123456";
     */

    private static final String PATH = "bpmn/";
    private static final String FIX_BPMN = ".bpmn";
    private static final String FIX_PNG = ".png";
    private static final String FIX_ZIP = ".zip";

    public static ProcessEngine getProcessEngine() {
        return ProcessEngines.getDefaultProcessEngine();
    }

    /**
     * 获取核心 Service
     */
    private static RepositoryService getRepositoryService() {
        return getProcessEngine().getRepositoryService();
    }

    private static RuntimeService getRuntimeService() {
        return getProcessEngine().getRuntimeService();
    }

    private static TaskService getTaskService() {
        return getProcessEngine().getTaskService();
    }

    /*
      public static FormService getFormService() {
        return getProcessEngine().getFormService();
      }

      public static HistoryService getHistoryService() {
        return getProcessEngine().getHistoryService();
      }

      public static IdentityService getIdentityService() {
        return getProcessEngine().getIdentityService();
      }

      public static ManagementService getManagementService() {
        return getProcessEngine().getManagementService();
      }
     */

    /**
     * description 初始化Activiti数据库环境
     *
     * @author fansrn
     * @date 17:28 2019/8/6
     */
    public static void initActivitiDatabase() {
        if (PLUGIN_START) {
            return;
        }
        /*
          //创建流程引擎配置信息对象
          ProcessEngineConfiguration pec = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
                  // 设置数据库的类型
                  .setDatabaseType(MYSQL)
                  // 设置数据库驱动
                  .setJdbcDriver(MYSQL_DRIVER)
                  // 设置jdbcURL
                  .setJdbcUrl(JDBC_URL)
                  // 设置用户名
                  .setJdbcUsername(JDBC_USERNAME)
                  // 设置密码
                  .setJdbcPassword(JDBC_PASSWORD)
                  // 使用托管事务工厂
                  .setTransactionsExternallyManaged(true)

                  // 设置创建数据库的方式：
                  // ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE(true);                如果没有数据库表就会创建数据库表，有的话就修改表结构.
                  // ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE(false);              不会创建数据库表
                  // ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP(create-drop);  先创建、再删除.

                  .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
          */

        //从配置文件加载流程引擎对象
        ProcessEngineConfiguration pec = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("spring-config.xml");
        // 构建流程引擎对象，调用访方法才会创建数据表
        ProcessEngine pe = pec.buildProcessEngine();
        PLUGIN_START = true;
        log.info("start activiti plugin ... ");
        // 调用close方法时，才会删除
        pe.close();
    }

    /**
     * description 直接部署---classpath
     *
     * @param flowName name of workflow
     * @param fileName fileName of bpmn and png
     * @return Deployment
     * @author fansrn
     * @date 14:04 2019/8/7
     */
    public static Deployment deployFromClasspath(String flowName, String fileName) {
        try {
            //RepositoryService：与流程定义和部署对象相关的Service
            return getRepositoryService()
                    //创建一个部署对象
                    .createDeployment()
                    //添加部署的名称
                    .name(flowName)
                    //从classpath的资源中加载，一次只能加载一个文件
                    .addClasspathResource(PATH + fileName + FIX_BPMN)
                    //从classpath的资源中加载，一次只能加载一个文件
                    .addClasspathResource(PATH + fileName + FIX_PNG)
                    //完成部署
                    .deploy();
        } catch (Exception e) {
            log.error("ActivitiUtils: deploy from classpath error", e);
        }
        return null;
    }

    /**
     * description 通过zip文件部署
     *
     * @param name     name of workflow
     * @param fileName name of file
     * @return Deployment
     * @author fansrn
     * @date 14:03 2019/8/7
     */
    public static Deployment deployFromZip(String name, String fileName) {
        try {
            InputStream ins = ClassLoaderUtil.getClassLoader().getResourceAsStream(PATH + fileName + FIX_ZIP);
            if (ins != null) {
                ZipInputStream zipInputStream = new ZipInputStream(ins);
                return getRepositoryService()
                        .createDeployment()
                        .name(name)
                        //指定zip格式的文件完成部署
                        .addZipInputStream(zipInputStream)
                        .deploy();
            }
            return null;
        } catch (Exception e) {
            log.error("ActivitiUtils: deploy from zip error", e);
        }
        return null;
    }

    /**
     * description 根据流程定义key值查询最新的流程定义信息
     *
     * @param processDefinitionKey 流程bpme文件的id
     * @return ProcessDefinition
     * @author fansrn
     * @date 11:20 2019/8/8
     */
    public static ProcessDefinition findLastestProcess(String processDefinitionKey) {
        List<ProcessDefinition> processDefinitions = getRepositoryService()
                // 流程定义查询
                .createProcessDefinitionQuery()
                // 查询条件
                .processDefinitionKey(processDefinitionKey)
                //流程定义版本升序排列
                .orderByProcessDefinitionVersion().desc()
                .list();
        return CollectionUtil.isNotEmpty(processDefinitions) ? processDefinitions.get(0) : null;
    }

    /**
     * description 非级联删除：只能用于流程未启动时
     *
     * @param deploymentId 流程部署id
     * @author fansrn
     * @date 14:34 2019/8/8
     */
    public static void del(String deploymentId) {
        delCascade(deploymentId, false);
    }

    /**
     * description 级联删除：可用于流程为启动以及流程启动时
     *
     * @param deploymentId 流程部署id
     * @author fansrn
     * @date 14:35 2019/8/8
     */
    public static void delCascade(String deploymentId) {
        delCascade(deploymentId, true);
    }

    /**
     * description 流程删除
     *
     * @param deploymentId 流程定义（部署）id
     * @param cascade      是否级联
     * @author fansrn
     * @date 13:59 2019/8/7
     */
    private static void delCascade(String deploymentId, boolean cascade) {
        getRepositoryService().deleteDeployment(deploymentId, cascade);
    }

    /**
     * description 不携带流程变量启动
     *
     * @param processDefinitionKey 流程定义的key，bpmn的id
     * @return ProcessInstance
     * @author fansrn
     * @date 14:35 2019/8/8
     */
    public static ProcessInstance startProcess(String processDefinitionKey) {
        Map<String, Object> map = new HashMap<>();
        return startProcess(processDefinitionKey, map);
    }

    /**
     * description 启动流程
     *
     * @param processDefinitionKey 流程定义的key，通过这个key来启动流程实例
     * @param variables            其他参数，如流程变量等
     * @return ProcessInstance
     * @author fansrn
     * @date 14:17 2019/8/7
     */
    public static ProcessInstance startProcess(String processDefinitionKey, Map<String, Object> variables) {
        try {
            return getRuntimeService()
                    /*
                     * 流程可通过以下几种方式启动，我们使用key来启动，使用key值启动，默认是按照最新版本的流程定义启动
                     * .startProcessInstanceById
                     * .startProcessInstanceByKeyAndTenantId
                     * .startProcessInstanceByMessage
                     * .startProcessInstanceByMessageAndTenantId
                     */
                    // startProcessInstanceByKey方法还可以设置其他的参数，比如流程变量。
                    .startProcessInstanceByKey(processDefinitionKey, variables);
        } catch (Exception e) {
            log.error("ActivitiUtils: start process error", e);
        }
        return null;
    }

    /**
     * description 根据实例id、代理人获取任务
     *
     * @param processInstanceId 流程实例id
     * @param assignee          代理人
     * @return task
     * @author fansrn
     * @date 14:49 2019/8/8
     */
    public static Task findTask(String processInstanceId, String assignee) {
        return getTaskService()
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskAssignee(assignee)
                .singleResult();
    }

    /**
     * description 查询个人任务
     *
     * @param assignee 代理人
     * @return list of tasks
     * @author fansrn
     * @date 15:43 2019/8/7
     */
    public static List<Task> findTasks(String assignee) {
        try {
            //与正在执行的任务管理相关的Service
            return getTaskService()
                    //创建任务查询对象
                    .createTaskQuery()
                    /* 查询条件（where部分）*/
                    //指定个人任务查询，指定办理人
                    .taskAssignee(assignee)
//                      .taskCandidateUser(candidateUser)//组任务的办理人查询
//                      .processDefinitionId(processDefinitionId)//使用流程定义ID查询
//                      .processInstanceId(processInstanceId)//使用流程实例ID查询
//                      .executionId(executionId)//使用执行对象ID查询
                    /* 排序*/
                    .orderByTaskCreateTime().asc()//使用创建时间的升序排列
                    /* 返回结果集*/
//                      .singleResult()//返回惟一结果集
//                      .count()//返回结果集的数量
//                      .listPage(firstResult, maxResults);//分页查询
                    .list();//返回列表
        } catch (Exception e) {
            log.error("ActivitiUtils: query personal tasks error", e);
        }
        return null;
    }

    /**
     * description 完成任务
     *
     * @param taskId 任务id
     * @return result
     * @author fansrn
     * @date 14:36 2019/8/8
     */
    public static boolean completeTaskById(String taskId) {
        return completeTaskById(taskId, new HashMap<>());
    }

    /**
     * description 完成任务
     *
     * @param taskId    任务id
     * @param variables 额外参数
     * @return true 成功 ; false 失败
     * @author fansrn
     * @date 16:12 2019/8/7
     */
    private static boolean completeTaskById(String taskId, Map<String, Object> variables) {
        try {
            getTaskService().complete(taskId, variables);
            return true;
        } catch (Exception e) {
            log.error("ActivitiUtils: complete task error", e);
        }
        return false;
    }

    /**
     * description 完成任务
     *
     * @param task      流程实例id
     * @param variables 额外参数
     * @return result
     * @author fansrn
     * @date 14:58 2019/8/8
     */
    public static boolean completeTaskByAssignee(Task task, Map<String, Object> variables) {
        try {
            getTaskService().complete(task.getId(), variables);
            return true;
        } catch (Exception e) {
            log.error("ActivitiUtils: complete task by assignee error", e);
        }
        return false;
    }

    /**
     * description 查询流程状态
     *
     * @param processInstanceId 流程实例id
     * @return id of node
     * @author fansrn
     * @date 11:57 2019/8/8
     */
    public static String isProcessActive(String processInstanceId) {
        //表示正在执行的流程实例和执行对象
        ProcessInstance pi = getRuntimeService()
                //创建流程实例查询
                .createProcessInstanceQuery()
                //使用流程实例ID查询
                .processInstanceId(processInstanceId)
                .singleResult();
        if (null != pi) {
            return pi.getActivityId();
        } else {
            return null;
        }
    }
}
