package com.github.xiaoxixi.client.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ZkClientOperatorTest {

    private final static  String host = "192.168.1.99:2181";

    private ZkClient zkClient = null;

    @Before
    public void initZkClient() {
        zkClient = new ZkClient(new ZkConnection(host), 10000);
    }

    @Test
    public void testZkClientOperate() throws Exception{
        // 创建临时节点
        zkClient.createEphemeral("/temp");

        // 创建级联节点，ture的话会创建父节点
        zkClient.createPersistent("/super1/c1", true);
        Thread.sleep(2000);
        zkClient.delete("/temp");
        // 递归删除
        boolean delResult = zkClient.deleteRecursive("/super1");
        System.out.println(delResult);


        zkClient.createPersistent("/super1", "1234");
        zkClient.createPersistent("/super1/c1", "c1 content");
        zkClient.createPersistent("/super1/c2", "c2 content");

        List<String> childs = zkClient.getChildren("/super1");
        for (String child : childs) {
            String fullPath = "/super1/" + child;
            System.out.println("path is:" + fullPath + ", value is:"+zkClient.readData(fullPath));
        }

        zkClient.writeData("/super1/c1", "new c1 content");
        System.out.println("/super1/c1 latest content:" + zkClient.readData("/super1/c1"));

        zkClient.deleteRecursive("/super1");

    }

    @After
    public void afterTest(){
        zkClient.close();
    }
}
