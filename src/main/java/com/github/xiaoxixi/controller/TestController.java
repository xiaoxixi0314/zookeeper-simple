package com.github.xiaoxixi.controller;

import com.github.xiaoxixi.lock.LockServiceRouter;
import com.github.xiaoxixi.lock.enums.LockStrategyEnum;
import com.github.xiaoxixi.lock.unsafe.OrderNoGenerator;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@RestController
@RequestMapping("/test")
public class TestController{

    private OrderNoGenerator unSafe = new OrderNoGenerator();


    @Autowired
    private LockServiceRouter lockServiceRouter;

    @RequestMapping("/lock/{strategy}/{threadNums}")
    public String testMysqlLock(@PathVariable String strategy, @PathVariable Integer threadNums) {
        LockStrategyEnum strategyEnum = EnumUtils.getEnum(LockStrategyEnum.class, strategy.toUpperCase());
        if (Objects.isNull(strategyEnum)) {
            return "un support operation";
        }

        if (Objects.isNull(threadNums)) {
            threadNums = 1;
        }
        CountDownLatch countDown = new CountDownLatch(threadNums);
        try {
            for (int i = 0; i < threadNums; i++) {
                System.out.println(i);
                new Thread(() -> {
                    countDown.countDown();
                    lockServiceRouter.safeOperate(strategyEnum, () -> unSafe.printOrderNo());
                }).start();
            }
            countDown.await();
            System.out.println("start test....");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

}
