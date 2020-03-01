package com.github.xiaoxixi;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZkConfig {

    @Value("${zookeeper.host}")
    private String zkHost;

    @Value("${zookeeper.connect.timeout}")
    private Integer zkConnectTimeout;

    @Value("${zookeeper.session.timeout}")
    private Integer zkSessionTimeout;

    @Bean
    public ZkClient zkClient() {
        return new ZkClient(zkHost, zkConnectTimeout, zkSessionTimeout);
    }

}
