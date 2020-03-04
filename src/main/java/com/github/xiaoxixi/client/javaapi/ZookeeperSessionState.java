package com.github.xiaoxixi.client.javaapi;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import java.util.Objects;

/**
 * zookeeper session state
 * Connecting
 * connected
 * closed
 * auth failed
 */
public class ZookeeperSessionState {

    private final static String host = "192.168.18.105:2181";

    private final static String PATH = "/simple";

    @Test
    public void testSessionState() {
        try {
            ZooKeeper zooKeeper = new ZooKeeper(host, 30000, (event) -> {
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    System.out.println("===zookeeper connected.===");
                }
            });
            System.out.println(zooKeeper.getState());
            System.out.println(zooKeeper);
            if(!Objects.isNull(zooKeeper.exists(PATH, false))) {
                zooKeeper.delete(PATH, -1);
                System.out.println("=====path " + PATH + " was deleted=====");
            }
            zooKeeper.create(PATH, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
