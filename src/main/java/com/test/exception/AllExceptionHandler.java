package com.test.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by admin on 2016/4/19.
 */
@ControllerAdvice
public class AllExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public String exceptionHandler(){
        return "asdfasdfasfasfsf";
    }
}
