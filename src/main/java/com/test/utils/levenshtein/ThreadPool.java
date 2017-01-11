package com.test.utils.levenshtein;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by admin on 2016/9/18.
 */
public class ThreadPool {
    private ExecutorService executorService;
    private String words;
    private int block;

    public ThreadPool(String words, int block) {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.words = words;
        this.block = block;
    }
    public DistancePair best(String target){

        return null;
    }
    public class task implements Callable<DistancePair>{
        private int offset;
        private String text;

        public task(){

        }
        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         * @throws Exception if unable to compute a result
         */
        @Override
        public DistancePair call() throws Exception {
            return null;
        }
    }
}

