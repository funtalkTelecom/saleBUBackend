package com.hrtx.config.litenners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.hrtx.global.ContextUtils;
import com.hrtx.global.SystemParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hrtx.web.service.PermissionService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebListener
public class CommonListener implements ServletContextListener {

	private Logger log = LoggerFactory.getLogger(CommonListener.class);
	
	@Autowired PermissionService permissionService;
	
	public void contextInitialized(ServletContextEvent event) {
		permissionService.checkOrInsertPermission();
		ServletContext context = event.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
		ContextUtils.setContext(ctx);
		SystemParam.load();
		log.info("进行了 权限表检查+++++++++++++++++++++++++++++++++++++++++++++++");
	}

	public void contextDestroyed(ServletContextEvent event) {
	}

}