package com.hrtx.config.litenners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hrtx.web.service.PermissionService;

@WebListener
public class CommonListener implements ServletContextListener {

	private Logger log = LoggerFactory.getLogger(CommonListener.class);
	
	@Autowired PermissionService permissionService;
	
	public void contextInitialized(ServletContextEvent event) {
		permissionService.checkOrInsertPermission();
		log.info("进行了 权限表检查+++++++++++++++++++++++++++++++++++++++++++++++");
	}

	public void contextDestroyed(ServletContextEvent event) {
	}

}