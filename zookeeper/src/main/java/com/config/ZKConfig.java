package com.config;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Configuration
public class ZKConfig {

        @Value("${zookeeper.address}")
        private    String connectString;

        @Value("${zookeeper.timeout}")
        private  int timeout;


        @Bean(name = "zkClient")
        public ZkClient zkClient() throws IOException {
            ZkClient zkClient = new ZkClient(connectString);
            return  zkClient;
        }


}
