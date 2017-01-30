package com.test.jpa;

import com.test.domain.TestDomain;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by admin on 2016/4/26.
 */
public interface ITestDomainJpaDao extends JpaRepository<TestDomain, Integer> {

    TestDomain findByName(String name);
}
