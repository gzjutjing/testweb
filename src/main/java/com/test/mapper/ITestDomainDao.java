package com.test.mapper;

import com.test.domain.TestDomain;

import java.util.List;

/**
 * Created by admin on 2016/4/26.
 */
public interface ITestDomainDao {
    public TestDomain selectById(Integer id);
}
