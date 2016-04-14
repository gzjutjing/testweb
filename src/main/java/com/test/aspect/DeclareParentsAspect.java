package com.test.aspect;

import com.test.service.IDeclareParentsTest;
import com.test.service.impl.DeclareParentsImpl;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

/**
 * Created by admin on 2016/4/13.
 */
@Aspect
@Service
public class DeclareParentsAspect {

    @DeclareParents(value = "com.test.service.ITestService+", defaultImpl = DeclareParentsImpl.class)
    public IDeclareParentsTest declareParentsTest;
}
