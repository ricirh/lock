package com;


import com.config.BeanContext;
import com.lock.RedisLock;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class RedisTest extends Thread{

    private BeanContext beanContext;
    RedisLock redisLock;
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args){
        SpringApplication.run(RedisTest.class);
        for (int i=0;i<1000;i++){
            RedisTest t = new RedisTest();
            t.start();
        }
        System.out.println("结束");
    }

    @SneakyThrows
    @Override
    public void run() {
        RedisLock redisLock = BeanContext.getBean(RedisLock.class);
        jdbcTemplate = BeanContext.getBean(JdbcTemplate.class);
        final int[] cur = {0};
        while(true){
            redisLock.lock("lock");
            String sql = "select count from redistest";
            jdbcTemplate.query(sql, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet resultSet) throws SQLException {
                    cur[0] = resultSet.getInt("count");
                }
            });
            if(cur[0] > 0){
                sql = "update redistest set count=count-1";
                jdbcTemplate.update(sql);
            }else{
                break;
            }
            //TimeUnit.MILLISECONDS.sleep(100);
            redisLock.unLock("lock");
            TimeUnit.MILLISECONDS.sleep(50);        //给其他线程抢锁的机会
        }
    }
}
