/*
package com.hrtx.global;*/
package com.hrtx.global;


import com.hrtx.config.utils.RedisUtil;;
import com.hrtx.web.pojo.User;
import com.hrtx.web.pojo.ConsumerLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


@Component
public class ApiSessionUtil {
    @Autowired private RedisUtil redisUtils;
    public static final String JESSION_ID_NAME = "__sessid";
    private Long expire_time=7200l;

    public static String getApiKey(String key){
        return "egt-kh:api:" + key;
    }

    public String getTokenStr(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getParameter(JESSION_ID_NAME);
    }
    public User getUser(){
        String key = getApiKey(this.getTokenStr());
        return (User) (redisUtils.get(key));
    }
    public void saveOrUpdate(String apiKey,User user) {
        if(apiKey == null) return ;
        String key1 = getApiKey(apiKey);
        redisUtils.set(key1,user,expire_time);
    }
    public void updateExpire(String apiKey) {
        String key1 = getApiKey(apiKey);
        User bean=this.get(getApiKey(apiKey));
        if(bean == null) return ;
        redisUtils.set(key1,expire_time);
    }
    public void delete(String apiKey) {
        String key = getApiKey(apiKey);
        redisUtils.del(key);
    }

    public User get(String apiKey){
        String key = getApiKey(apiKey);
        return (User) (redisUtils.get(key));
    }


    /***
     * by zdh
     * @param apiKey
     * @param userClientLog
     */
    public void saveOrUpdate(String apiKey,ConsumerLog userClientLog) {
        if(apiKey == null) return ;
        String key1 = getApiKey(apiKey);
        redisUtils.set(key1,userClientLog,expire_time);
    }

    public ConsumerLog getUserClient(){
        String key = getApiKey(this.getTokenStr());
        return (ConsumerLog) (redisUtils.get(key));
    }
}