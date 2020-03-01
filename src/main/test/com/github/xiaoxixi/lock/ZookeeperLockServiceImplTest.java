package com.github.xiaoxixi.lock;

import com.github.xiaoxixi.lock.unsafe.OrderNoGenerator;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

public class ZookeeperLockServiceImplTest extends BaseTest{

    private OrderNoGenerator unSafe = new OrderNoGenerator();
    private static final int THREAD_NUMS = 20;

    CountDownLatch countDown = new CountDownLatch(THREAD_NUMS);


    @Resource(name ="zkLockService")
    private LockService lockService;

    @Test
    public void testZkLock() throws Exception {
        for (int i = 0; i < THREAD_NUMS; i++){
            new Thread(() -> {
                System.out.println("start generate order no ..............");
                countDown.countDown();
                generateOrderNo();
            }).start();
        }
        countDown.await();
        Thread.sleep(3000);
    }

//    @Test
//    public void testZkLockSeq() throws Exception {
//
//        for (int i = 0; i < THREAD_NUMS; i++){
//            new Thread(() -> {
//                System.out.println("start generate order no ..............");
//                countDown.countDown();
//                System.out.println(generateOrderNo());
//            }).start();
//        }
//        countDown.await();
//        Thread.sleep(3000);
//    }

    public void generateOrderNo() {

        try {
            lockService.lock();
            unSafe.printOrderNo();
        } finally {
            lockService.unLock();
        }
    }


}