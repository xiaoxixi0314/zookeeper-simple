package com.github.xiaoxixi.javaapi;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperSession {

    private final static  String host = "192.168.1.99:2181";

    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper = new ZooKeeper(host, 30000, null);
            System.out.println(zooKeeper.getState());
            System.out.println(zooKeeper);
            zooKeeper.create("/simple", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
