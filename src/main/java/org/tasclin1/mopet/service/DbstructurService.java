package org.tasclin1.mopet.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DbstructurService {
    SimpleJdbcTemplate simpleJdbc;

    public void setSimpleJdbc(SimpleJdbcTemplate simpleJdbc) {
	this.simpleJdbc = simpleJdbc;
    }

    @Autowired
    @Qualifier("mopetService")
    private MopetService mopetService;
    List<DbUpdate> dbUpdateL;

    public List<DbUpdate> getDbUpdateL() {
	return dbUpdateL;
    }

    public void setDbUpdateL(List<DbUpdate> dbUpdateL) {
	this.dbUpdateL = dbUpdateL;
    }

    protected final Log log = LogFactory.getLog(getClass());

    @Transactional
    public void setFlushModeCommit() {
	em.setFlushMode(FlushModeType.COMMIT);
    }

    public void init() {
	System.out.println("------init--db--structure--------" + name);
	System.out.println("------init--db--structure--------" + mopetService.nextDbid());
	String sql = " SELECT version FROM oncows_db_version " + " WHERE type='structure' ORDER BY version DESC ";
	log.debug(sql);
	Map<String, Object> map = simpleJdbc.queryForList(sql).get(0);
	Timestamp lastUpdate = (Timestamp) map.get("version");
	log.debug(lastUpdate);
	// setFlushModeCommit();
	// if (true)
	// return;
	for (DbUpdate dbUpdate : dbUpdateL) {
	    if (dbUpdate.getDbUpdateDate().after(lastUpdate)) {
		log.debug(dbUpdate.getDbUpdateDate());
		dbUpdate.setSimpleJdbc(simpleJdbc);
		dbUpdate.update();
	    }
	}
    }

    private EntityManager em;

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
	this.em = em;
    }

    public DbstructurService() {
	System.out.println("------DbstructurService--------");
    }

    String name;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

}
