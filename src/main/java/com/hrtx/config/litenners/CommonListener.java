package com.hrtx.config.litenners;

import com.hrtx.global.ContextUtils;
import com.hrtx.global.Messager;
import com.hrtx.global.SystemParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hrtx.web.service.PermissionService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

//@WebListener
@Component
public class CommonListener implements ApplicationRunner {

	private Logger log = LoggerFactory.getLogger(CommonListener.class);
	@Autowired PermissionService permissionService;
	@Autowired SystemParam	systemParam;
	@Autowired private ServletContext servletContext;


	@Override
	public void run(ApplicationArguments var1) throws Exception{
		log.info("进行了 权限表检查+++++++++++++++++++++++++++++++++++++++++++++++");
		permissionService.checkOrInsertPermission();
		log.info("系统配置初始化+++++++++++++++++++++++++++++++++++++++++++++++");
		systemParam.load1();
		log.info("短信组件初始化+++++++++++++++++++++++++++++++++++++++++++++++");
		Messager.init();

		log.info("ContextUtils组件初始化+++++++++++++++++++++++++++++++++++++++++++++++");
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		ContextUtils.setContext(ctx);

	}

}