package com.test.utils;

/**
 * Created by admin on 2016/9/4.
 */
public class VolitileExample {
    int x=0;
    volatile int b=0;
    private void write(){
        x=5;
        b=1;
    }
    private void read(){
        int dummy=b;
        while(x!=5){
            System.out.printf("----");
        }
        System.out.printf(dummy+"-"+x);
    }

    public static void main(String[] args) throws InterruptedException {
        VolitileExample example=new VolitileExample();
        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                example.write();
            }
        });
        Thread t2=new Thread(new Runnable() {
            @Override
            public void run() {
                example.read();
            }
        });
        t1.start();
        t2.start();
        //t1.join();
        //t2.join();
    }
}
