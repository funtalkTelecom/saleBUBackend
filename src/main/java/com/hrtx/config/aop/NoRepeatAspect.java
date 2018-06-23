package com.hrtx.config.aop;

import com.hrtx.config.advice.ServiceException;
import com.hrtx.global.LockUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NoRepeatAspect {
    public final Logger log = LoggerFactory.getLogger(this.getClass());
    private  Class[] baseClass = new Class[]{String.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Boolean.class, Character.class};

    @Pointcut(value="@annotation(com.hrtx.config.annotation.NoRepeat)")
    public void pointCut(){};

    @Around(value="pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        StringBuffer params = new StringBuffer();
        for (Object object:args) {
            if(object != null) {
                log.info("--------"+String.valueOf(object)+"---"+object.getClass().isPrimitive());
                if(ArrayUtils.contains(baseClass, object.getClass()) || object.getClass().isPrimitive()) {
                    params.append(String.valueOf(object));
                }else{
                    params.append(JSONObject.fromObject(object).toString());
                }
            }else {
                params.append("null");
            }
        }
        String market = joinPoint.getSignature().getName()+"|"+params.toString();
        log.info("接收到需要验证重复请求的方法参数："+market);
        if(StringUtils.isNotBlank(market)) {
            if(!LockUtils.tryLock(market)) {
                throw new ServiceException("请勿重复请求");
            }
            try {
                return joinPoint.proceed();
            } finally {
                LockUtils.unLock(market);
            }
        }
        return joinPoint.proceed();
    }
}
