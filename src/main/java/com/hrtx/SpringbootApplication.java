package com.hrtx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = "com.hrtx")
@ServletComponentScan
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class SpringbootApplication {

    public static void main(String[] args) {
    	SpringApplication.run(SpringbootApplication.class, args); 
    }
    
    
}
