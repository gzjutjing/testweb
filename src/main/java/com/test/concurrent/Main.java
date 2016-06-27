package com.test.concurrent;

import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2016/6/27.
 */
public class Main {
    public static void main(String[] args) {
        Master master = new Master(new PlusWorker(), 5);
        for (int i = 0; i < 3; i++) {
            master.submit(i);
        }
        master.execute();
        Map<String, Object> resultMap = master.getResultMap();

        Integer re=0;
        while (resultMap.size() > 0 || !master.isComplete()) {
            Set<String> keys = resultMap.keySet();
            String key =null;
            for(String k:keys){
                key=k;
                break;
            }
            Integer i =null;
            if(key!=null){
                i=(Integer)resultMap.get(key);
            }
            if(i!=null){
                //最终结果
                re+=i;
            }
            if(key!=null){
                //移除已经被计算过的项
                resultMap.remove(key);
            }
        }
        System.out.println(re);
    }
}
