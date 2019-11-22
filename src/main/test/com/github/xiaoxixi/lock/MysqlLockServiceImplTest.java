package com.github.xiaoxixi.lock;

import com.github.xiaoxixi.lock.unsafe.UnSafeOrderNoGenerator;
import org.junit.Test;
import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

public class MysqlLockServiceImplTest extends BaseTest {

    private UnSafeOrderNoGenerator unSafe = new UnSafeOrderNoGenerator();
    private static final int THREAD_NUMS = 100;
    CountDownLatch countDown = new CountDownLatch(THREAD_NUMS);

    @Resource(name ="mysqlLockService")
    private LockService lockService;

    @Test
    public void lockTest() throws InterruptedException {
        for (int i =0; i < THREAD_NUMS; i++) {
            System.out.println(i);
            new Thread(() -> {
               countDown.countDown();
               generateOrderNo();
            }).start();
        }
        countDown.await();
        System.out.println("start test....");
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