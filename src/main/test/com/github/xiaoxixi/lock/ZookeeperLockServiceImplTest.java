package com.github.xiaoxixi.lock;

import com.github.xiaoxixi.lock.unsafe.UnSafeOrderNoGenerator;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

public class ZookeeperLockServiceImplTest extends BaseTest{

    private UnSafeOrderNoGenerator unSafe = new UnSafeOrderNoGenerator();
    private static final int THREAD_NUMS = 10;
    CountDownLatch countDown = new CountDownLatch(THREAD_NUMS);

    @Resource(name = "zkLockService")
    private LockService lockService;

    @Test
    public void testZkLock() throws Exception {
        for (int i = 0; i < THREAD_NUMS; i++){
            new Thread(() -> {
                countDown.countDown();
                generateOrderNo();
            }).start();
        }
        countDown.await();
        Thread.sleep(30000);
    }

    public void generateOrderNo() {
        try {
            lockService.getLock();
            String orderNo = unSafe.getOrderNo();
            System.out.println(Thread.currentThread().getName()+":" + orderNo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lockService.unLock();
        }
    }

}