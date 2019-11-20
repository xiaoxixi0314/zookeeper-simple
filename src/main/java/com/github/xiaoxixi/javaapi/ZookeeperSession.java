package com.github.xiaoxixi.javaapi;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

public class ZookeeperSession {

    private final static  String host = "192.168.1.99:2181";

    @Test
    public void testSessionState() {
        try {
            ZooKeeper zooKeeper = new ZooKeeper(host, 30000, null);
            System.out.println(zooKeeper.getState());
            System.out.println(zooKeeper);
            Thread.sleep(2000);
            zooKeeper.create("/simple", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
