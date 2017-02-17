package com.test.utils.feign;

import com.test.controller.ExpressReturn;
import feign.Param;

import java.util.Date;

/**
 * Created by jingjiong on 2017/1/15.
 */
public class FeignServiceImpl implements FeignService {
    @Override
    public String test(@Param("params") String params) {
        System.out.println("-------------------" + new Date());
        return null;
    }

    @Override
    public int add(@Param("params") String params, @Param("a") Integer a, @Param("b") Integer b) {
        return 0;
    }

    /**
     * header必须加上content-type
     *
     * @param expressReturn
     * @return
     */
    @Override
    public ExpressReturn objectTest(ExpressReturn expressReturn) {
        return new ExpressReturn();
    }
}
