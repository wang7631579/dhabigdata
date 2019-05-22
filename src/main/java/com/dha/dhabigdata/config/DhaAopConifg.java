package com.dha.dhabigdata.config;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Configuration
public class DhaAopConifg {

	public static long startTime;
	public static long endTime;

	/**
	 * 切点 @Pointcut("execution(*
	 * com.example.demo.controller.find*(..))")
	 * @Pointcut("execution(public *
	 * com.example.demo.controller.*.*(..))")
	 */
	@Pointcut("execution(public * com.dha.dhabigdata.controller.*.*(..))")
	public void printMethod() {
		System.out.println("printMethod");
	}

	// 前置通知
	@Before("printMethod()")
	public void before(JoinPoint joinPoint) {
		System.out.println("我是前置通知!!!");
		// 获取目标方法的参数信息
		Object[] obj = joinPoint.getArgs();
		Signature signature = joinPoint.getSignature();
		// 代理的是哪一个方法
		System.out.println("方法：" + signature.getName());
		// AOP代理类的名字
		System.out.println("方法所在包:" + signature.getDeclaringTypeName());
		// AOP代理类的类（class）信息
		signature.getDeclaringType();
		MethodSignature methodSignature = (MethodSignature) signature;
		String[] strings = methodSignature.getParameterNames();
		System.out.println("参数名：" + Arrays.toString(strings));
		System.out.println("参数值ARGS : " + Arrays.toString(joinPoint.getArgs()));
		// 接收到请求，记录请求内容
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest req = attributes.getRequest();
		// 记录下请求内容
		System.out.println("请求URL : " + req.getRequestURL().toString());
		System.out.println("HTTP_METHOD : " + req.getMethod());
		System.out.println("IP : " + req.getRemoteAddr());
		System.out.println("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "."
				+ joinPoint.getSignature().getName());

	}

	// 后置通知
	@After("printMethod()")
	public void after() {
		endTime = System.currentTimeMillis() - startTime;
		System.out.println("after begin");
	}

	@AfterReturning(pointcut = "printMethod()", returning = "object")
	public void getAfterReturn(Object object) {
		System.out.println("本次接口耗时={}ms" + endTime);
		System.out.println("afterReturning={}" + object.toString());
	}

	/**
	 * 后置异常通知
	 * 
	 * @param jp
	 */
	@AfterThrowing("printMethod()")
	public void throwss(JoinPoint jp) {
		System.out.println("方法异常时执行.....");
	}

	/**
	 * 环绕通知,环绕增强，相当于MethodInterceptor
	 * 
	 * @param pjp
	 * @return
	 */
	@Around("printMethod()")
	public Object arround(ProceedingJoinPoint pjp) {
		System.out.println("arround");
		try {
			Object o = pjp.proceed();
			return o;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

}
