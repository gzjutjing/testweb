package com.test.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by admin on 2016/4/19.
 */
@ControllerAdvice
public class AllExceptionHandler {
    Logger logger = LoggerFactory.getLogger(AllExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView exceptionHandler(RuntimeException runtimeException) {
        logger.error(runtimeException.getMessage());
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("msg", "您的程序异常啦！!");
        modelAndView.setViewName("error");
        return modelAndView;
    }
}
