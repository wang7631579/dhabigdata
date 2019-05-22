package com.dha.dhabigdata.hadoop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = "hadoop")
public class HadoopConfig {
	
	private String hadoopHdfsUrl;
	private String hadoopUser;
	private String hbaseZookeeperQuorum;
	private String hbaseookeeperPropertyClientPort;
	private String hbaseMaster;
	
}
