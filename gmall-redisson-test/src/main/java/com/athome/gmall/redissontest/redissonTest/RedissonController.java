package com.athome.gmall.redissontest.redissonTest;


import com.alibaba.dubbo.common.utils.StringUtils;
import com.athome.gmall.util.GmallRedissonConfig;
import com.athome.gmall.util.RedisUtil;
import org.apache.zookeeper.server.Request;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;

@Controller
@ResponseBody
public class RedissonController {
    @Autowired
    GmallRedissonConfig gmallRedissonConfig;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    RedisUtil redisUtil;

    @RequestMapping("testRedisson")
    public String testRedisson(HttpServletRequest request) {

        String IP = request.getRemoteAddr();
        RLock lock = redissonClient.getLock("anyLock");
        Jedis jedis = redisUtil.getJedis();

        String v = jedis.get("k");
        if (StringUtils.isBlank(v)) {
            v = "1";

        }
        System.out.println(v+"ip:"+IP);
        jedis.set("k", (Integer.parseInt(v) + 1) + "");
        jedis.close();

        //lock.lock();


        return "succeess";
    }

}
