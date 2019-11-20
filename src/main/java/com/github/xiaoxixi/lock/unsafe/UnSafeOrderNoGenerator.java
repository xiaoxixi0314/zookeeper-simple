package com.github.xiaoxixi.lock.unsafe;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class UnSafeOrderNoGenerator {

    public static int startNo = 0;

    public String getOrderNo() {
        String date = DateFormatUtils.format(new Date(), "yyyyMMdd");
        return date + "-" + ++startNo;
    }
}
