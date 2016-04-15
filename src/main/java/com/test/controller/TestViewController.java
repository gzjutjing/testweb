package com.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by admin on 2016/4/14.
 */
@Controller
public class TestViewController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }
}
