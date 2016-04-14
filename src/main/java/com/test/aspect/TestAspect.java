package com.test.aspect;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

/**
 * Created by admin on 2016/4/13.
 */
@Aspect
@Service
public class TestAspect {
    @Pointcut(value = "execution(** com.test.service..*.*(..))")
    public void test(){

    }

    @Before("test()")
    public void before(){
        System.out.println("--aspect-before");
    }

    @AfterThrowing("test()")
    public void throwing(){
        System.out.println("---AfterThrowing");
    }
}
