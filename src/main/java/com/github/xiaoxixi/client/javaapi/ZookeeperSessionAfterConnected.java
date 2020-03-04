package com.github.xiaoxixi.client.javaapi;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class ZookeeperSessionAfterConnected {
    private final static  String host = "192.168.18.105:2181";

    private final static CountDownLatch countdown = new CountDownLatch(1);
    private final static String PATH = "/simple";

    @Test
    public void testSimpleWatcher() throws Exception{
        ZooKeeper zooKeeper = new ZooKeeper(host, 30000, (event) -> {
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                countdown.countDown();
                System.out.println("zookeeper connected.");
            }
        });

        countdown.await();
        System.out.println(zooKeeper.getState());
        if (!Objects.isNull(zooKeeper.exists(PATH, false))) {
            zooKeeper.setData(PATH, "value1".getBytes(), -1);
        } else {
            zooKeeper.create(PATH, "value".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        zooKeeper.close();

    }
}
