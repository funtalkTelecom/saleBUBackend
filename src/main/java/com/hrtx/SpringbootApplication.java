package com.hrtx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.*;

@SpringBootApplication
@ImportResource(value = {"classpath:applicationContext.xml"})
@Configuration
@ComponentScan(basePackages = "com.hrtx")
@ServletComponentScan
@EnableAspectJAutoProxy //开启aop切面编程
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class SpringbootApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        System.out.println("====================独立tomcat启动中=====================");
        return application.sources(SpringbootApplication.class);
    }

    public static void main(String[] args) {
        System.out.println("====================内置tomcat启动中=====================");
        System.out.println("====================为了在部署到tomcat时确保一致的效果，取消内置tomcat启动方式 zjc 2018.7.18=====================");
    	SpringApplication.run(SpringbootApplication.class, args);
    }

}
