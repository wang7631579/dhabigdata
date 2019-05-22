package com.dha.dhabigdata.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class DhaListener implements ServletContextListener{
  
	//public final static Logger logger = LoggerFactory.getLogger(UserController.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("---------------DhaListener");
		//ServletContextListener.super.contextInitialized(sce);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		//ServletContextListener.super.contextDestroyed(sce);
	}
}
