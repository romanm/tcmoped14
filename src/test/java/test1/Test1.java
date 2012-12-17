package test1;

import java.sql.Timestamp;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/data-access-config.xml")
@Transactional
public class Test1 extends AbstractTransactionalJUnit4SpringContextTests {
    protected final Log log = LogFactory.getLog(getClass());
    @Autowired
    private SimpleJdbcTemplate simpleJdbc;

    private EntityManager em;

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
	this.em = em;
    }

    @Test
    public void sampleTest() {
	System.out.println("Creating a new contact" + em);
	System.out.println("Creating a new contact");
	System.out.println("simpleJdbc = " + simpleJdbc);
	String sql = " SELECT version FROM oncows_db_version " + " WHERE type='structure' ORDER BY version DESC ";
	System.out.println("sql = " + sql);
	Map<String, Object> map = simpleJdbc.queryForList(sql).get(0);
	Timestamp lastUpdate = (Timestamp) map.get("version");
	System.out.println(lastUpdate);
    }
}
