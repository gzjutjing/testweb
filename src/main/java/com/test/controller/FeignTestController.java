package com.test.controller;

import com.test.utils.feign.FeignService;
import com.test.utils.feign.FeignServiceImpl;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by jingjiong on 2017/1/15.
 */
@Controller
public class FeignTestController {
    @RequestMapping("/feign")
    @ResponseBody
    public void testFeign(){
        FeignServiceImpl impl=new FeignServiceImpl();

        FeignService feignService = HystrixFeign.builder().encoder(new JacksonEncoder())
                .logger(new feign.Logger.ErrorLogger()).logLevel(feign.Logger.Level.FULL)
                .target(FeignService.class, "http://localhost:8089",impl);
        String s=feignService.test("p=fasdf");
        System.out.println(s);
    }
}
