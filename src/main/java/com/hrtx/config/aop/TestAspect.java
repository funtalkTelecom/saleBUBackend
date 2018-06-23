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

    /**
     * 可以在执行方法之后对目标方法的参数进行判断
     * @param joinPoint
     */
    //    @After(value="value="execution(* com.hrtx.web.service.*.pay*(..))")
//    public void after(JoinPoint joinPoint){
//        System.out.println("After增强：模拟方法结束后的释放资源");
//        System.out.println("After增强：被织入增强处理的目标方法为：" + joinPoint.getSignature().getName());
//        System.out.println("After增强：目标方法的参数为：" + Arrays.toString(joinPoint.getArgs()));
//        System.out.println("After增强：被织入增强处理的目标对象为" + joinPoint.getTarget());
//    }

    /**
     * 可以在执行方法之前和之后改变参数和返回值
     * @param joinPoint
     * @return 最终的返回值
     * @throws Throwable
     */
    @Around(value="pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//        System.out.println("Around增强：执行方法之前，模拟开始事物");
//        Object[] args = joinPoint.getArgs();
//        if(args != null && args.length > 0 && args[0].getClass() == String.class) {
//            args[0] = "增加的前缀" + args[0];
//        }
//        Object rvt = joinPoint.proceed();
//        System.out.println("Around增强：执行方法之后，模拟结束事物");
//        if(rvt != null && rvt instanceof Integer) {
//            rvt = (Integer)rvt * (Integer)rvt;
//        }
        return joinPoint.proceed();
    }


//     * 可以在执行方法之前对目标方法的参数进行判断
//     * 通过抛出一个异常来阻断目标方法的访问
//     * @param joinPoint
//     */
//    @Before(value="execution(* com.hrtx.web.service.*.pay*(..))")
//    @Before(value="pointCut()")
//    public void authority(JoinPoint joinPoint) {
//        System.out.println("Before增强：模拟权限检查");
//        System.out.println("Before增强：被织入增强处理的目标目标方法为：" + joinPoint.getSignature().getName());
//        System.out.println("Before增强：目标方法的参数为：" + Arrays.toString(joinPoint.getArgs()));
//        joinPoint.getArgs()[0] = "除了Around其他的都是是不可以修改目标方法的参数的";
//        System.out.println("joinPoint.getArgs()[0]:"+joinPoint.getArgs()[0]);
//        System.out.println("Before增强：目标方法的参数为：" + Arrays.toString(joinPoint.getArgs()));
//        System.out.println("Before增强：被织入增强处理的目标对象为：" + joinPoint.getTarget());
//    }
    /**
     * 与After的区别在于AfterReturning只有在方法执行成功的之后才会被织入，如果After和
     * AfterReturning同时存在于一个文件中，谁写在前面谁先运行
     * @param joinPoint
     * @param rvt方法的返回值
     */
//    @AfterReturning(pointcut="execution(* com.service.*.*(..))", returning="rvt")
//    public void log(JoinPoint joinPoint, Object rvt) {
//        System.out.println("AfterReturning增强：获取目标方法的返回值：" + rvt);
//        System.out.println("AfterReturning增强：模拟日志功能");
//        System.out.println("AfterReturning增强：获织入增强的目标方法为：" + joinPoint.getSignature().getName());
//        System.out.println("AfterReturning增强：目标方法的参数为：" + Arrays.toString(joinPoint.getArgs()));
//        System.out.println("AfterReturning增强：被织入增强处理的目标对象为：" + joinPoint.getTarget());
//    }
}
