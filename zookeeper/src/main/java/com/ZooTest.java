package com;

import com.config.BeanContext;
import com.lock.ZkLock;
import lombok.SneakyThrows;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@MapperScan("com.dao")
public class ZooTest extends Thread{


    private static ZkLock zkLock;
    public static void main(String[] args) {
        SpringApplication.run(ZooTest.class);


//        for(int i = 0; i < 10 ; i++){
//            ZooTest thread = new ZooTest();
//            thread.start();
//        }

    }

    @SneakyThrows
    @Override
    public void run(){
        zkLock = BeanContext.getBean(ZkLock.class);
        while(true){
            zkLock.lock();
            System.out.println(Thread.currentThread().getName() + "获得锁");
            TimeUnit.MILLISECONDS.sleep(100);
            zkLock.unLock();
            System.out.println(Thread.currentThread().getName() + "释放锁");
            TimeUnit.MILLISECONDS.sleep(50);        //给其他线程抢锁的机会
        }
    }

}
