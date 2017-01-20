package com.test.service;

import com.test.domain.TestDomain;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;

/**
 * Created by admin on 2016/4/11.
 */
public interface ITestService {

    public int profileLevel1();
    public float profileLevel2();

    public List<TestDomain> getMockTestList(int size);

    public TestDomain getById(Integer id);

    public String modifyReturn(String name);

    public String asyncTest();
}

