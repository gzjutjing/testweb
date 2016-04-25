package com.test.controller;

import com.test.domain.TestDomain;
import com.test.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/4/14.
 */
@Controller
public class TestViewController {

    @Autowired
    private ITestService testService;
    @Autowired
    private JmsOperations jmsOperations;

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
        modelMap.put("tt", "tt1");
        modelMap.put("mockList", getList(20));
        return "mock1";
    }

    @RequestMapping("/mock/{id}")
    public String mockPath(@PathVariable String id, ModelMap modelMap) {
        modelMap.put("id", id);
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

    @RequestMapping(value = "/validTest", produces = "application/json;;charset=UTF-8")
    @ResponseBody
    public String validTest(@Valid TestDomain testDomain, Errors result) {
        if (result.hasErrors()) {
            return result.getAllErrors().get(0).getDefaultMessage();
        }
        return "ok";
    }

    @RequestMapping(value = "/fileUp", method = RequestMethod.POST)
    @ResponseBody
    public void fileUp(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        try {
            Part part = request.getPart("file");
            file.transferTo(new File("d:/" + file.getOriginalFilename()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/jms")
    @ResponseBody
    public String jms(ModelMap modelMap) {
        TestDomain testDomain = new TestDomain();
        testDomain.setName("jingjiong");
        jmsOperations.convertAndSend("jing", testDomain);
        System.out.println("---------------------send");
//        TestDomain t = (TestDomain) jmsOperations.receiveAndConvert("jing");
//        System.out.println(t.getName());
        return "消息发送ok！";
    }
}
