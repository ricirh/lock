package com.lock;

import com.config.BeanContext;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLock implements Runnable {
    private static int OUTTIME = 200;
    private static int SLEEPTIME = 100;
    private static boolean PROTECTED = true;
    private static String CURKEY = "";
    private static int count = 0;
    @Autowired
    RedisTemplate<String, Long> redisTemplate;

    public RedisLock(){
        Thread thread = new Thread(this);
        thread.start();
        System.out.println("init");
    }

    @SneakyThrows
    public boolean lock(String lockValue) {
        boolean flag = false;               //是否获取到锁

        while(!flag){
            flag = redisTemplate.opsForValue().setIfAbsent(lockValue,System.currentTimeMillis());
            if(flag){
                CURKEY = lockValue;
                //System.out.println(Thread.currentThread().getName() + "获得锁");
                if(++ count  > 1){
                    System.out.println("锁被第" + count + "次取用");
                }
                return true;
            }
            Long lockT = redisTemplate.opsForValue().get(lockValue);
            Long curT = System.currentTimeMillis();
            if(null == lockT || curT - lockT > OUTTIME){                                             //锁过期
                try {
                    if(lockT.equals(redisTemplate.opsForValue().getAndSet(lockValue,System.currentTimeMillis()))){            //判断锁是否已经被取用
                        redisTemplate.opsForValue().set(lockValue,System.currentTimeMillis());
                        //System.out.println(Thread.currentThread().getName() + "获得锁");
                        count--;
                        flag = true;
                        break;
                    }
                }catch (Exception e){
                    //System.out.println("锁已经被释放");
                }

            }
            TimeUnit.MILLISECONDS.sleep(SLEEPTIME);
        }
        if(++ count  > 1){
            System.out.println("锁被地" + count + "次取用");
        }
        CURKEY = lockValue;
        return true;
    }

    public boolean unLock(String lockValue){
        if(null != redisTemplate.opsForValue().get(lockValue)) {
            count--;
            redisTemplate.delete(lockValue);
            CURKEY = "";
            //System.out.println(Thread.currentThread().getName() + "释放锁");
            return true;
        }else{
            return false;
        }
    }


    @SneakyThrows
    @Override
    public void run() {
        redisTemplate = BeanContext.getBean("lockRedisTemplate");
        while (PROTECTED){
            Long lockT = redisTemplate.opsForValue().get(CURKEY);
            if(null !=  lockT){
                redisTemplate.opsForValue().setIfPresent(CURKEY,System.currentTimeMillis());
                //System.out.println("延时");
            }
            TimeUnit.MILLISECONDS.sleep(SLEEPTIME);
        }
    }
}
