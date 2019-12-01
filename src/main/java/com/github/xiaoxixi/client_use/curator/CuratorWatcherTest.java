package com.github.xiaoxixi.client_use.curator;

import com.alibaba.fastjson.JSON;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;


public class CuratorWatcherTest {
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

    /**
     * 使用原生java api的方式监听
     * 仅仅能监控本节点的数据修改，删除操作并且只能监听一次
     */
    @Test
    public void testWatcherWithJavaApi() throws Exception {
        if (client.checkExists().forPath("/test") != null) {
            client.delete().guaranteed().deletingChildrenIfNeeded().forPath("/test");
        }
        client.create().withMode(CreateMode.PERSISTENT).forPath("/test", "test".getBytes());
        client.getData().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    System.out.println("原生api方式监听event:");
                    System.out.println(event);
                    byte[] data = client.getData().forPath("/test");
                    System.out.println("changed data:" + new String(data));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).forPath("/test");
        // 只能监听到此次变化
        client.setData().forPath("/test", "new test".getBytes());
        Thread.sleep(2000);
        // 本次变化监听不到
        client.setData().forPath("/test", "new test1".getBytes());
        Thread.sleep(2000);
    }


    /**
     * path cache 监听一个路径下：孩子节点的创建，删除，节点数据更新
     * 产生的时间会传递给PathChildrenCacheListener
     */
    @Test
    public void testPathCacheListener() throws Exception {
        Stat stat = client.checkExists().forPath("/test");
        if (stat != null) {
            System.out.println(JSON.toJSONString(stat));
            client.delete().deletingChildrenIfNeeded().forPath("/test");
            stat = client.checkExists().forPath("/test");
            System.out.println(JSON.toJSONString(stat));
        }

        PathChildrenCache childrenCache = new PathChildrenCache(client, "/test", true);
        PathChildrenCacheListener listener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println("start receive event ....");
                ChildData  childData = event.getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("CHILD ADDED, PATH:" + childData.getPath() + ",DATA:" + new String(childData.getData()));
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD REMOVED, PATH:" + childData.getPath() + ",DATA:" + new String(childData.getData()));
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD UPDATED, PATH:" + childData.getPath() + ",DATA:" + new String(childData.getData()));
                        break;
                    case INITIALIZED:
                        System.out.println("CHILD INITIALIZED, PATH:" + childData.getPath() + ",DATA:" + new String(childData.getData()));
                        break;
                    default:
                        break;
                }
            }
        };
        childrenCache.getListenable().addListener(listener);
        System.out.println("register zk watcher success....");
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        client.create().forPath("/test", "add path".getBytes());
        Thread.sleep(2000);
        client.create().forPath("/test/child1", "child 1".getBytes());
        Thread.sleep(2000);
        client.setData().forPath("/test/child1", "new child1".getBytes());
        Thread.sleep(2000);
        client.delete().forPath("/test/child1");
        Thread.sleep(2000);
        client.delete().forPath("/test");
        Thread.sleep(2000);
    }


    /**
     * 监听本节点的变化情况，可监听节点的修改，删除
     * @throws Exception
     */
    @Test
    public void testNodeCacheListener() throws Exception {
        Stat stat = client.checkExists().forPath("/test");
        if (stat != null) {
            System.out.println(JSON.toJSONString(stat));
            client.delete().deletingChildrenIfNeeded().forPath("/test");
        }

        NodeCache nodeCache = new NodeCache(client, "/test", false);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("the test node is change and result is:");
                ChildData data = nodeCache.getCurrentData();
                System.out.println("the path:" + data.getPath());
                System.out.println("the data:" + new String(data.getData()));
                System.out.println("the stat:" + JSON.toJSONString(data.getStat()));
                System.out.println("=========================");
            }
        });
        nodeCache.start();

        client.create().withMode(CreateMode.PERSISTENT).forPath("/test", "hello test".getBytes());
        Thread.sleep(2000);
        client.setData().forPath("/test", "hello test2".getBytes());
        Thread.sleep(2000);
        client.delete().forPath("/test");
        Thread.sleep(2000);
    }


    /**
     * TreeCacheListener 可以监听指定节点和节点下所有节点的变化
     * @throws Exception
     */
    @Test
    public void testTreeCacheListener() throws Exception {
        Stat stat = client.checkExists().forPath("/test");
        if (stat != null) {
            System.out.println(JSON.toJSONString(stat));
            client.delete().deletingChildrenIfNeeded().forPath("/test");
        }

        TreeCache treeCache = new TreeCache(client, "/test");
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                ChildData data = treeCacheEvent.getData();
                if (data == null) {
                    System.out.println("the data is null, event type:" + treeCacheEvent.getType());
                    return;
                }
                switch (treeCacheEvent.getType()) {
                    case NODE_ADDED:
                        System.out.println("NODE ADDED:" + data.getPath()+ ", THE VALUE:" + new String(data.getData()));
                        System.out.println("==============================");
                        break;
                    case NODE_UPDATED:
                        System.out.println("NODE UPDATED:" + data.getPath()+ ", THE VALUE:" + new String(data.getData()));
                        System.out.println("==============================");
                        break;
                    case NODE_REMOVED:
                        System.out.println("NODE REMOVED:" + data.getPath()+ ", THE VALUE:" + new String(data.getData()));
                        System.out.println("==============================");
                        break;
                    default:
                        break;
                }
            }
        });
        treeCache.start();

        client.create().forPath("/test", "hello test".getBytes());
        Thread.sleep(2000);
        client.create().forPath("/test/key1", "hello key1".getBytes());
        Thread.sleep(2000);
        client.create().forPath("/test/key2", "hello key2".getBytes());
        Thread.sleep(2000);
        client.create().forPath("/test/key1/child1", "hello child1".getBytes());
        Thread.sleep(2000);
        client.setData().forPath("/test/key1", "new key1".getBytes());
        Thread.sleep(2000);
        client.delete().forPath("/test/key1/child1");
        Thread.sleep(2000);
    }
}
