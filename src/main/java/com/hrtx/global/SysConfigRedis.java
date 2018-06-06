package com.hrtx.global;

import com.hrtx.config.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统配置Redis
 */
@Component
public class SysConfigRedis {
    public static String getSysConfigKey(String key){
        return "sys:config:" + key;
    }

    @Autowired  private RedisUtil redisUtils;

    public void saveOrUpdate(String Object) {
        String key = getSysConfigKey("");
        redisUtils.set(key, Object);
    }

    public void delete(String configKey) {
        String key = getSysConfigKey(configKey);
        redisUtils.del(key);
    }

    public String get(String configKey){
        String key = getSysConfigKey(configKey);
        return String.valueOf(redisUtils.get(key));
    }
}
