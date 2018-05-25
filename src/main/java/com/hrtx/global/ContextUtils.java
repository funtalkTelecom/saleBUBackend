package com.hrtx.global;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

public class ContextUtils extends ContextLoaderListener {
	
	private static ApplicationContext context;

	public static ApplicationContext getContext() {
		return context;
	}

	public static void setContext(ApplicationContext aContext) {
		context = aContext;
	}

}