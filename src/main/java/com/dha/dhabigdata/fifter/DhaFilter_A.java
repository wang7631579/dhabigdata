package com.dha.dhabigdata.fifter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.core.config.Order;

@WebFilter(urlPatterns = "/*")
@Order(0)
public class DhaFilter_A implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("DhaFilter_A DhaFilter_A DhaFilter_A");
		if (false) {
			((HttpServletResponse) response).sendRedirect("");// 重定向
		}
		// doFilter将请求转发给过滤器链下一个filter , 如果没有filter那就是你请求的资源
		chain.doFilter(request, response);

	}

}
