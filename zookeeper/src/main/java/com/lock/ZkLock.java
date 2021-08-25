package com.lock;

import lombok.SneakyThrows;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
public class ZkLock {
    private static final String CONNECTION="127.0.0.1:2181";
    protected ZkClient zkClient = new ZkClient(CONNECTION);
    private static final String PARENT = "/lock";
    private static final String PREFIX = "/lock/";
    private String curLock = "";
    @SneakyThrows
    public boolean lock(){
        String order = zkClient.create(PREFIX + "order-","".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        List<String> orders = zkClient.getChildren(PARENT);
        orders.sort(String::compareTo);
        order = order.replace(PREFIX,"");
        int index = 0;
        for(int i = 0; i < orders.size(); i++){
            if(orders.get(i).equals(order)){
                break;
            }
            index++;
        }
        if(0 == index){
            curLock = order;
            return true;
        }else{
            String preOrder = orders.get(index - 1);
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            zkClient.subscribeDataChanges(PREFIX + preOrder, new IZkDataListener() {
                @Override
                public void handleDataChange(String s, Object o) throws Exception {

                }

                @Override
                public void handleDataDeleted(String s) throws Exception {
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
        }
        curLock = order;
        return true;
    }

    public boolean unLock(){
        zkClient.delete(PREFIX + curLock);
        return true;
    }
}
