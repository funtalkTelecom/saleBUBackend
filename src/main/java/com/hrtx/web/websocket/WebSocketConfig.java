package com.hrtx.web.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * 配置WebSocket,需要在配置类上使用@EnableWebSocketMessageBroker开启WebSocket支持
 * @EnableWebSocketMessageBroker注解同时会开启使用STOMP协议来传输基于代理（message broker）用的消息，
 * 这时控制器支持使用@MessageMapping，就像使用@RequestMapping一样。
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer  {
    /**
     * 注册STOMP协议的节点（endpoint）并映射指定的URL
     * @param registry
     */
    public void registerStompEndpoints(StompEndpointRegistry registry){
        //注册一个STOMP的endpoint，并指定使用SockJs协议
        registry.addEndpoint("/endpointWisely").setAllowedOrigins("*").withSockJS();
    }

    /**
     * 配置消息代理类
     * @param registry
     */
    public void configureMessageBroker(MessageBrokerRegistry registry){
        //广播式应配置一个/topic消息代理
        registry.enableSimpleBroker("/topic");
    }

    /*@Bean
    public ServerEndpointExporter serverEndpointExporter()
    {
        return  new ServerEndpointExporter();
    }*/
}
