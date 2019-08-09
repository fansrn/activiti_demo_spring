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

import java.util.List;

/**
 * description 测试监听器方式赋值
 *
 * @author fansrn
 * @date 16:36 2019/8/8
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring-config.xml"
})
@Slf4j
public class AssigneeTest2 {

    static final String PROCESS_KEY = "assigneeListener";
    static final String ASSIGNEE_ONE = "one";
    static final String ASSIGNEE_TWO = "two";
    static final String PROCESS_INSTANCE_ID = "75001";

    /**
     * description 部署
     *
     * @author fansrn
     * @date 16:37 2019/8/8
     */
    @Test
    public void deploy() {
        Deployment deployment = ActivitiUtils.deployFromClasspath("测试代理人表达式设置_listener", "approval_assignee3");
        if (null != deployment) {
            log.info("部署ID: {}", deployment.getId());
            log.info("部署名称: {}", deployment.getName());
        }
        /*
         * 2019-08-09 09:48:01.292 [main] INFO  AssigneeTest2 - 部署ID: 72501
         * 2019-08-09 09:48:01.292 [main] INFO  AssigneeTest2 - 部署名称: 测试代理人表达式设置_listener
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
        ProcessInstance pi = ActivitiUtils.startProcess(PROCESS_KEY);
        if (null != pi) {
            log.info("流程实例ID: {}", pi.getId());//流程实例ID
            log.info("流程定义ID: {}", pi.getProcessDefinitionId());//流程定义ID
        }
        /*
         * 2019-08-09 09:49:03.046 [main] INFO  AssigneeTest2 - 流程实例ID: 75001
         * 2019-08-09 09:49:03.046 [main] INFO  AssigneeTest2 - 流程定义ID: assigneeListener:1:72504
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

        /**
         * ver 1
         * 2019-08-09 09:52:24.380 [main] INFO  AssigneeTest2 - ********************** group tasks **********************
         * 2019-08-09 09:52:24.442 [main] INFO  AssigneeTest2 - 任务ID： 75004
         * 2019-08-09 09:52:24.443 [main] INFO  AssigneeTest2 - 任务名称： GroupApproval
         * 2019-08-09 09:52:24.443 [main] INFO  AssigneeTest2 - 任务的创建时间： Fri Aug 09 09:49:02 CST 2019
         * 2019-08-09 09:52:24.443 [main] INFO  AssigneeTest2 - 任务的办理人： one
         * 2019-08-09 09:52:24.443 [main] INFO  AssigneeTest2 - 流程实例ID：75001
         * 2019-08-09 09:52:24.443 [main] INFO  AssigneeTest2 - 执行对象ID： 75001
         * 2019-08-09 09:52:24.443 [main] INFO  AssigneeTest2 - 流程定义ID： assigneeListener:1:72504
         * 2019-08-09 09:52:24.443 [main] INFO  AssigneeTest2 - **********************************************************
         * 2019-08-09 09:52:24.447 [main] INFO  AssigneeTest2 - ********************** team tasks **********************
         *
         * ver 2
         * 2019-08-09 09:55:10.638 [main] INFO  AssigneeTest2 - ********************** group tasks **********************
         * 2019-08-09 09:55:10.658 [main] INFO  AssigneeTest2 - ********************** team tasks **********************
         * 2019-08-09 09:55:10.658 [main] INFO  AssigneeTest2 - 任务ID： 77504
         * 2019-08-09 09:55:10.658 [main] INFO  AssigneeTest2 - 任务名称： TeamApproval
         * 2019-08-09 09:55:10.658 [main] INFO  AssigneeTest2 - 任务的创建时间： Fri Aug 09 09:53:26 CST 2019
         * 2019-08-09 09:55:10.659 [main] INFO  AssigneeTest2 - 任务的办理人： two
         * 2019-08-09 09:55:10.659 [main] INFO  AssigneeTest2 - 流程实例ID：75001
         * 2019-08-09 09:55:10.659 [main] INFO  AssigneeTest2 - 执行对象ID： 75001
         * 2019-08-09 09:55:10.659 [main] INFO  AssigneeTest2 - 流程定义ID： assigneeListener:1:72504
         * 2019-08-09 09:55:10.659 [main] INFO  AssigneeTest2 - **********************************************************
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
        Task task = ActivitiUtils.findTask(PROCESS_INSTANCE_ID, ASSIGNEE_ONE);
        if (ActivitiUtils.completeTaskById(task.getId())) {
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
        Task task = ActivitiUtils.findTask(PROCESS_INSTANCE_ID, ASSIGNEE_TWO);
        if (ActivitiUtils.completeTaskById(task.getId())) {
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
        ActivitiUtils.delCascade("65001");
    }
}
