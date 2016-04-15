package com.test.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by admin on 2016/4/12.
 */
@RestController
public class SpringBootTestController {
    private AtomicInteger i = new AtomicInteger();

    @RequestMapping("/boot")
    public String springBoot() {
        return i.incrementAndGet() + "aaaaaaaaaaaaa";
    }
}
