package com.dha.dhabigdata.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hadoop.hive.HiveClientFactory;
import org.springframework.data.hadoop.hive.HiveClientFactoryBean;
import org.springframework.data.hadoop.hive.HiveTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSourceHiveJdbcTemplateConfig {

	@Bean(name = "hiveDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.hive")
	public DataSource hiveDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "hiveJdbcTemplate")
	public JdbcTemplate hiveJdbcTemplate(@Qualifier("hiveDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	/*
	@Bean(name = "hiveJdbcTemplate")
	public HiveTemplate hiveJdbcTemplate(@Qualifier("hiveDataSource") DataSource dataSource) throws Exception {
		HiveClientFactoryBean hiveClientFactoryBean = new HiveClientFactoryBean();
		hiveClientFactoryBean.setHiveDataSource(dataSource);
		hiveClientFactoryBean.afterPropertiesSet();
		HiveClientFactory factory = hiveClientFactoryBean.getObject();
		HiveTemplate hiveTemplate = new HiveTemplate(factory);
		return hiveTemplate;
	}
	 */
}
