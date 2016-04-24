package com.test.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2016/4/24.
 */
@Service
public class Receive {
    @JmsListener(destination = "jing")
    public void test(Object o) {
        System.out.println("++++++"+o);
    }
}
