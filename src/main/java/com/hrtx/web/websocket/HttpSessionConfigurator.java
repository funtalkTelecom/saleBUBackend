package com.hrtx.web.websocket;

import org.apache.catalina.session.StandardSessionFacade;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * 从websocket中获取用户session
 *
 *
 */
public class HttpSessionConfigurator extends ServerEndpointConfig.Configurator{
    public final org.slf4j.Logger log = LoggerFactory.getLogger(WebSocketServer.class);
/*    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {

        HttpSession httpSession = (HttpSession) request.getHttpSession();
        sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
    }*/

    private static final String HttpSession = null;
    /* 修改握手,就是在握手协议建立之前修改其中携带的内容 */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        /*如果没有监听器,那么这里获取到的HttpSession是null*/
        StandardSessionFacade ssf = (StandardSessionFacade) request.getHttpSession();
        if (ssf != null) {
            HttpSession session = (HttpSession) request.getHttpSession();
            sec.getUserProperties().put("sessionid", session);
            log.info("获取到的SessionID：{}",session.getId());
        }
        //ApiSessionUtil  apiSessionUtil2=(ApiSessionUtil) ContextUtils.getContext().getBean("apiSessionUtil");
        //Long kk=apiSessionUtil2.getConsumer().getId();
        sec.getUserProperties().put("name", "小强");
        super.modifyHandshake(sec, request, response);
    }
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        //这个对象说一下，貌似只有服务器是tomcat的时候才需要配置,具体我没有研究
        return new ServerEndpointExporter();
    }
}