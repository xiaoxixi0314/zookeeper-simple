package com.github.xiaoxixi;

import com.github.xiaoxixi.lock.LockService;
import com.github.xiaoxixi.lock.enums.LockStrategyEnum;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class BeanConfig {

    @Resource(name = "mysqlLockService")
    private LockService mysqlLockService;

    @Resource(name = "zkLockService")
    private LockService zkLockService;

    @Resource(name = "zkSeqLockService")
    private LockService zkSeqLockService;

    @Bean
    public Map<LockStrategyEnum, LockService> lockServiceMap(){
        Map<LockStrategyEnum, LockService> map = new HashMap<>();
        map.put(LockStrategyEnum.MYSQL, mysqlLockService);
        map.put(LockStrategyEnum.ZK, zkLockService);
        map.put(LockStrategyEnum.ZK_SEQ, zkSeqLockService);
        return map;
    }

}
