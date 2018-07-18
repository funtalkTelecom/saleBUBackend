/*
package com.hrtx.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

//初始化Session配置
@EnableCaching
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 7200)//2小时
public class SessionInitializer extends AbstractHttpSessionApplicationInitializer{
  public SessionInitializer() {
      super(RedisConfig.class);
  }
}
*/
