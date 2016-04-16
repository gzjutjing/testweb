package com.test.controller;

import com.test.domain.TestDomain;
import com.test.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
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

    @RequestMapping("/mock/{id}")
    public String mockPath(@PathVariable String id, ModelMap modelMap) {
        modelMap.put("id",id);
        testService.getById(id);
        return "mock2";
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

    @RequestMapping(value = "/validTest",produces = "application/json;;charset=UTF-8")
    @ResponseBody
    public String validTest(@Valid TestDomain testDomain, Errors result){
        if(result.hasErrors()){
            return result.getAllErrors().get(0).getDefaultMessage();
        }
        return "ok";
    }
}
