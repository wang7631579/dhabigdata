package com.dha.dhabigdata.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dha.dhabigdata.interceptor.DhaInterceptor;

@Configuration
public class DhaMvcConfig implements WebMvcConfigurer {
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//追加默认访问 Spring Boot 2.0 以后  必须这么写 1.0 的方法 WebMvcConfigurerAdapter 
		System.out.println("----DhaMvcConfig----addViewControllers");
		registry.addViewController("/").setViewName("index");
		WebMvcConfigurer.super.addViewControllers(registry);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		System.out.println("----DhaMvcConfig---- addResourceHandlers");
		// 保证前段页面能够找到文件位置
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
		WebMvcConfigurer.super.addResourceHandlers(registry);	
		// 或者在配置文件中加入下面这 2个配置
		//#资源映射路径为/static/**
		//spring.mvc.static-path-pattern=/static/**	
		//spring.resources.static-locations=classpath:/static/ 	
		// 也可以使用模板引擎(Freemarker 或 Themeleaf )
		// 但是 默认制定的是templates文件夹下  的页面   	
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 不拦截路径 要变为/css/** 
		registry.addInterceptor(new DhaInterceptor())
			.addPathPatterns("/**")
			.excludePathPatterns("/","/static/**","/css/**","/img/**","/js/**");
		WebMvcConfigurer.super.addInterceptors(registry);
	}
	
	/*@Bean
	public LocaleResolver localeResolver() {
		//定义国际化转换
		//如何不使用模板引擎 页面国际化  
		//JSP---<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
		//或者使用模板引擎 
		//纯HTML 不支持Spring 的国际化
		
		return new DhaLocaleResolverConfig();
	}*/
	
}