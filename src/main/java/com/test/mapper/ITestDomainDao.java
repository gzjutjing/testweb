package com.test.mapper;

import com.test.domain.TestDomain;

/**
 * Created by admin on 2016/4/26.
 */
public interface ITestDomainDao {
    TestDomain selectById(Integer id);

}
