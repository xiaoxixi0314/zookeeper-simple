package com.github.xiaoxixi.lock.unsafe;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StandAloneSafeOrderNoGenerator {

    private static int startNo = 0;
    private static Lock lock = new ReentrantLock();

    public String getOrderNo() {
        try {
            lock.lock();
            String date = DateFormatUtils.format(new Date(), "yyyyMMdd");
            return date + "-" + ++startNo;
        } finally {
            lock.unlock();
        }
    }


}
