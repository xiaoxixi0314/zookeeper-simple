package com.github.xiaoxixi.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * 注意羊群效应
 */
@Service("zkLockService")
public class ZookeeperLockServiceImpl extends AbstractLockService {

    private static final String LOCK_PATH = "/LOCK_SERVICE";

    private CountDownLatch countDown;

    @Autowired
    private ZkClient zkClient;

    @Override
    public boolean tryLock() {
        try {
            zkClient.createEphemeral(LOCK_PATH);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void unLock() {
        zkClient.delete(LOCK_PATH);
    }

    @Override
    public void waitLock() {
        IZkDataListener listener = new IZkDataListener(){
            @Override
            public void handleDataChange(String dataPath, Object data){}

            @Override
            public void handleDataDeleted(String dataPath){
                if (Objects.isNull(countDown)) {
                    return;
                }
                countDown.countDown();
            }
        };
        zkClient.subscribeDataChanges(LOCK_PATH, listener);

        if (zkClient.exists(LOCK_PATH)) {
            countDown = new CountDownLatch(1);
            try {
                countDown.await();
            } catch (InterruptedException ie){
                Thread.currentThread().interrupt();
                throw new RuntimeException("wait lock error:", ie);
            }
        }

        zkClient.unsubscribeDataChanges(LOCK_PATH, listener);
    }
}
