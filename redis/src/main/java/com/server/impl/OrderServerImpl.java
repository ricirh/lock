package com.server.impl;

import com.dao.OrderDao;
import com.lock.RedisLock;
import com.server.OrderServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class OrderServerImpl implements OrderServer {

    @Autowired
    RedisLock redisLock;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    OrderDao orderDao;

    @Override
    public boolean createOrder() {

        if(redisTemplate.opsForValue().decrement("goodKey") >= 0){
            redisLock.lock("lock");
            orderDao.createOrder();
            redisLock.unLock("lock");
        }else {
            return  false;
        }
        return  true;
    }
}
