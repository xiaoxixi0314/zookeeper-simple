package com.github.xiaoxixi.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.CountDownLatch;

/**
 * 注意羊群效应
 */
@Service(value ="zkLockService")
public class ZkLockServiceImpl extends AbstractLockService {

    private static final String LOCK_PATH = "/lock_service";

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
        final CountDownLatch countDown = new CountDownLatch(1);
        IZkDataListener listener = new IZkDataListener(){
            @Override
            public void handleDataChange(String dataPath, Object data){
            }

            @Override
            public void handleDataDeleted(String dataPath){
                countDown.countDown();
            }
        };
        zkClient.subscribeDataChanges(LOCK_PATH, listener);

        if (zkClient.exists(LOCK_PATH)) {
            try {
                countDown.await();
            } catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }

        zkClient.unsubscribeDataChanges(LOCK_PATH, listener);
    }
}
