package com.github.xiaoxixi.lock;


public class MysqlLockServiceImpl extends AbstractLockService {

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public void waitLock() {

    }

    @Override
    public void unLock() {

    }
}
