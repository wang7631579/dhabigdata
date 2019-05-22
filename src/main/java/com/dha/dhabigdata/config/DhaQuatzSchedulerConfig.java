package com.dha.dhabigdata.config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.dha.dhabigdata.job.MyFirstExerciseJob;
import com.dha.dhabigdata.job.MySecondExerciseJob;

@Configuration
public class DhaQuatzSchedulerConfig {
	
	 @Autowired
	 private DataSource dataSource;  
	//http://www.cnblogs.com/nick-huang/p/8456272.html#my_inner_label7
	 
	@Bean(name = "myFirstExerciseJobBean")
	public JobDetailFactoryBean myFirstExerciseJobBean() {
		JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
		jobDetail.setName("first-job"); // 任务的名字
		jobDetail.setGroup("group1"); // 任务的分组
		jobDetail.setJobClass(MyFirstExerciseJob.class);
		jobDetail.setDurability(true);
		return jobDetail;
	}

	@Bean(name = "myFirstExerciseJobTrigger")
	public CronTriggerFactoryBean myFirstExerciseJobTrigger(
			@Qualifier("myFirstExerciseJobBean") JobDetailFactoryBean myFirstExerciseJobBean) {
		CronTriggerFactoryBean tigger = new CronTriggerFactoryBean();
		tigger.setJobDetail(myFirstExerciseJobBean.getObject());
		tigger.setCronExpression("0/10 * * * * ?");
		tigger.setName("first-job-trigger");
		return tigger;
	}

	@Bean(name = "mySecondExerciseJobBean")
	public JobDetailFactoryBean mySecondExerciseJobBean() {
		JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
		jobDetail.setName("second-job"); // 任务的名字
		jobDetail.setGroup("group1"); // 任务的分组
		jobDetail.setJobClass(MySecondExerciseJob.class);
		jobDetail.setDurability(true);
		return jobDetail;
	}

	@Bean(name = "mySecondExerciseJobTrigger")
	public CronTriggerFactoryBean MySecondExerciseJobTrigger(
			@Qualifier("mySecondExerciseJobBean") JobDetailFactoryBean mySecondExerciseJobBean) {
		CronTriggerFactoryBean tigger = new CronTriggerFactoryBean();
		tigger.setJobDetail(mySecondExerciseJobBean.getObject());
		tigger.setCronExpression("0/10 * * * * ?");
		tigger.setName("second-job-trigger");
		return tigger;
	}
	    
   
    //调度器工厂Bean
    @Bean(name = "schedulerFactory")
    public SchedulerFactoryBean schedulerFactory(Trigger... triggers) {
    	SchedulerFactoryBean bean = new SchedulerFactoryBean();
        Properties p = new Properties();
        try {
           p.load(this.getClass().getClassLoader().getResourceAsStream("quartz.properties"));
        } catch (IOException e) {
           e.printStackTrace();
          // this.logger.error("加载quartz.properties失败", e);
           throw new Error(e);
        }
        bean.setQuartzProperties(p);
        bean.setDataSource(dataSource);
        // 覆盖已存在的任务
        bean.setOverwriteExistingJobs(true);
        // 延时启动定时任务，避免系统未完全启动却开始执行定时任务的情况
        bean.setStartupDelay(15);
        // 注册触发器
        bean.setTriggers(triggers);
        return bean;
    }
}
