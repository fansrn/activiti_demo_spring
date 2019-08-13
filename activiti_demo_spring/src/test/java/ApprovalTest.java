import cn.hutool.core.collection.CollectionUtil;
import com.fansrn.activiti.common.ActivitiUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * description
 *
 * @author fansrn
 * @date 17:12 2019/8/6
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring-config.xml"
})
@Slf4j
public class ApprovalTest {

    static final String PROCESS_KEY = "approval";
    static final String GROUP_LEADER = "GroupLeader";
    static final String TEAM_LEADER = "TeamLeader";

    @Test
    public void init() {
        ActivitiUtils.initActivitiDatabase();
    }

    /**
     * description 部署流程定义（使用classpathResource）
     *
     * @author fansrn
     * @date 11:34 2019/8/7
     */
    @Test
    public void deployFromClasspath() {
        Deployment deployment = ActivitiUtils.deployFromClasspath("流程定义classpath", "approval");
        if (null != deployment) {
            log.info("部署ID: {}", deployment.getId());
            log.info("部署名称: {}", deployment.getName());
        }
    }

    /**
     * description 部署流程定义（使用ZipInputStream）
     *
     * @author fansrn
     * @date 11:34 2019/8/7
     */
    @Test
    public void deployFromZip() {
        Deployment deployment = ActivitiUtils.deployFromZip("流程定义zip", "approval");
        if (null != deployment) {
            log.info("部署ID: {}", deployment.getId());
            log.info("部署名称: {}", deployment.getName());
        }
    }

    /**
     * description 查询流程定义
     *
     * @author fansrn
     * @date 11:34 2019/8/7
     */
    @Test
    public void findProcessDefinition() {
        List<ProcessDefinition> list = ActivitiUtils.getRepositoryService()//与流程定义和部署对象相关的Service
                .createProcessDefinitionQuery()//创建一个流程定义的查询
                /* 指定查询条件,where条件 */
//                .deploymentId(deploymentId)//使用部署对象ID查询
//                .processDefinitionId(processDefinitionId)//使用流程定义ID查询
//                .processDefinitionKey(processDefinitionKey)//使用流程定义的key查询
//                .processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询

                /* 排序：选定排序方式后，通过.asc()、.desc()控制升、降排序*/
                .orderByProcessDefinitionId().asc()//按照按照流程定义id升序排列
//                .orderByDeploymentId()//按照部署对象id排序
//                .orderByProcessDefinitionCategory()//按照流程定义类别排序
//                .orderByProcessDefinitionId()//按照流程定义id排序
//                .orderByProcessDefinitionKey()//按照流程定义key值排序
//                .orderByProcessDefinitionName()//按照流程定义名称排序
//                .orderByProcessDefinitionVersion()//按照流程定义版本排序
//                .orderByTenantId()
//                .orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列

                /* 返回的结果集 */
                .list();//返回一个集合列表，封装流程定义
//                .singleResult();//返回惟一结果集
//                .count();//返回结果集数量
//                .listPage(firstResult, maxResults);//分页查询

        if (CollectionUtil.isNotEmpty(list)) {
            for (ProcessDefinition pd : list) {
                log.info("流程定义ID: {}", pd.getId());//流程定义的key+版本+随机生成数
                log.info("流程定义的名称: {}", pd.getName());//对应approval.bpmn文件中的name属性值
                log.info("流程定义的key: {}", pd.getKey());//对应approval.bpmn文件中的id属性值
                log.info("流程定义的版本: {}", pd.getVersion());//当流程定义的key值相同的相同下，版本升级，默认1
                log.info("资源名称bpmn文件: {}", pd.getResourceName());
                log.info("资源名称png文件: {}", pd.getDiagramResourceName());
                log.info("部署对象ID: {}", pd.getDeploymentId());
                log.info("*********************************************");
            }
        }
    }

    /**
     * description 删除流程定义：此处删除test_01部署流程
     *
     * @author fansrn
     * @date 11:34 2019/8/7
     */
    @Test
    public void deleteProcessDefinition() {
        //非级联删除：只可在流程未启动时使用
        ActivitiUtils.del("12501");

        //级联删除：不管流程启动与否都可以使用
        ActivitiUtils.delCascade("15001");
        log.info("删除成功！");
    }

    /**
     * description 查看流程图：将生成图片放到文件夹下  --- 通过部署流程ID查询
     *
     * @throws IOException
     * @author fansrn
     * @date 11:34 2019/8/7
     */
    @Test
    public void viewPic() throws IOException {
        String deploymentId = "1";
        //获取图片资源名称
        List<String> list = ActivitiUtils.getRepositoryService().getDeploymentResourceNames(deploymentId);
        //定义图片资源的名称
        String resourceName = "";
        if (CollectionUtil.isNotEmpty(list)) {
            for (String name : list) {
                if (name.contains(".png")) {
                    resourceName = name;
                }
            }
        }
        //获取图片的输入流
        InputStream in = ActivitiUtils.getRepositoryService().getResourceAsStream(deploymentId, resourceName);

        //将图片生成到桌面
        File file = new File("C:/Users/Gentleman/Desktop/" + resourceName);

        //将输入流的图片写到磁盘
        FileUtils.copyInputStreamToFile(in, file);
    }

    /**
     * description 查询最新版本的流程定义
     *
     * @author fansrn
     * @date 11:44 2019/8/7
     */
    @Test
    public void findLastVersionProcessDefinition() {
        ProcessDefinition processDefinition = ActivitiUtils.findLastestProcess(PROCESS_KEY);
        if (Objects.nonNull(processDefinition)) {
            log.info("流程定义ID: {}", processDefinition.getId());//流程定义的key+版本+随机生成数
            log.info("流程定义的名称: {}", processDefinition.getName());//对应approval.bpmn文件中的name属性值
            log.info("流程定义的key: {}", processDefinition.getKey());//对应approval.bpmn文件中的id属性值
            log.info("流程定义的版本: {}", processDefinition.getVersion());//当流程定义的key值相同的相同下，版本升级，默认1
            log.info("资源名称bpmn文件: {}", processDefinition.getResourceName());
            log.info("资源名称png文件: {}", processDefinition.getDiagramResourceName());
            log.info("部署对象ID： {}", processDefinition.getDeploymentId());
            log.info("*********************************************************************************");
        }
    }

    /**
     * description 启动流程实例：此处我们通过流程定义key值启动，key值相同的情况下，默认启动最新版本
     *
     * @author fansrn
     * @date 14:14 2019/8/7
     */
    @Test
    public void startProcessInstance() {
        ProcessInstance pi = ActivitiUtils.startProcess(PROCESS_KEY);
        if (null != pi) {
            log.info("流程实例ID: {}", pi.getId());//流程实例ID
            log.info("流程定义ID: {}", pi.getProcessDefinitionId());//流程定义ID
        }
    }

    /**
     * description 查询个人任务：之前我们已经在 bpmn 中已设定 GroupLeader 和 TeamLeader 为相关人员，此处我们查询这两个人的个人任务
     *
     * @author fansrn
     * @date 15:43 2019/8/7
     */
    @Test
    public void findPersonalTask() {
        List<Task> groupTasks = ActivitiUtils.findTasks(GROUP_LEADER);
        log.info("********************** group tasks **********************");
        if (CollectionUtil.isNotEmpty(groupTasks)) {
            for (Task task : groupTasks) {
                log.info("任务ID： {}", task.getId());
                log.info("任务名称： {}", task.getName());
                log.info("任务的创建时间： {}", task.getCreateTime());
                log.info("任务的办理人： {}", task.getAssignee());
                log.info("流程实例ID：" + task.getProcessInstanceId());
                log.info("执行对象ID： {}", task.getExecutionId());
                log.info("流程定义ID： {}", task.getProcessDefinitionId());
                log.info("**********************************************************");
            }
        }

        List<Task> teamTasks = ActivitiUtils.findTasks(TEAM_LEADER);
        log.info("********************** team tasks **********************");
        if (CollectionUtil.isNotEmpty(teamTasks)) {
            for (Task task : teamTasks) {
                log.info("任务ID： {}", task.getId());
                log.info("任务名称： {}", task.getName());
                log.info("任务的创建时间： {}", task.getCreateTime());
                log.info("任务的办理人： {}", task.getAssignee());
                log.info("流程实例ID：" + task.getProcessInstanceId());
                log.info("执行对象ID： {}", task.getExecutionId());
                log.info("流程定义ID： {}", task.getProcessDefinitionId());
                log.info("**********************************************************");
            }
        }
    }

    /**
     * description 执行任务：根据上一步 {@link #findPersonalTask()} 查询出的 taskId（任务id）执行任务
     *
     * @author fansrn
     * @date 16:16 2019/8/7
     */
    @Test
    public void completeTask() {
        //由上一步查询得出
        String groupTaskId = "17504";
        String teamTaskId = "20002";
        if (ActivitiUtils.completeTaskById(groupTaskId)) {
            log.info("任务成功，任务id： {}", groupTaskId);
        } else {
            log.info("任务失败，任务id： {}", groupTaskId);
        }

//        String processInstanceId = "17501";
//        if (ActivitiUtils.completeTaskByAssignee(processInstanceId, GROUP_LEADER)) {
//            log.info("任务成功，流程实例id： {}，执行人： {}", groupTaskId, GROUP_LEADER);
//        } else {
//            log.info("任务失败，流程实例id： {}，执行人： {}", groupTaskId, GROUP_LEADER);
//        }
    }

    /**
     * description 查询流程状态
     *
     * @author fansrn
     * @date 16:29 2019/8/7
     */
    @Test
    public void processStatus() {
        String processInstanceId = "17501";
        String nodeId = ActivitiUtils.isProcessActive(processInstanceId);
        if (StringUtils.isNotBlank(nodeId)) {
            log.info("ActivitiUtils: process is active, nodeId : {}", nodeId);
        } else {
            log.info("ActivitiUtils: process is finished");
        }
    }

    /**
     * description 查询实例的历史活动：通过这个接口不仅仅查到这些信息，还有其他的方法，可以获取更多的关于历史活动的其他信息。
     *
     * @author fansrn
     * @date 16:52 2019/8/7
     */
    @Test
    public void findHistoryActivity() {
        String processInstanceId = "17501";
        List<HistoricActivityInstance> list = ActivitiUtils.getHistoryService()
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        if (CollectionUtil.isNotEmpty(list)) {
            for (HistoricActivityInstance historicActivityInstance : list) {
                log.info("活动id： {}", historicActivityInstance.getActivityId());
                log.info("审批人：{}", historicActivityInstance.getAssignee());
                log.info("任务id：{}", historicActivityInstance.getTaskId());
                log.info("**********************************************************");
            }
        }
    }

    /**
     * description 查询历史流程实例
     *
     * @author fansrn
     * @date 16:59 2019/8/7
     */
    @Test
    public void findHistoryProcessInstance() {
        String processInstanceId = "17501";
        // 与历史数据（历史表）相关的Service
        HistoricProcessInstance hpi = ActivitiUtils.getHistoryService()
                // 创建历史流程实例查询
                .createHistoricProcessInstanceQuery()
                // 使用流程实例ID查询
                .processInstanceId(processInstanceId)
                .orderByProcessInstanceStartTime().asc().singleResult();
        log.info("流程实例id： {}", hpi.getId());
        log.info("流程定义id： {}", hpi.getProcessDefinitionId());
        log.info("流程开始时间： {}", hpi.getStartTime());
        log.info("流程结束时间： {}", hpi.getEndTime());
        log.info("流程时长： {} s", hpi.getDurationInMillis());
    }

    /**
     * description 查询流程的历史任务
     *
     * @author fansrn
     * @date 17:01 2019/8/7
     */
    @Test
    public void findHistoryTask() {
        String processInstanceId = "17501";
        // 与历史数据（历史表）相关的Service
        List<HistoricTaskInstance> list = ActivitiUtils.getHistoryService()
                // 创建历史任务实例查询
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByTaskCreateTime().asc().list();
        if (CollectionUtil.isNotEmpty(list)) {
            for (HistoricTaskInstance hti : list) {
                log.info("流程任务id： {}", hti.getId());
                log.info("流程任务名称： {}", hti.getName());
                log.info("流程实例id： {}", hti.getProcessInstanceId());
                log.info("流程开始时间： {}", hti.getStartTime());
                log.info("流程结束时间： {}", hti.getEndTime());
                log.info("流程时长： {} s", hti.getDurationInMillis());
                log.info("**********************************************************");
            }
        }
    }

    /**
     * description 查询历史流程变量：在这个实例中没有设置流程变量，所以，这里是查询不到任何历史信息的。
     *
     * @author fansrn
     * @date 17:06 2019/8/7
     */
    @Test
    public void findHistoryProcessVariables() {
        String processInstanceId = "17501";
        List<HistoricVariableInstance> list = ActivitiUtils.getHistoryService()
                // 创建一个历史的流程变量查询对象
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        if (CollectionUtil.isNotEmpty(list)) {
            for (HistoricVariableInstance hvi : list) {
                log.info("流程参数id： {}", hvi.getId());
                log.info("流程实例id： {}", hvi.getProcessInstanceId());
                log.info("流程参数名称： {}", hvi.getVariableName());
                log.info("流程参数类型： {}", hvi.getVariableTypeName());
                log.info("流程参数值： {}", hvi.getValue());
            }
        }
    }

    /**
     * description 通过执行sql来查询历史数据，由于activiti底层就是数据库表
     * <p>
     * 这个接口是提供直接通过 sql 语句来查询历史信息的，我们只需要在 sql() 方法中写原生的 sql 语句就可以进行数据查询。
     *
     * @author fansrn
     * @date 17:07 2019/8/7
     */
    @Test
    public void findHistoryByNative() {
        HistoricProcessInstance hpi = ActivitiUtils.getHistoryService()
                .createNativeHistoricProcessInstanceQuery()
                .sql("select * from act_hi_procinst")
                .singleResult();
        log.info("流程实例id： {}", hpi.getId());
        log.info("流程定义id： {}", hpi.getProcessDefinitionId());
        log.info("流程开始时间： {}", hpi.getStartTime());
        log.info("流程结束时间： {}", hpi.getEndTime());
        log.info("流程时长： {} s", hpi.getDurationInMillis());
    }

}
