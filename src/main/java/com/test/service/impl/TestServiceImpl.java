package com.test.service.impl;

import com.test.annotation.AnnotationName;
import com.test.domain.TestDomain;
import com.test.mapper.ITestDomainDao;
import com.test.service.ITestService;
import configuration.MyConditional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/4/11.
 */
@Service
@Conditional(MyConditional.class)
public class TestServiceImpl implements ITestService {
    @Autowired
    private ITestDomainDao testDomainMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    //@Cacheable(value = "ccccc", keyGenerator = "commonKeyGenerator")
    public TestDomain getById(Integer id) {
        System.out.println("---------------------------------getbyid");
        List<Map<String, Object>> map = jdbcTemplate.queryForList("SHOW VARIABLES LIKE '%char%'");
        /*map.forEach((k, v) -> {
            System.out.println(k + "----" + v);
        });*/
        System.out.println(map.size());
        System.out.println(testDomainMapper.findByName("11").getName());
        return testDomainMapper.selectById(id);
    }

    @Override
    @AnnotationName()
    public String modifyReturn(String name) {
        System.out.println("========modifyReturn name=" + name);
        return name;
    }

    @Override
    @Async
    public String asyncTest() {
        System.out.println("异步调用开始---------------------");
        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("异步调用结束---------------------");
        return "异步数据返回";
    }
}
