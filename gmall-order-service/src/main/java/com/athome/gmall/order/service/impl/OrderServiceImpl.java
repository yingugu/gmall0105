package com.athome.gmall.order.service.impl;/**
 * @package_name com.athome.gmall.order.service.impl
 * @version 0.1.0
 * @Description
 * @author yinping
 * @create 2020-04-03 16:09
 * @since 0.1.0
 **/

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.athome.gmall.bean.OmsOrder;
import com.athome.gmall.bean.OmsOrderItem;
import com.athome.gmall.order.mapper.OmsOrderItemMapper;
import com.athome.gmall.order.mapper.OmsOrderMapper;
import com.athome.gmall.service.OrderService;
import com.athome.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.UUID;

/**
 * <h3>gmall0105</h3>
 * <p>订单服务实现</p>
 * @author : yinping
 * @date : 2020-04-03 16:09
 **/
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OmsOrderMapper omsOrderMapper;
    @Autowired
    OmsOrderItemMapper omsOrderItemMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public String checkTradeCode(String memberId,String tradeCode) {
        Jedis jedis = null;

        try {
            jedis = redisUtil.getJedis();
            String tradeKey = "user:"+memberId+":tradeCode";
            String tradeCodeFromCatch = jedis.get(tradeKey);
            if(StringUtils.isNotEmpty(tradeCodeFromCatch)&&tradeCodeFromCatch.equals(tradeCode)){
                //检查成功以后把信息删除掉
                jedis.del(tradeKey);
                return "success";
            }else{
                return "fail";
            }

        } finally {
            jedis.close();
        }

    }

    @Override
    public String genTradeCode(String memberId) {
        //根据memberId生成交易码，在提交订单时检查交易码，然后销毁。
        //存在缓存中，结构为--> user:memberId:tradeCode：随机字符串
        Jedis jedis = null;
        String tradeCode = null;
        try {
            jedis = redisUtil.getJedis();

            String tradeKey = "user:"+memberId+":tradeCode";
            tradeCode = UUID.randomUUID().toString();

            jedis.setex(tradeKey,60*15,tradeCode);


        } finally {
            jedis.close();
        }


        return tradeCode;
    }
}
