package com.github.xiaoxixi.client.javaapi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZookeeperWatcher implements Watcher {

    private final CountDownLatch waiter = new CountDownLatch(1);

    private static final String HOST = "192.168.1.99:2181";

    private static final String PATH = "/test_watcher";

    private static final String CHILDREN_PATH = PATH + "/children";

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
            zookeeper = new ZooKeeper(HOST, 3000, this);
            waiter.wait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readData(String path, boolean needWatch) {
        try {
            return new String(zookeeper.getData(path, needWatch, null));
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

    public List<String> getChildren(String path, boolean needWatch) {
        List<String> childrens = new ArrayList<>();
        try {
            childrens = zookeeper.getChildren(path, needWatch);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return childrens;
    }

    public void writeData(String path, String value) {
        try {
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

    public boolean exists(String path, boolean needWatch) {
        Stat stat = null;
        try {
            stat =  zookeeper.exists(path, needWatch);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stat != null;
    }

    /**
     * 监听仅触发一次，
     * 如果收到了一个监听事件并且想再次监听，
     * 需重新再设置一次
     * @param event
     */
    @Override
    public void process(WatchedEvent event) {
        if(event.getState() == Event.KeeperState.SyncConnected) {
            System.out.println("zookeeper connected.");
            waiter.countDown();
        }
        // 调用exists方法设置监听
        if (event.getType() == Event.EventType.NodeCreated){
            System.out.println("== node created:" + event.getPath());
        }
        // 调用exists, getData, getChildren设置监听
        if (event.getType() == Event.EventType.NodeDeleted) {
            System.out.println("== node deleted:" + event.getPath());
        }

        // 调用getData设置监听
        if (event.getType() == Event.EventType.NodeChildrenChanged) {
            System.out.println("== node child changed:" + event.getPath());
        }
        // 调用getData设置监听
        if (event.getType() == Event.EventType.NodeDataChanged) {
            System.out.println("== node data changed:" + event.getPath());
        }

    }

    public static void main(String[] args) throws Exception{

        ZookeeperWatcher simple = new ZookeeperWatcher();


        simple.createConnection();

        if (simple.exists(CHILDREN_PATH, false)) {
            simple.deletePath(CHILDREN_PATH);
        }
        if (simple.exists(PATH, false)) {
            simple.deletePath(PATH);
        }
        // 设置监听created事件
        simple.exists(PATH, true);
        simple.createPath(PATH, "other simple.");
        simple.exists(PATH, true);

        String pathValue = simple.readData(PATH, true);
        System.out.println("path read result 1:" + pathValue);
        simple.writeData(PATH, "other simple 2.");
        String pathValue2 = simple.readData(PATH, true);
        System.out.println("path read result 2:" + pathValue2);
        simple.getChildren(PATH, true);

        simple.createPath(CHILDREN_PATH, "children");
        String childrenPathValue = simple.readData(CHILDREN_PATH, true);
        System.out.println("children path read result1:" + childrenPathValue);
        simple.writeData(PATH, "children 2.");
        String childrenPathValue2 = simple.readData(PATH, true);
        System.out.println("children path read result2:" + childrenPathValue2);

        simple.deletePath(CHILDREN_PATH);
        simple.deletePath(PATH);

        simple.releaseConnection();
    }
}
