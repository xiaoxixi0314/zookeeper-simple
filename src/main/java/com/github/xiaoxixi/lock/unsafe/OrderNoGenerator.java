package com.github.xiaoxixi.lock.unsafe;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class OrderNoGenerator {

    public static int startNo = 0;

    public String getOrderNoUnSafe() {
        String date = DateFormatUtils.format(new Date(), "yyyyMMdd");
        return date + "-" + ++startNo;
    }

    public void printOrderNo() {
        System.out.println(Thread.currentThread().getName() + ":" + getOrderNoUnSafe());
    }
}
