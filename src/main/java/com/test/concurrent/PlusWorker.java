package com.test.concurrent;

/**
 * Created by admin on 2016/6/27.
 */
public class PlusWorker extends Worker {
    @Override
    public Object handle(Object input) {
        Integer i = (Integer) input;
        return i * i * i;
    }
}
