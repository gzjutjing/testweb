package com.test.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by admin on 2016/4/19.
 */
@ControllerAdvice
public class AllExceptionHandler {
    Logger logger = LoggerFactory.getLogger(AllExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView exceptionHandler(RuntimeException runtimeException, HttpServletRequest request, HttpServletResponse response) {
        logger.error(runtimeException.getMessage());
        String xRequestedWith = request.getHeader("X-Requested-With");
        if (!StringUtils.isEmpty(xRequestedWith)) {
            try {
                response.setContentType("text/json; charset=UTF-8");
                response.setHeader("Cache-Control", "no-cache");
                response.getWriter().write("您的程序异常啦，详见日志!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("msg", "您的程序异常啦！!");
        modelAndView.setViewName("error");
        return modelAndView;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDenied(AccessDeniedException exception) {
        System.out.println("---------------您没有权限访问---------------");
        return "您没有权限访问";
    }
}
