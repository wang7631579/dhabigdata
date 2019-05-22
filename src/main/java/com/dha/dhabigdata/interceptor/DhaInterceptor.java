package com.dha.dhabigdata.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class DhaInterceptor implements HandlerInterceptor{
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("-----DhaInterceptor------"+request.getRequestURL());
		//System.out.println("-----DhaInterceptor------"+request.getRequestURL());
		/*System.out.println(request.getRequestURL());
		Object user = request.getSession().getAttribute("user");
		if(user == null ) {
			request.getRequestDispatcher("/").forward(request, response);
			return false;
		}else {
			return true;
		}*/
		
		return true;
		//return HandlerInterceptor.super.preHandle(request, response, handler);
	}
	
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
}
