package com.test.utils;

import com.test.BaseTest;
import com.test.controller.ExpressReturn;
import com.test.utils.feign.FeignService;
import com.test.utils.feign.FeignServiceImpl;
import feign.Feign;
import feign.Logger;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jingjiong on 2017/1/15.
 */
public class FeignTest extends BaseTest {
    @Test
    public void feignTest() {
        /*FeignService feignService = Feign.builder().encoder(new JacksonEncoder())
                .logger(new feign.Logger.ErrorLogger()).logLevel(feign.Logger.Level.FULL)
                .target(FeignService.class, "http://localhost:8080");
        String s = feignService.test("p=1");
        System.out.println(s);*/
        FeignService feignService2 = HystrixFeign.builder().encoder(new JacksonEncoder()).decoder(new JacksonDecoder())
                .logger(new feign.Logger.ErrorLogger()).logLevel(Logger.Level.FULL)
                .target(FeignService.class, "http://localhost:8080", new FeignServiceImpl());

        ExpressReturn expressReturn = new ExpressReturn();
        expressReturn.setStatus(1);
        expressReturn.setTrackingNo("tracking no");
        expressReturn.setExpressCode("10000000");
        ExpressReturn expressReturn1 = feignService2.objectTest(expressReturn);
        System.out.println(JsonUtils.writeValueAsString(expressReturn1));
        Assert.assertEquals("tracking no", expressReturn1.getTrackingNo());
    }
}
