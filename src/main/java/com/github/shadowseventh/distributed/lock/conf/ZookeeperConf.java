package com.github.shadowseventh.distributed.lock.conf;

import com.github.shadowseventh.distributed.lock.prop.DisLockProperties;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;

/**
 *
 */
@Configuration
public class ZookeeperConf {

    @Autowired
    private DisLockProperties properties;

    @Bean
    public RetryPolicy zkRetry() {
        return new ExponentialBackoffRetry(1000, 3);
    }

    @Bean(initMethod = "start", destroyMethod = "close")
    public CuratorFramework zkClient(
            RetryPolicy zkRetry
    ) {
        return CuratorFrameworkFactory.newClient(
                properties.getAddresses(),
                zkRetry
        );
    }

    @Bean
    public ZookeeperLockRegistry zkLockRegistry(
            CuratorFramework zkClient
    ) {
        return new ZookeeperLockRegistry(zkClient);
    }

}
