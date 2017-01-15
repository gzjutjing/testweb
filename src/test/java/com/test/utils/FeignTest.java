package com.test.utils;

import com.test.BaseTest;
import com.test.utils.feign.FeignService;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.junit.Test;

/**
 * Created by jingjiong on 2017/1/15.
 */
public class FeignTest extends BaseTest {
    @Test
    public void feignTest(){
        FeignService feignService = Feign.builder().encoder(new JacksonEncoder())
                .logger(new feign.Logger.ErrorLogger()).logLevel(feign.Logger.Level.FULL)
                .target(FeignService.class, "http://localhost:8080");
        String s=feignService.test("p=1");
        System.out.println(s);
    }
}
