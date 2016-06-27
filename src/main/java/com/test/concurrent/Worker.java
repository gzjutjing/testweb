package com.test.concurrent;

import java.util.Map;
import java.util.Queue;

/**
 * Created by admin on 2016/6/27.
 */
public class Worker implements Runnable {
    Queue<Object> workQueue;
    Map<String, Object> resultMap;

    public Queue<Object> getWorkQueue() {
        return workQueue;
    }

    public void setWorkQueue(Queue<Object> workQueue) {
        this.workQueue = workQueue;
    }

    public Map<String, Object> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<String, Object> resultMap) {
        this.resultMap = resultMap;
    }

    public Object handle(Object input) {
        return input;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while (true) {
            Object o = workQueue.poll();
            if (o == null)
                break;
            Object handle = handle(o);
            resultMap.put(Integer.toString(handle.hashCode()), handle);
        }
    }
}
