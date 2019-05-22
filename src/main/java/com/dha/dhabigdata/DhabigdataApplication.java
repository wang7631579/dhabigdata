package com.dha.dhabigdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//开启fifter servlet  interceptor
@ServletComponentScan
//开启异步注解
@EnableAsync
public class DhabigdataApplication {

	public static void main(String[] args) {
		SpringApplication.run(DhabigdataApplication.class, args);
	}

}
