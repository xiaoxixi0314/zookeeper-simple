package com.github.xiaoxixi.lock;

import com.github.xiaoxixi.lock.enums.LockStrategyEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class LockServiceRouter{

    @Autowired
    private Map<LockStrategyEnum, LockService> lockServiceMap;

    public void safeOperate(LockStrategyEnum strategy, Operate action){
        LockService lock = getLockService(strategy);
        try {
            lock.getLock();
            action.process();
        } finally {
            lock.unLock();
        }
    }

    private LockService getLockService(LockStrategyEnum strategy) {
        return lockServiceMap.get(strategy);
    }
}
