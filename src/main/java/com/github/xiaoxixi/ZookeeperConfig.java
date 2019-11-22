package com.github.xiaoxixi;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ZookeeperConfig {

    @Value("${zookeeper.host}")
    private String zookeeperHost;

    @Value("${zookeeper.timeout}")
    private int zookeeperTimeout;

    @Bean
    @Scope("prototype")
    public ZkClient zkClient(){
        return new ZkClient(new ZkConnection(zookeeperHost, zookeeperTimeout));
    }
}
