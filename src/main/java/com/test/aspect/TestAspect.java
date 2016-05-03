package com.test.aspect;

import com.test.annotation.AnnotationName;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by admin on 2016/4/13.
 */
@Aspect
@Service
public class TestAspect {
    @Pointcut(value = "execution(** com.test.service..*.*(..))")
    public void test() {

    }

    @Before("test()")
    public void before() {
        System.out.println("--aspect-before");
    }

    @AfterThrowing("test()")
    public void throwing() {
        System.out.println("---AfterThrowing");
    }

    @AfterReturning(pointcut = "test()", returning = "retVal")
    public void modify(JoinPoint joinPoint, Object retVal) {
        Object[] o = joinPoint.getArgs();
        if (o != null && o.length >= 1) {
            for (Object oo : o)
                System.out.println("AfterReturning=======" + oo);
            if (o.length == 1) {
                System.out.println("=====AfterReturning修改值=======");
                o[0] = "AfterReturning修改值";
            }
        }
    }


    @Around(value = "test()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] o = joinPoint.getArgs();
        if (o != null && o.length == 1) {
            System.out.println("------------------around修改值了！！");
            o[0] = "around修改值了！";
        }
        //获取目标对象对应的类名
        System.out.println("应用注解的类名：" + joinPoint.getTarget().getClass());
        //获取实现类继承的接口名
        Class[] c = joinPoint.getTarget().getClass().getInterfaces();
        System.out.println("注解对象所实现的接口名：" + c[0]);
        //获取到了注解在这个service实现类上的annotation
        Annotation[] a = joinPoint.getTarget().getClass().getAnnotations();
        //获取这个类上的注解的个数
        System.out.println("应用注解类上的注解个数：" + a.length);
        //判断这个类上面的注释是否是AnnotationName这个自定义的注解，如果是返回这个注解，如果不是返回null
        if (joinPoint.getTarget().getClass().getAnnotation(AnnotationName.class) != null) {
            //获取到这个类上的注解
            AnnotationName anns = joinPoint.getTarget().getClass().getAnnotation(AnnotationName.class);
            //输出这个类上的注解的值
            System.out.println("注释在实现类上的annotation：" + anns.value());
        }
        //判断这个接口上是否存在此注解
        if (c[0].getAnnotation(AnnotationName.class) != null) {
            AnnotationName an = (AnnotationName) c[0].getAnnotation(AnnotationName.class);
            System.out.println("注解对象所实现接口上的注解值：" + an.value());
        }
        //获取目标对象上正在执行的方法名
        String methodString = joinPoint.getSignature().getName();
        System.out.println("目标对象上正在执行的方法名：" + methodString);
        //获取到这个类上面的方法全名
        Method meths[] = joinPoint.getSignature().getDeclaringType().getMethods();
        System.out.println("方法上面的全名：" + meths[0]);
        //获取到这个类上面的方法上面的注释
        Annotation[] anns = meths[0].getDeclaredAnnotations();
        if (anns.length > 0)
            System.out.println("正在执行方法上面的注释：" + ((AnnotationName) anns[0]).value());
        //让你注释的那个方法执行

        return joinPoint.proceed(o);
    }
}
