package com.dha.dhabigdata.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class MySecondExerciseJob implements Job {
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		System.out.println("-----MySecondExerciseJob + myJobBusinessMethod");
		
	}

}
