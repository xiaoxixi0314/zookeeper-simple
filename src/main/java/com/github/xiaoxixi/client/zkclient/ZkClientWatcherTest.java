package com.github.xiaoxixi.client.zkclient;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ZkClientWatcherTest {

    private ZkClient zkClient;
    private final String host = "192.168.1.201:2181";


    @Before
    public void initZkClient(){
        zkClient = new ZkClient(new ZkConnection(host), 10000);
    }

    @Test
    public void watchDataChange() throws Exception {

        zkClient.createPersistent("/super", "1233");
        zkClient.subscribeDataChanges("/super", new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                System.out.println(s +" has changed to " + o);
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                System.out.println("the node " + s + " has deleted.");
            }
        });
        Thread.sleep(1000);
        zkClient.writeData("/super", "33333");
        Thread.sleep(1000);
        zkClient.delete("/super");
        Thread.sleep(2000);
    }

    @Test
    public void watchChildrenChange() throws Exception{
        zkClient.createPersistent("/super/c1", true);
        zkClient.subscribeChildChanges("/super", new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
                System.out.println("parent path:" + parentPath);
                System.out.println("current childs:" + currentChildren);
            }
        });

        Thread.sleep(1000);
        zkClient.createPersistent("/super/c2");
        Thread.sleep(1000);
        zkClient.createPersistent("/super/c3");
        Thread.sleep(1000);
        zkClient.delete("/super/c2");
        Thread.sleep(1000);
        zkClient.deleteRecursive("/super");
        Thread.sleep(1000);
    }

    @After
    public void close(){
        zkClient.close();
    }
}
