package com.hrtx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(value = {"classpath:applicationContext.xml"})
@Configuration
@ComponentScan(basePackages = "com.hrtx")
@ServletComponentScan
@EnableAspectJAutoProxy //开启aop切面编程
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class SpringbootApplication {

    public static void main(String[] args) {
    	SpringApplication.run(SpringbootApplication.class, args); 
    }
    
    
}
