package com.github.xiaoxixi.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

public class OperatorTest {

    /**
     * ExponentialBackoffRetry:重试一定次数，每次重试时间一次递增
     * RetryNTimes:重试N次
     * RetryOneTime:重试一次
     * RetryUntilElapsed:重试一定时间
     */
    private static final RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    private static final String SERVER = "192.168.1.99:2181";
    private static final int SESSION_TIME_OUT = 30000;
    private static final int CONNECT_TIME_OUT = 30000;

    private CuratorFramework client = null;

    @Before
    public void testBefore(){
        client =
                CuratorFrameworkFactory.newClient(SERVER, SESSION_TIME_OUT, CONNECT_TIME_OUT, retryPolicy);
        client.start();
    }

    @Test
    public void testCreate() throws Exception {
        // 节点创建
        client.create()
                .forPath("/curator", "curator path test".getBytes());
        // 永久有序节点
        client.create()
                .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                .forPath("/curator_seq", "seg data".getBytes());
        // 临时节点
        client.create()
                .withMode(CreateMode.EPHEMERAL)
                .forPath("/curator_tmp", "tmp".getBytes());

        // 临时有序节点
        client.create()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath("/curator_tmp_seq", "tmp_seq".getBytes());

    }


    @Test
    public void testExists() throws Exception {
        Stat stat1 = client.checkExists().forPath("/curator");
        Stat stat2 = client.checkExists().forPath("/curator2");

        System.out.println("/curator is exists:" + (stat1 != null));
        System.out.println("/curator2 is exists:" + (stat2 != null));
    }


    /**
     * 异步设置节点数据- 监听方式
     * @throws Exception
     */
    @Test
    public void testAsyncSetData() throws Exception {
        CuratorListener listener = (client, event) -> {
            System.out.println("the path:" + event.getPath());
        };

        // 添加监听器
        client.getCuratorListenable().addListener(listener);
        // 异步设置某个节点数据
        client.setData().inBackground().forPath("/curator", "new data".getBytes());
        // 防止单元测试结束而看不到异步执行结果
        Thread.sleep(5000);
    }

    /**
     * 异步设置节点数据 - 回调方式
     * @throws Exception
     */
    @Test
    public void testCallbackAsyncSetData() throws Exception {
        BackgroundCallback callback = (client, event) -> {
            System.out.println("the path with callback:" + event.getPath());
        };

        client.setData().inBackground(callback).forPath("/curator", "call back data".getBytes());
        Thread.sleep(5000);
    }

    @Test
    public void testDelete() throws Exception{
        client.create().creatingParentsIfNeeded().forPath("/curator/key1", "curator key1".getBytes());
        client.create().creatingParentsIfNeeded().forPath("/curator/key2", "curator key2".getBytes());
        // 删除节点
        client.delete().forPath("/curator/key2");
        // 级联删除节点
        client.delete().guaranteed().deletingChildrenIfNeeded().forPath("/curator");
    }

    /**
     * 事务
     * @throws Exception
     */
    @Test
    public void testTransaction() throws Exception {
        Collection<CuratorTransactionResult> results = client.inTransaction()
                .create().forPath("/curator", "transaction data".getBytes())
                .and()
                .create().forPath("/curator/transaction1", "transaction data1".getBytes())
                .and()
                .create().forPath("/curator/transaction2", "transaction data2".getBytes())
                .and()
                .setData().forPath("/curator/transaction2", "transaction data3".getBytes())
                .and().commit();
        for (CuratorTransactionResult result : results) {
            System.out.println(result.getForPath() +":"+ result.getType());
        }
        Thread.sleep(1000);
    }

    /**
     * 测试事务失败，回滚
     * @throws Exception
     */
    @Test
    public void testTransactionFail() throws Exception {
        Collection<CuratorTransactionResult> results = client.inTransaction()
                .create().forPath("/curator_fail", "transaction data".getBytes())
                .and()
                .create().forPath("/curator_fail/transaction1", "transaction data1".getBytes())
                .and()
                // 删除时不能有子节点
                .delete().forPath("/curator_fail")
                .and().commit();
        for (CuratorTransactionResult result : results) {
            System.out.println(result.getForPath() +":"+ result.getType());
        }
        Thread.sleep(1000);
    }

}
