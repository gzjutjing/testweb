package com.test.service.impl;

import com.test.domain.TestDomain;
import com.test.mapper.ITestDomainDao;
import com.test.service.ITestService;
import configuration.MyConditional;
import org.springframework.beans.factory.annotation.Autowired;
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
    public TestDomain getById(Integer id) {
        return testDomainMapper.selectById(id);
    }
}
