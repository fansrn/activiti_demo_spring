import com.fansrn.activiti.common.ActivitiUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * description
 *
 * @author fansrn
 * @date 17:18 2019/7/25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring-config.xml"
})
@Slf4j
public class ActicitiTest {

    /**
     * description 初始化Activiti数据库初始环境
     *
     * @author fansrn
     * @date 17:29 2019/8/6
     */
    @Test
    public void initTableTest() {
        ActivitiUtils.initActivitiDatabase();
    }

    /**
     * description 部署流程实例
     *
     * @throws Exception
     * @author fansrn
     * @date 17:55 2019/7/25
     */
    @Test
    public void testTask() throws Exception {
        Deployment deployment = ActivitiUtils.deployFromClasspath("流程定义test2", "test_01");
        if (null != deployment) {
            log.info("部署ID: {}", deployment.getId());
            log.info("部署名称: {}", deployment.getName());
        }
    }

}
