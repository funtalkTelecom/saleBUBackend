package com.hrtx.config.aop;

import com.hrtx.dto.Result;
import com.hrtx.global.LockUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

//import com.hrtx.annotation.Action;


//@Aspect
//@Component
public class TestAspect {
    //    @Around、@Before、@After、@AfterReturning
    @Pointcut(value="@annotation(com.hrtx.config.annotation.NoRepeat)")
//    @Pointcut(value="execution(* com.hrtx.web.service.*.pay*(..))")
    public void pointCut(){};

//    @After(value = "pointCut()")
//    public void after(JoinPoint joinPoint){
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        Action action = methodSignature.getMethod().getAnnotation(Action.class);
//        System.out.println(action.name());
//    }

    //    @After(value="value="execution(* com.hrtx.web.service.*.pay*(..))")
//    public void after(JoinPoint joinPoint){
//        System.out.println("方法后执行。。。。。。。。。。");
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        Method method = methodSignature.getMethod();
//        Parameter[] parameters = method.getParameters();
//        for (Parameter parameter:parameters) {
//            Annotation[] annotations =  parameter.getAnnotations();
//            System.out.println(parameter.getName());
//        }
//        System.out.println("-------------------");
//
//    }
    @Around(value="pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        /*Object[] args = joinPoint.getArgs();
        StringBuffer params = new StringBuffer();
        for (Object object:args) {
            params.append(String.valueOf(object));
        }
        System.out.println(params.toString());
        if(StringUtils.isNotBlank(params.toString())) {
            if(!LockUtils.tryLock(params.toString())) return new Result(Result.ERROR, "请勿重复请求");
            try {
                return joinPoint.proceed();
            } finally {
                LockUtils.unLock(params.toString());
            }
        }
        return joinPoint.proceed();*/
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        Method method = methodSignature.getMethod();
//        Object[] objects = joinPoint.getArgs();
//        Parameter[] parameters = method.getParameters();
//        int i = 0;
//        for (Parameter parameter:parameters) {
//            Annotation[] annotations =  parameter.getAnnotations();
//            Object object = objects[i];
//            for (Annotation annotation:annotations) {
//
//            }
//            System.out.println(object.getClass());
//            System.out.println(object.toString());
//            System.out.println(annotations.length);
//            i++;
//        }
//        Object rvt = joinPoint.proceed();
//        return rvt;
          return joinPoint.proceed();
    }

//    @Before(value="execution(* com.hrtx.web.service.*.pay*(..))")
//    @Before(value="pointCut()")
//    public void before(JoinPoint joinPoint) {
//        System.out.println("方法前执行。。。。。。。。。。");
//        for (int i = 0; i < joinPoint.getArgs().length; i++) {
//            System.out.println(joinPoint.getArgs()[i]);
//        }
//        System.out.println(joinPoint.getSignature().getName());
//    }
}
