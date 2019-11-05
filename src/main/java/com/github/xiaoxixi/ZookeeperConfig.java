package com.github.xiaoxixi;

import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;

@Configuration
public class ZookeeperConfig {

    @Value("${zookeeper.host}")
    private String zookeeperHost;

    @Value("${zookeeper.timeout}")
    private int zookeeperTimeout;

    @Bean
    public ZooKeeper zooKeeper() throws IOException {
        return new ZooKeeper(zookeeperHost, zookeeperTimeout, null);
    }
}
