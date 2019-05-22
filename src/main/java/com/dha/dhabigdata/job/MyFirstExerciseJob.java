package com.dha.dhabigdata.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@DisallowConcurrentExecution
public class MyFirstExerciseJob implements Job {
	//http://www.cnblogs.com/nick-huang/p/8456272.html#my_inner_label3

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("MyFirstExerciseJob + myJobBusinessMethod");
		
	}

	
}
