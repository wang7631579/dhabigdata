package com.dha.dhabigdata.hadoop.util;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.alibaba.druid.util.StringUtils;

/**
 * 创建Hive 工具类
 * 
 * @author wangpz
 *
 */
@Repository
public class DHaHiveRepository {

	@Autowired
	private JdbcTemplate hiveJdbcTemplate;

	/**
	 * 此方法直接执行命令如 创建database create database hive_jdbc_test
	 * @param command
	 */
	public void executeHiveCommand(String command) {
		hiveJdbcTemplate.execute(command);
	}

	/**
	 * 在指定的  database 下进行查询
	 * @param dbName
	 * @param sql
	 * @return
	 */
	public List<Map<String, Object>> queryForListMap(String dbName, String sql) {
		if (StringUtils.isEmpty(dbName)) {
			dbName = "default";
		}
		List<Map<String, Object>> rows;
		try {
			hiveJdbcTemplate.execute("use " + dbName);
			rows = hiveJdbcTemplate.queryForList(sql);
		} catch (DataAccessException e) {
			e.printStackTrace();
			rows = null;
		}
		return rows;
	}
	
	/**
	 * 在指定的  database 下进行查询
	 * @param dbName
	 * @param sql
	 * @param params sql的参数 
	 * @return
	 */
	public List<Map<String, Object>> queryForListMap(String dbName, String sql,Object[] params) {
		if (StringUtils.isEmpty(dbName)) {
			dbName = "default";
		}
		List<Map<String, Object>> rows;
		try {
			hiveJdbcTemplate.execute("use " + dbName);
			rows = hiveJdbcTemplate.queryForList(sql,params);
		} catch (DataAccessException e) {
			e.printStackTrace();
			rows = null;
		}
		return rows;
	}

}
