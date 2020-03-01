package com.github.xiaoxixi.lock;

/**
 * 锁服务
 * 模板方法
 */
public abstract class AbstractLockService implements LockService {

    @Override
    public void lock(){
        // 竞争锁失败，等待锁，继续获取锁直到获取成功
        if (!tryLock()) {
            waitLock();
            // 递归继续获取锁
            lock();
        }
    }


    /**
     * 占有锁
     * @return
     */
    public abstract boolean tryLock();

    /**
     * 等待锁
     */
    public abstract void waitLock();

}
