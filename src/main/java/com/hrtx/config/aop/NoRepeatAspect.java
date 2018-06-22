package com.hrtx.config.aop;

import com.hrtx.dto.Result;
import com.hrtx.global.LockUtils;
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

    @Pointcut(value="@annotation(com.hrtx.config.annotation.NoRepeat)")
    public void pointCut(){};

    @Around(value="pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        StringBuffer params = new StringBuffer();
        for (Object object:args) {
            params.append(String.valueOf(object));
        }
        log.info("接收到需要验证重复请求的方法参数："+params.toString());
        if(StringUtils.isNotBlank(params.toString())) {
            if(!LockUtils.tryLock(params.toString())) {
                log.info("请求重复");
                return new Result(Result.ERROR, "请勿重复请求");
            }
            try {
                return joinPoint.proceed();
            } finally {
                LockUtils.unLock(params.toString());
            }
        }
        return joinPoint.proceed();
    }
}
