package com.github.xiaoxixi.lock.unsafe;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StandAloneSafeOrderNoGeneratorTest extends Thread{

    StandAloneSafeOrderNoGenerator orderNoGenerator = new StandAloneSafeOrderNoGenerator();

    private static final int THREAD_NUMS = 2000;
    private static CountDownLatch countDown = new CountDownLatch(THREAD_NUMS);

    private static List<String> orderNoResult = new Vector<>();

    @Override
    public void run() {
        countDown.countDown();
        orderNoResult.add(orderNoGenerator.getOrderNo());
    }

    public static void main(String[] args){
        try {
            for (int i = 0; i < THREAD_NUMS; i++) {
                new StandAloneSafeOrderNoGeneratorTest().start();
            }
            countDown.await();

            Thread.sleep(3000);

            Map<String, Long> group = orderNoResult.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            for(Map.Entry<String, Long> entry :group.entrySet()) {
                if (entry.getValue() > 1L) {
                    System.out.println(entry.getKey()+":"+entry.getValue());
                }
            }
            System.out.println("test over");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
