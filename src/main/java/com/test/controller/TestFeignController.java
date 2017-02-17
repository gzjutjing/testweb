package com.test.controller;

import com.test.utils.JsonUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by admin on 2017/2/17.
 */
@Controller
public class TestFeignController {
    //此方法不能接受对象
    @RequestMapping("/testFeign/")
    @ResponseBody
    public ExpressReturn testFeign(ExpressReturn expressReturn){
        System.out.println(JsonUtils.writeValueAsString(expressReturn));
        return expressReturn;
    }
    //对象传递，接口方得传@Headers("Content-Type: application/json")和对象参数
    @RequestMapping("/testFeign2/")
    @ResponseBody
    public ExpressReturn testFeign2(@RequestBody ExpressReturn expressReturn){
        System.out.println(JsonUtils.writeValueAsString(expressReturn));
        return expressReturn;
    }
    //此方法不能接受对象
    @RequestMapping("/testFeign3/")
    @ResponseBody
    public ExpressReturn testFeign3(@RequestParam ExpressReturn expressReturn){
        System.out.println(JsonUtils.writeValueAsString(expressReturn));
        return expressReturn;
    }
}
