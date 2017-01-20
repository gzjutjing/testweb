package com.test.mapper;

import com.test.domain.TestDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by admin on 2016/4/26.
 */
public interface ITestDomainDao extends JpaRepository<TestDomain,Integer>{
    public TestDomain selectById(Integer id);

    TestDomain findByName(String name);
}
