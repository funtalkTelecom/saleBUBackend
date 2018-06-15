package com.hrtx.config.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

//import com.hrtx.annotation.Action;


@Aspect
@Component
public class PayAspect {
//    @Around、@Before、@After、@AfterReturning
//    @Pointcut(value="@annotation(com.hrtx.annotation.Action)")
//    public void pointCut(){};

//    @After(value = "pointCut()")
//    public void after(JoinPoint joinPoint){
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        Action action = methodSignature.getMethod().getAnnotation(Action.class);
//        System.out.println(action.name());
//    }

    @After(value="execution(* com.hrtx.web.service.*.pay*(..))")
    public void after(JoinPoint joinPoint){
        System.out.println("方法后执行。。。。。。。。。。");
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter:parameters) {
            Annotation[] annotations =  parameter.getAnnotations();
            System.out.println(parameter.getName());
        }
        System.out.println("-------------------");

    }
    @Around(value="execution(* com.hrtx.web.service.*.pay*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("方法正在执行。。。。。。。。。。");
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] objects = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();
        int i = 0;
        for (Parameter parameter:parameters) {
            Annotation[] annotations =  parameter.getAnnotations();
            Object object = objects[i];
            for (Annotation annotation:annotations) {

            }
            System.out.println(object.getClass());
            System.out.println(object.toString());
            System.out.println(annotations.length);
            i++;
        }
        Object rvt = joinPoint.proceed();
        return rvt;
    }

//    @Before(value="execution(* com.hrtx.web.service.*.pay*(..))")
    public void before(JoinPoint joinPoint) {
        System.out.println("方法前执行。。。。。。。。。。");
        for (int i = 0; i < joinPoint.getArgs().length; i++) {
            System.out.println(joinPoint.getArgs()[i]);
        }
        System.out.println(joinPoint.getSignature().getName());
    }
}
