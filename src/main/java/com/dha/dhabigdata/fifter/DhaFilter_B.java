package com.dha.dhabigdata.fifter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.core.config.Order;

@WebFilter(urlPatterns = "/*")
@Order(1)
public class DhaFilter_B implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("DhaFilter_B DhaFilter_B DhaFilter_B");
		if (false) {
			((HttpServletResponse) response).sendRedirect("");// 重定向
		}
		// doFilter将请求转发给过滤器链下一个filter , 如果没有filter那就是你请求的资源
		chain.doFilter(request, response);
	 	
	}

	
}
