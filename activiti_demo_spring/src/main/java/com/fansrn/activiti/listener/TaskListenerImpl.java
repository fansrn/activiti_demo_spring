package com.fansrn.activiti.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * description
 *
 * @author fansrn
 * @date 17:29 2019/8/8
 */
@Slf4j
public class TaskListenerImpl implements TaskListener {

    private static final String NODE_ONE = "GroupApproval";
    private static final String NODE_TWO = "TeamApproval";
    static final String ASSIGNEE_ONE = "one";
    static final String ASSIGNEE_TWO = "two";


    @Override
    public void notify(DelegateTask delegateTask) {
        String name = delegateTask.getName();
        if (name.equals(NODE_ONE)) {
            log.info("当前节点： {}", NODE_ONE);
            delegateTask.setAssignee(ASSIGNEE_ONE);
        } else if (name.equals(NODE_TWO)) {
            log.info("当前节点： {}", NODE_TWO);
            delegateTask.setAssignee(ASSIGNEE_TWO);
        }
    }
}
