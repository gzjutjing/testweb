package com.test.controller;

import com.test.domain.TestDomain;
import com.test.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/4/14.
 */
@Controller
public class TestViewController {

    @Autowired
    private ITestService testService;

    public ITestService getTestService() {
        return testService;
    }

    public void setTestService(ITestService testService) {
        this.testService = testService;
    }

    @RequestMapping("/")
    public String index(ModelMap modelMap) {
        modelMap.put("test", "test");
        return "index";
    }

    @RequestMapping("/mock")
    public String mock(ModelMap modelMap) {
        modelMap.put("tt","tt1");
        modelMap.put("mockList",getList(20));
        return "mock1";
    }

    private List<TestDomain> getList(int size) {
        List<TestDomain> l = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TestDomain t = new TestDomain();
            t.setId(i);
            t.setName("name=" + i);
            l.add(t);
        }
        return l;
    }
}
