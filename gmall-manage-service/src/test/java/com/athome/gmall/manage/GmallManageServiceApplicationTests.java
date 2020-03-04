package com.athome.gmall.manage;

import com.athome.gmall.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageServiceApplicationTests {

    @Autowired
    RedisUtil redisUtil;

    @Test
    public void contextLoads() {
//        Jedis jedis = redisUtil.getJedis();
//        System.out.println(jedis);

        System.out.println("线程开始运行"+Thread.currentThread().getName());
        try {
            Thread.sleep(3000);
            System.out.println("睡醒了"+Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("过去了"+Thread.currentThread().getName());
        contextLoads();
    }

}
