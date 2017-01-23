package com.test.controller;

import com.test.domain.TestDomain;
import com.test.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;

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
    public String index(TestDomain testDomain, ModelMap modelMap) {
        modelMap.put("test", "test");
        testDomain.setName("aaa");
        try {
            Thread.sleep(1000 * 4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "index";
    }

    @RequestMapping("/mock")
    public String mock(ModelMap modelMap) {
        modelMap.put("tt", "tt1");
        modelMap.put("mockList", getList(20));
        return "mock1";
    }

    @RequestMapping("/mock/{id}")
    public String mockPath(@PathVariable Integer id, ModelMap modelMap) {
        modelMap.put("id", id);
        TestDomain testDomain = testService.getById(id);
        System.out.println(testDomain.getName());
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

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping("/redis")
    @ResponseBody
    public String redis() {
        //template.opsForValue().set("kj","king");
        TestDomain testDomain = testService.getById(1);
        return "ok";
    }

    //////////////
    @RequestMapping("/testaspect")
    @ResponseBody
    public String testaspect() {
        String s = testService.modifyReturn("aaa");
        System.out.println("----controller testaspect=" + s);
        return s;
    }

    ////////////////异步调用测试
    @RequestMapping("/async")
    @ResponseBody
    public String async() {
        String s = testService.asyncTest();
        System.out.println("s==============="+s);
        return "async is ok！s="+s;
    }

    @RequestMapping("/async/callable")
    public Callable<String> callable(ModelMap map) {
        System.out.println("thread nam=" + Thread.currentThread().getName());
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("yi bu shi yong ye mian");
                System.out.println("thread nam=" + Thread.currentThread().getName());
                Thread.sleep(1000 * 4);
                map.put("key", "value");
                return "asyncCallable";
            }
        };
    }

    @RequestMapping("/async/callableException")
    public Callable<String> callableException(ModelMap map) {
        System.out.println("thread nam=" + Thread.currentThread().getName());
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                throw new IllegalStateException("Callable error");
            }
        };
    }

    @RequestMapping("/async/callWithCustomTimeout")
    public WebAsyncTask<String> callWithCustomTimeout() {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(3000);
                return "自定义超时";
            }
        };
        return new WebAsyncTask<String>(1000, callable);
    }

    Queue<DeferredResult<String>> defferQueue = new ConcurrentLinkedDeque<>();

    //在异步处理完成时返回org.springframework.web.context.request.async.DeferredResult,
    // 其他线程，例如定时任务或一个JMS或一个AMQP消息,Redis通知等等：
    @RequestMapping("/async/deferred")
    @ResponseBody
    public DeferredResult<String> deferredResult() {
        DeferredResult<String> deferredResult = new DeferredResult<>();
        defferQueue.add(deferredResult);// Add deferredResult to a Queue or a Map...
        return deferredResult;
    }

    // In some other thread...
    //deferredResult.setResult(data);
    // Remove deferredResult from the Queue or Map
    @Scheduled(fixedRate = 2000)
    public void defferSchedule() {
        System.out.println("-------------testViewController 2s schedule");
        for (DeferredResult<String> result : this.defferQueue) {
            result.setResult("jie果");
            defferQueue.remove(result);
        }
    }

}
