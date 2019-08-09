import cn.hutool.core.collection.CollectionUtil;
import com.fansrn.activiti.common.ActivitiUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description 测试表达式方式赋值
 *
 * @author fansrn
 * @date 16:36 2019/8/8
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring-config.xml"
})
@Slf4j
public class AssigneeTest {

    static final String PROCESS_KEY = "assigneeApproval";
    static final String ASSIGNEE_ONE = "one";
    static final String ASSIGNEE_TWO = "two";
    static final String PROCESS_INSTANCE_ID = "50001";

    /**
     * description 部署
     *
     * @author fansrn
     * @date 16:37 2019/8/8
     */
    @Test
    public void deploy() {
        Deployment deployment = ActivitiUtils.deployFromClasspath("测试代理人表达式设置22", "approval_assignee2");
        if (null != deployment) {
            log.info("部署ID: {}", deployment.getId());
            log.info("部署名称: {}", deployment.getName());
        }
        /*
         * 2019-08-08 17:16:29.093 [main] INFO  AssigneeTest - 部署ID: 47501
         * 2019-08-08 17:16:29.093 [main] INFO  AssigneeTest - 部署名称: 测试代理人表达式设置
         */
    }

    /**
     * description 启动
     *
     * @author fansrn
     * @date 16:37 2019/8/8
     */
    @Test
    public void start() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("one", ASSIGNEE_ONE);
        ProcessInstance pi = ActivitiUtils.startProcess(PROCESS_KEY, variables);
        if (null != pi) {
            log.info("流程实例ID: {}", pi.getId());//流程实例ID
            log.info("流程定义ID: {}", pi.getProcessDefinitionId());//流程定义ID
        }
        /*
         * 2019-08-08 17:17:02.826 [main] INFO  AssigneeTest - 流程实例ID: 50001
         * 2019-08-08 17:17:02.826 [main] INFO  AssigneeTest - 流程定义ID: assigneeApproval:2:47504
         */
    }

    @Test
    public void findPersonalTask() {
        List<Task> groupTasks = ActivitiUtils.findTasks(ASSIGNEE_ONE);
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

        List<Task> teamTasks = ActivitiUtils.findTasks(ASSIGNEE_TWO);
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

        /*
         * ver 1
         * 2019-08-08 17:17:43.636 [main] INFO  AssigneeTest - ********************** group tasks **********************
         * 2019-08-08 17:17:43.646 [main] INFO  AssigneeTest - 任务ID： 50005
         * 2019-08-08 17:17:43.646 [main] INFO  AssigneeTest - 任务名称： GroupApproval
         * 2019-08-08 17:17:43.646 [main] INFO  AssigneeTest - 任务的创建时间： Thu Aug 08 17:17:02 CST 2019
         * 2019-08-08 17:17:43.646 [main] INFO  AssigneeTest - 任务的办理人： one
         * 2019-08-08 17:17:43.646 [main] INFO  AssigneeTest - 流程实例ID：50001
         * 2019-08-08 17:17:43.647 [main] INFO  AssigneeTest - 执行对象ID： 50001
         * 2019-08-08 17:17:43.647 [main] INFO  AssigneeTest - 流程定义ID： assigneeApproval:2:47504
         * 2019-08-08 17:17:43.647 [main] INFO  AssigneeTest - **********************************************************
         * 2019-08-08 17:17:43.652 [main] INFO  AssigneeTest - ********************** team tasks **********************
         *
         * ver 2
         * 2019-08-08 17:18:37.811 [main] INFO  AssigneeTest - ********************** group tasks **********************
         * 2019-08-08 17:18:37.839 [main] INFO  AssigneeTest - ********************** team tasks **********************
         * 2019-08-08 17:18:37.839 [main] INFO  AssigneeTest - 任务ID： 52503
         * 2019-08-08 17:18:37.839 [main] INFO  AssigneeTest - 任务名称： TeamApproval
         * 2019-08-08 17:18:37.839 [main] INFO  AssigneeTest - 任务的创建时间： Thu Aug 08 17:18:23 CST 2019
         * 2019-08-08 17:18:37.839 [main] INFO  AssigneeTest - 任务的办理人： two
         * 2019-08-08 17:18:37.839 [main] INFO  AssigneeTest - 流程实例ID：50001
         * 2019-08-08 17:18:37.839 [main] INFO  AssigneeTest - 执行对象ID： 50001
         * 2019-08-08 17:18:37.839 [main] INFO  AssigneeTest - 流程定义ID： assigneeApproval:2:47504
         * 2019-08-08 17:18:37.839 [main] INFO  AssigneeTest - **********************************************************
         */
    }

    /**
     * description 完成任务
     *
     * @author fansrn
     * @date 16:38 2019/8/8
     */
    @Test
    public void completeTaskOne() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("one", ASSIGNEE_ONE);
        variables.put("two", ASSIGNEE_TWO);
        Task task = ActivitiUtils.findTask(PROCESS_INSTANCE_ID, ASSIGNEE_ONE);
        if (ActivitiUtils.completeTaskByAssignee(task, variables)) {
            log.info("任务成功！");
        } else {
            log.info("任务失败!");
        }
    }

    /**
     * description 完成任务
     *
     * @author fansrn
     * @date 16:38 2019/8/8
     */
    @Test
    public void completeTaskTwo() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("one", ASSIGNEE_ONE);
        variables.put("two", ASSIGNEE_TWO);
        Task task = ActivitiUtils.findTask(PROCESS_INSTANCE_ID, ASSIGNEE_TWO);
        if (ActivitiUtils.completeTaskByAssignee(task, variables)) {
            log.info("任务成功！");
        } else {
            log.info("任务失败!");
        }
    }

    /**
     * description 查询流程状态
     *
     * @author fansrn
     * @date 16:29 2019/8/7
     */
    @Test
    public void processStatus() {
        String nodeId = ActivitiUtils.isProcessActive(PROCESS_INSTANCE_ID);
        if (StringUtils.isNotBlank(nodeId)) {
            log.info("ActivitiUtils: process is active, nodeId : {}", nodeId);
        } else {
            log.info("ActivitiUtils: process is finished");
        }
    }

    @Test
    public void del() {
        ActivitiUtils.delCascade(PROCESS_INSTANCE_ID);
    }
}
