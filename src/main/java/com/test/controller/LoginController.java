package com.test.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by admin on 2016/6/29.
 */
@Controller
@Api(value = "登录")
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
//    @Autowired
//    private TestDubboService testDubboService;
//    @Autowired
//    private TestHessian1Service testHessian1Service;
//    @Autowired
//    private TestHessian2Service testHessian2Service;

    @ApiOperation(value = "登录接口", notes = "登录接口描述notes")
    @RequestMapping("/login")
    public String login() {
        logger.info("---login page---");
//        System.out.println("-----login---"+testDubboService.hello("无语"));
//        System.out.println("---"+testHessian1Service.hessian1());
//        System.out.println("---"+testHessian2Service.hessian2());
        return "login";
    }

    @ApiOperation(value = "拒绝的接口", notes = "拒绝的接口note")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "参数map", name = "map")
    })
    @RequestMapping("/accessDeny")
    public String accessDeny(ModelMap map) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String u = "";
        if (principal instanceof UserDetails)
            u = ((UserDetails) principal).getUsername();
        else u = (String) principal;
        map.put("user", u);
        return "accessDeny";
    }

}
