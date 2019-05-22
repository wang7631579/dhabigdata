package com.dha.dhabigdata.config;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.dha.dhabigdata.hadoop.HadoopConfig;

@org.springframework.context.annotation.Configuration
public class DhaHadoopConfig {
	/**
	 * Spring boot 集成 Hdfs 注意事项 
	 * 1. pom 中需要追加 
	 * <dependency>
	 * 		<groupId>org.slf4j</groupId>
	 * 		<artifactId>slf4j-log4j12</artifactId>
	 * </dependency> 
	 * 2. 配置hadoop的文件 hadoopConfig.xml---->变更为读取application.properties文件
	 * 3. 当前机器还需要安装hadoop-common-2.2.0-bin-master 我这里使用的是2.2 如果需要其他版本 请自行替换 
	 *    ---->变更为D:\hadoop-common-2.7.5-bin-master\bin
	 * 4. 然后通过ApplicationContext 获取当前的注入对象
	 * 	  ---->通过Configration 注解注入对象	 * 
	 * 5. 还要设置环境变量 hadoop.home.dir 我这里有用的是代码设置的
	 *    （window 下配置 参照https://blog.csdn.net/a2099948768/article/details/79577246）
	 * 6. 还需要吧hadoop-common-2.7.5-bin-master\bin中的hadoop.dll 放到C:\Windows\System32 下
	 * 7. 
	 * 
	 * 
	 * 
	 */
	 private static String HADOOP_HOME_DIR = "D:\\hadoop-common-2.7.5-bin-master";
	/*
	 * @Autowired private ApplicationContext ctx; private static String
	 * HADOOP_HOME_DIR = "D:\\hadoop-common-2.2.0-bin-master"; private static String
	 * HADOOP_CONFIG_FILENAME = "hadoopConfig.xml";
	 * 
	 * @Bean public FileSystem dhaFileSystem() { // hadoop.home.dir 需要配置你的环境变量
	 * System.setProperty("hadoop.home.dir", HADOOP_HOME_DIR); ctx = new
	 * ClassPathXmlApplicationContext(HADOOP_CONFIG_FILENAME); FileSystem fileSystem
	 * = (FileSystem) ctx.getBean("fileSystem"); return fileSystem; }
	 */
	@Autowired
	HadoopConfig hadoopConfig;

	@Bean
	@Primary
	public Configuration dhaHadoopConfiguration() {
		// 需要配置当前的环境变量 如果没有配置需要配置
		System.setProperty("hadoop.home.dir", HADOOP_HOME_DIR);		
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", hadoopConfig.getHadoopHdfsUrl());
		conf.set("user", hadoopConfig.getHadoopUser());
		conf.setBoolean("fs.hdfs.impl.disable.cache", true);
		return conf;
	}
	
	@Bean("hbaseConfiguration")
	public Configuration hbaseConfiguration() {
		System.setProperty("hadoop.home.dir", HADOOP_HOME_DIR);
		Configuration conf = HBaseConfiguration.create();
		// 指定ZK 的集群
		conf.set("hbase.zookeeper.quorum", hadoopConfig.getHbaseZookeeperQuorum());
		// 指定ZK 的端口
		conf.set("hbase.zookeeper.property.clientPort", hadoopConfig.getHbaseookeeperPropertyClientPort());
		// 指定Hbase 的Master
		conf.set("hbase.master", hadoopConfig.getHbaseMaster());
		return conf;
	}

	@Bean
	//@Scope("prototype")
	public Connection hbaseConnection(@Qualifier("hbaseConfiguration") Configuration hbaseConfiguration) throws IOException {
		Connection connection = ConnectionFactory.createConnection(hbaseConfiguration);
		return connection;

	}
}
