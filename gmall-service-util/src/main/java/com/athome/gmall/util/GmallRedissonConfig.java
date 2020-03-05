package com.athome.gmall.util;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GmallRedissonConfig {
    //冒号后面是默认值
    @Value("${spring.redis.host:0}")
    private String host;
    @Value("${spring.redis.port:6379}")
    private int port;
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+host+":"+port);
        RedissonClient redissonClient = Redisson.create(config);
        return  redissonClient;


    }

}
