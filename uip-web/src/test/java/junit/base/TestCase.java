package junit.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Junit 测试基类，主要是为了加载配置文件
 * 使得继承此类的子类不需要再去加载配置文件
 * @author ShuiQing PM	
 * 2014年9月13日 下午5:22:42
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/springContext-*.xml", "classpath:springContext-common.xml"})
@WebAppConfiguration
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class TestCase{

    @PersistenceContext
    protected EntityManager entityManager;

    @Test
	public void test () {

    }
}
