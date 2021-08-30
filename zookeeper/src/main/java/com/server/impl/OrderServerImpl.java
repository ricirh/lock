package com.server.impl;

import com.dao.OrderDao;
import com.lock.ZkLock;
import com.server.OrderServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class OrderServerImpl implements OrderServer {

    @Autowired
    ZkLock zkLock;

    @Autowired
    JedisPool jedisPool;

    @Autowired
    OrderDao orderDao;

    @Override
    public boolean createOrder() {

        Jedis jedis = jedisPool.getResource();
        if(jedis.decr("goodKey") >= 0){
            zkLock.lock();
            orderDao.createOrder();
            zkLock.unLock();
        }else {
            jedis.close();
            return  false;
        }
        jedis.close();
        return  true;
    }
}
