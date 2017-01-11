package com.test.utils.feign;

import feign.Feign;
import feign.jackson.JacksonEncoder;

/**
 * 纯feign使用，没有spring cloud
 * 应该可以解析远程返回json直接转化为对象
 * Created by admin on 2017/1/11.
 */
public class UserFeign {
    public int add() {
        FeignService searchHttpService = Feign.builder().encoder(new JacksonEncoder())
                .logger(new feign.Logger.ErrorLogger()).logLevel(feign.Logger.Level.FULL)
                .target(FeignService.class, "http://localhost:8080");
        int count = searchHttpService.add("yuan yang shu chu", 1, 3);
        return count;
    }
}
