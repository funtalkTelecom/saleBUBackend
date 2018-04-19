package com.hrtx.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.hrtx.config.interceptors.PowerInterceptor;

@Configuration
@EnableAsync
//@EnableWebMvc
public class ApplicationConfig extends WebMvcConfigurerAdapter{

	@Value("${app.power.exclude-path}")
	private String[] powerExcludePath;
	
	@Autowired private PowerInterceptor powerInterceptor;
	
    public void addInterceptors(InterceptorRegistry registry) {
    	InterceptorRegistration ir=registry.addInterceptor(powerInterceptor).addPathPatterns("/**");//拦截
    	for (String value : powerExcludePath) {
    		ir.excludePathPatterns(value);//排除拦截
		}
        super.addInterceptors(registry);
    }
    
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new EmbeddedServletContainerCustomizer() {
            public void customize(ConfigurableEmbeddedServletContainer container) {

                ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401.html");
                ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
                ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");

                container.addErrorPages(error401Page, error404Page, error500Page);
            }
        };
    }
    
//    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//        configurer.enable();
//    }
    
    
    /*@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//    	registry.addResourceHandler("/**")
//    	.addResourceLocations("file:D:/Workspaces/egt365-hk/src/main/webapp/WEB-INF/static/")
//    	.resourceChain(false);
    	registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
//		super.addResourceHandlers(registry);
	}*/


	public Executor getAsyncExecutor() {
		//使用Spring内置线程池任务对象
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //设置线程池参数
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(25);
        taskExecutor.initialize();
        return taskExecutor;
	}
    
}
