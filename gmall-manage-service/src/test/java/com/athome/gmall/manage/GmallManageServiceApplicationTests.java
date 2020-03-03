package com.athome.gmall.manage;

import com.athome.gmall.util.RedisUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

@SpringBootTest
class GmallManageServiceApplicationTests {
@Autowired
    RedisUtil redisUtil;
@Test
    public void contextLoads(){
    //因为redisutil被整合到spring中，springboot在启动扫描的时候才会将bean注入，所以，当前的springboot启动类和Redis的两个配置类平级，需要将启动类向上提一级，保证启动类在启动的时候有权限向下扫描
    Jedis jedis = redisUtil.getJedis();
    System.out.println(jedis);

}
}
