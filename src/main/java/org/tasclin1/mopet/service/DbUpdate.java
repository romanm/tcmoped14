package org.tasclin1.mopet.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class DbUpdate {
    protected final Log log = LogFactory.getLog(getClass());

    void update() {

	StringBuffer sqls = new StringBuffer().append("\n-- DB UPDATE BEGIN \n");
	sqls.append("-- ").append(getDbUpdateDate()).append("\n");

	boolean isToUpdate = true;
	if (getIfNotDbObj() != null) {
	    // sqls.append("-- IfNotDbObj \n").append(niceSql(getIfNotDbObj()));
	    // sqls.append("-- IfNotDbObj \n").append(getIfNotDbObj() + ";\n");
	    int size = simpleJdbc.queryForList(getIfNotDbObj()).size();
	    log.debug(size);
	    isToUpdate = size == 0;
	}
	log.debug(isToUpdate);

	if (isToUpdate)
	    updateDirect(sqls);
	String updateType = "structure";
	String sql2 = " INSERT INTO oncows_db_version (type,version) " + " VALUES ('" + updateType + "','"
		+ dbUpdateDate + "')";
	sqls.append(sql2);
	simpleJdbc.update(sql2);
	sqls.append("-- DB UPDATE END \n");
	log.info(sqls);
    }

    private void updateDirect(StringBuffer sqls) {
	for (String sql1 : getSqlL()) {
	    log.debug("----" + sql1);
	    update1sql(sql1, sqls);
	}
    }

    static Pattern sql2sql = Pattern.compile("select.*(insert|update|delete)", Pattern.CASE_INSENSITIVE);

    private void update1sql(String sql1, StringBuffer sqls) {
	sql1 = sql1.replaceAll("\\s+", " ");
	// if (null != variableInt1)
	// sql1 = sql1.replaceAll("variableInt1", "" + vi1);
	sqls.append(sql1 + ";\n");
	if (sql2sql.matcher(sql1).find()) {
	    List<Map<String, Object>> dd = simpleJdbc.queryForList(sql1);
	    for (Map<String, Object> row : dd) {
		for (String colName : row.keySet()) {
		    log.debug(colName);
		    String sql = (String) row.get(colName);
		    // log.debug(sql);
		    if (sql.contains("\"\"\""))
			sql = sql.replaceAll("\"\"\"", "'");
		    else
			sql = sql.replaceAll("\"", "'");
		    log.debug(sql);
		    // StringBuffer niceSql = niceSql(sql);
		    // sqls.append(niceSql);
		    // sqls.append(sql);
		    // simpleJdbc.update(niceSql.toString());
		    simpleJdbc.update(sql);
		}
	    }
	} else {
	    // StringBuffer niceSql = niceSql(sql1);
	    // sqls.append(niceSql);
	    // simpleJdbc.update(niceSql.toString());
	    simpleJdbc.update(sql1);
	}
    }

    String ifNotDbObj, ifYesDbObj;

    public String getIfNotDbObj() {
	return ifNotDbObj;
    }

    public void setIfNotDbObj(String ifNotDbObj) {
	this.ifNotDbObj = ifNotDbObj;
    }

    public String getIfYesDbObj() {
	return ifYesDbObj;
    }

    public void setIfYesDbObj(String ifYesDbObj) {
	this.ifYesDbObj = ifYesDbObj;
    }

    public void setDate(String date) throws ParseException {
	Date parse = df.parse(date);
	dbUpdateDate = new Timestamp(parse.getTime());
    }

    static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Timestamp dbUpdateDate;

    public Timestamp getDbUpdateDate() {
	return dbUpdateDate;
    }

    List<String> sqlL;
    private SimpleJdbcTemplate simpleJdbc;

    public List<String> getSqlL() {
	return sqlL;
    }

    public void setSqlL(List<String> sqlL) {
	this.sqlL = sqlL;
    }

    public void setSimpleJdbc(SimpleJdbcTemplate simpleJdbc) {
	this.simpleJdbc = simpleJdbc;
    }
}
