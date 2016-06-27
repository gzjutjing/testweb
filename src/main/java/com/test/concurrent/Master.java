package com.test.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by admin on 2016/6/27.
 */
public class Master {
    Queue<Object> workQueue = new ConcurrentLinkedQueue<>();
    Map<String, Thread> threadMap = new HashMap<>();
    Map<String, Object> resultMap = new ConcurrentHashMap<>();

    public boolean isComplete() {
        for (Map.Entry<String, Thread> thread : threadMap.entrySet()) {
            if (thread.getValue().getState() != Thread.State.TERMINATED) {
                return false;
            }
        }
        return true;
    }

    public Master(Worker worker, int count) {
        worker.setResultMap(resultMap);
        worker.setWorkQueue(workQueue);
        for (int i = 0; i < count; i++) {
            threadMap.put(Integer.toString(i), new Thread(worker, Integer.toString(i)));
        }
    }

    public void submit(Object o) {
        workQueue.add(o);
    }

    public Map<String, Object> getResultMap() {
        return resultMap;
    }

    public void execute() {
        for (Map.Entry<String, Thread> entry : threadMap.entrySet()) {
            entry.getValue().start();
        }
    }
}
