package com.test.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by admin on 2016/6/29.
 */
@Controller
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
//    @Autowired
//    private TestDubboService testDubboService;
//    @Autowired
//    private TestHessian1Service testHessian1Service;
//    @Autowired
//    private TestHessian2Service testHessian2Service;

    @RequestMapping("/login")
    public String login() {
        logger.info("---login page---");
//        System.out.println("-----login---"+testDubboService.hello("无语"));
//        System.out.println("---"+testHessian1Service.hessian1());
//        System.out.println("---"+testHessian2Service.hessian2());
        return "login";
    }
}
