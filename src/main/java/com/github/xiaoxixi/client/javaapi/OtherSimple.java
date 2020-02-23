package com.github.xiaoxixi.client.javaapi;

import org.apache.zookeeper.*;
import org.springframework.util.StringUtils;
import java.util.concurrent.CountDownLatch;

public class OtherSimple implements Watcher {

    private final CountDownLatch waiter = new CountDownLatch(1);

    private static final String host = "192.168.1.99:2181";

    private ZooKeeper zookeeper = null;

    public void releaseConnection(){
        try {
            if (null != zookeeper) {
                zookeeper.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createConnection(){
        this.releaseConnection();
        try {
            zookeeper = new ZooKeeper(host, 3000, this);
            waiter.wait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readData(String path) {
        try {
            return new String(zookeeper.getData(path, false, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public Boolean createPath(String path, String value) {
        try{
            String actualPath = zookeeper.create(path,
                    value.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
            return !StringUtils.isEmpty(actualPath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void writeData(String path, String value) {
        try {
            // version 为-1则表示不做版本验证，类似乐观锁
            zookeeper.setData(path, value.getBytes(), -1);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletePath(String path) {
        try {
            zookeeper.delete(path, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void process(WatchedEvent event) {
        System.out.println("receive event notify:" + event.getState());
        if(event.getState() == Event.KeeperState.SyncConnected) {
            waiter.countDown();
        }
    }

    public static void main(String[] args) {

        OtherSimple simple = new OtherSimple();
        String path = "/other-simple";

        simple.createConnection();
        simple.createPath(path, "other simple.");
        String pathValue = simple.readData(path);
        System.out.println("first read result:" + pathValue);
        simple.writeData(path, "other simple 2.");

        String pathValue2 = simple.readData(path);
        System.out.println("second read result:" + pathValue2);

        simple.deletePath(path);
        System.out.println("deleted path:" + path);
    }
}
