package com.github.xiaoxixi.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Service(value = "zkSeqLockService")
public class ZkLockServiceSequentialImpl extends AbstractLockService {

    @Autowired
    private ZkClient zkClient;

    private Map<Thread, String> beforePathMap = new ConcurrentHashMap<>();
    private Map<Thread, String> currentPathMap = new ConcurrentHashMap<>();

    private static final String LOCK_PATH = "/LOCK_PATH";

    @PostConstruct
    public void init() {
        if (!zkClient.exists(LOCK_PATH)) {
            zkClient.createPersistent(LOCK_PATH);
        }
    }

    @Override
    public boolean tryLock() {
        try {
            String currentPath;
            // 任何线程进来先创建一个临时有序节点
            if (!currentPathMap.containsKey(Thread.currentThread())) {
                currentPath = zkClient.createEphemeralSequential(LOCK_PATH + "/", "");
                currentPathMap.put(Thread.currentThread(), currentPath);
            } else {
                currentPath = currentPathMap.get(Thread.currentThread());
            }
            // 第一次获取锁
            List<String> children = zkClient.getChildren(LOCK_PATH);
            Collections.sort(children);
            if (Objects.equals(currentPath, LOCK_PATH + "/" + children.get(0))) {
                return true;
            }
            int currentPathIndex = Collections.binarySearch(children, currentPath.substring(LOCK_PATH.length() + 1));
            String beforePath = LOCK_PATH + "/" + children.get(currentPathIndex - 1);
            beforePathMap.putIfAbsent(Thread.currentThread(), beforePath);
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public void waitLock() {
        String beforePath = beforePathMap.get(Thread.currentThread());
        if (StringUtils.isEmpty(beforePath)) {
            return;
        }
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
        zkClient.subscribeDataChanges(beforePath, listener);

        if (zkClient.exists(beforePath)) {
            try {
                countDown.await();
            } catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }

        zkClient.unsubscribeDataChanges(beforePath, listener);
    }

    @Override
    public void unLock() {
        String currentPath = currentPathMap.get(Thread.currentThread());
        if (StringUtils.isNotEmpty(currentPath)) {
            zkClient.delete(currentPath);
            currentPathMap.remove(Thread.currentThread());
            beforePathMap.remove(Thread.currentThread());
        }
    }
}
