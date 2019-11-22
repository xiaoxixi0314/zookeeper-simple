package com.github.xiaoxixi.lock;

import com.github.xiaoxixi.lock.mapper.LockServiceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("mysqlLockService")
public class MysqlLockServiceImpl extends AbstractLockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlLockServiceImpl.class);

    private static final Integer LOCK_ID = 1;

    @Autowired
    private LockServiceMapper lockServiceMapper;

    @Override
    public boolean tryLock() {
        try {
            lockServiceMapper.insert(LOCK_ID);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void waitLock(){
        try {
            Thread.sleep(10);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            LOGGER.error("wait lock interrupt exception:", ie);
            throw new RuntimeException("wait lock error:", ie);
        }
    }

    @Override
    public void unLock() {
        lockServiceMapper.delete(LOCK_ID);
    }
}
