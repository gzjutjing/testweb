package com.test.async;

import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Created by admin on 2017/3/1.
 */
public class Async {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        /*Future<String> future = executorService.submit(() -> {
            TimeUnit.SECONDS.sleep(2);
            return "hello";
        });
        try {
            System.out.println(new Date().toString());
            System.out.println(future.get());
            System.out.println(new Date().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        /////////////////////////
        CompletableFuture completableFuture = CompletableFuture.supplyAsync(new Supplier<String>() {

            *//*//**//**
         * Gets a result.
         *
         * @return a result
         *//*
            @Override
            public String get() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName());
                return "h";
            }
        });
        completableFuture.thenAccept((o) -> {
                    System.out.println((String) o);
                    System.out.println(new Date().toString());
                    System.out.println(Thread.currentThread().getName());
                }
        );*/
        /*completableFuture.handle(new BiFunction<String,Throwable, String>() {
            @Override
            public String apply(String o, Throwable o2) {
                if(o!=null){
                    System.out.printf("handled!!!");
                    return o;
                }else{
                    System.out.println("=="+o2.getMessage());
                    return "-1";
                }
            }
        });*/
        //completableFuture.completeExceptionally(new Exception("异常了"));
        CompletableFuture f1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            /**
             * Gets a result.
             *
             * @return a result
             */
            @Override
            public Integer get() {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 11;
            }
        }, executorService);
        CompletableFuture f2 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            /**
             * Gets a result.
             *
             * @return a result
             */
            @Override
            public Integer get() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 22;
            }
        }, executorService);
       /* CompletableFuture c = f1.thenAcceptBoth(f2, new BiConsumer<Integer, Integer>() {
            @Override
            public void accept(Integer o, Integer o2) {
                System.out.println(o + o2);
            }
        });*/
        CompletableFuture c1 = f1.thenCombine(f2, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer o, Integer o2) {
                System.out.println(o + o2);
                return o + o2;
            }
        });
        try {
            System.out.println(c1.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("------");
    }
}
