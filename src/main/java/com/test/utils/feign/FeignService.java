package com.test.utils.feign;

import com.test.controller.ExpressReturn;
import feign.*;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;

import java.io.IOException;

/**
 * rest请求远程接口
 *
 * @TODO 异常控制
 * Created by admin on 2017/1/11.
 */
public interface FeignService {
    //baidu测试
    @RequestLine("GET /boot")
    @Body("{params}")
    String test(@Param("params") String params);

    /**
     * 只需要定义接口
     *
     * @param params 实验body的请求
     * @param a      rest
     * @param b
     * @return
     */
    @RequestLine("GET /additional?a={a}&b={b}")
    @Body("{params}")
    int add(@Param("params") String params, @Param("a") Integer a, @Param("b") Integer b);

    /**
     * header必须加上content-type
     *
     * @param expressReturn
     * @return
     */
    @RequestLine("POST /testFeign2")
    @Headers("Content-Type: application/json")
    ExpressReturn objectTest(ExpressReturn expressReturn);

    static class ServiceErrorDecoder implements ErrorDecoder {
        final Decoder decoder;
        final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

        ServiceErrorDecoder(Decoder decoder) {
            this.decoder = decoder;
        }

        @Override
        public Exception decode(String methodKey, Response response) {
            try {
                System.out.println("feign异常xianshi--------------------------------");
                return (Exception) decoder.decode(response, Exception.class);
            } catch (IOException fallbackToDefault) {
                return defaultDecoder.decode(methodKey, response);
            }
        }
    }
}
