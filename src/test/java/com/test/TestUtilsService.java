package com.test;

import com.test.aspect.DeclareParentsAspect;
import com.test.service.IDeclareParentsTest;
import com.test.service.ITestService;
import configuration.AutoConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by admin on 2016/4/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AutoConfig.class)
public class TestUtilsService extends BaseTestUtils {

    @Autowired
    private ITestService testService;

    @Test
    public void notNull() {
        Assert.assertNotNull(testService);
    }

    @Test
    public void testProfileL1() {
        Assert.assertEquals(testService.profileLevel1(), 1);
        logger.debug("l2={}", testService.profileLevel2());
        //Assert.assertEquals(testService.profileLevel2(),null);
        ((IDeclareParentsTest)testService).showMe();

    }
}
