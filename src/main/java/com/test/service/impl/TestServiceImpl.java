package com.test.service.impl;

import com.test.annotation.AnnotationName;
import com.test.domain.TestDomain;
import com.test.mapper.ITestDomainDao;
import com.test.service.ITestService;
import configuration.MyConditional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by admin on 2016/4/11.
 */
@Service
@Conditional(MyConditional.class)
public class TestServiceImpl implements ITestService {
    @Autowired
    private ITestDomainDao testDomainMapper;

    @Override
    public int profileLevel1() {
        return 1;
    }

    @Override
    public float profileLevel2() {
        return 1;
    }

    @Override
    public List<TestDomain> getMockTestList(int size) {
        return null;
    }

    @Override
    @Cacheable(value = "ccccc",keyGenerator = "commonKeyGenerator")
    public TestDomain getById(Integer id) {
        System.out.println("---------------------------------getbyid");
        return testDomainMapper.selectById(id);
    }

    @Override
    @AnnotationName()
    public String modifyReturn(String name) {
        System.out.println("========modifyReturn name="+name);
        return name;
    }
}
