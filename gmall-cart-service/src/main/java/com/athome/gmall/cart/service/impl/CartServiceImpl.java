package com.athome.gmall.cart.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.athome.gmall.bean.OmsCartItem;
import com.athome.gmall.cart.mapper.OmsCartItemMapper;
import com.athome.gmall.service.CartService;
import com.athome.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.MapKey;
import java.util.*;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    OmsCartItemMapper omsCartItemMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public OmsCartItem ifCartExistByUser(String memberId, String skuId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        OmsCartItem omsCartItem1 = omsCartItemMapper.selectOne(omsCartItem);

        return omsCartItem1;
    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {
        if (StringUtils.isNotEmpty(omsCartItem.getMemberId())) {
            //避免添加空值
            omsCartItemMapper.insertSelective(omsCartItem);
        }


    }

    @Override
    public void updateCart(OmsCartItem omsCartItemFromDb) {

        Example e = new Example(OmsCartItem.class);
        e.createCriteria().andEqualTo("id", omsCartItemFromDb.getId());
        omsCartItemMapper.updateByExampleSelective(omsCartItemFromDb, e);

    }

    @Override
    public void flushCartCache(String memberId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        List<OmsCartItem> omsCartItems = omsCartItemMapper.select(omsCartItem);

        //同步到缓存中

        Jedis jedis = redisUtil.getJedis();
        //同步缓存的时候，要设计缓存结构
        //结构要求：1、存储的是购物车集合
        //2、键：用户id
        //3、购物车缓存找那个的某一个购物车数据的更新
        //使用Redis中的hash结构
//        MapKey
//                key value
//                key value
//                key value
//用法：hset 主key 从key 值
//        hset 主key 从key1 值1
//        hset 主key 从key2 值2
//    指定取值    hget 主key 从key
//     取所有值   hvals 主key


        try {


            Map<String, String> map = new HashMap<>();
            for (OmsCartItem cartItem : omsCartItems) {
                cartItem.setTotalPrice(cartItem.getPrice().multiply(cartItem.getQuantity()));
                map.put(cartItem.getProductSkuId(), JSON.toJSONString(cartItem));

            }
            //使用hash进行存储，方便查询和修改用户购物车集合中的某一个单独的购物车对象
            //一次性将hashmap全部放进去,两个参数，第一个是主key，第二个是要存储的map
            //key的设计遵循企业原则，对象名：id：说明--->User:memberId:cart
            jedis.del("user:" + memberId + ":cart");
            jedis.hmset("user:" + memberId + ":cart", map);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }

    }

    @Override
    //service层默认传递的参数都是合法的，是否为空在controller中判断
    public List<OmsCartItem> cartList(String userId) {
        Jedis jedis = null;

        List<OmsCartItem> omsCartItems = new ArrayList<>();
        try {

            jedis = redisUtil.getJedis();
            List<String> hvals = jedis.hvals("user:" + userId + ":cart");

            if (hvals!=null&&hvals.size()!=0){
                for (String hval : hvals) {
                    OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                    omsCartItems.add(omsCartItem);


                }

            }else{
                //缓存中没有，查询数据库
                String token = UUID.randomUUID().toString();
                String OK = jedis.set("userId:"+userId+":lock",token,"nx","px",60*10000);
                if (StringUtils.isNotEmpty(OK)&&OK.equals("OK")){
                    //拿到了分布式锁，可以查询数据库
                    //String test = "";
                    OmsCartItem omsCartItem = new OmsCartItem();
                    omsCartItem.setMemberId(userId);
                    omsCartItems = omsCartItemMapper.select(omsCartItem);

                    if (omsCartItems!=null){
                        //如果拿到的数据不为空，更新缓存
                          Map<String,String> map = new HashMap<>();
                        for (OmsCartItem cartItem : omsCartItems) {
                            cartItem.setTotalPrice(cartItem.getPrice().multiply(cartItem.getQuantity()));
                            map.put(cartItem.getProductSkuId(),JSON.toJSONString(cartItem));
                        }
                        jedis.del("user:" + userId + ":cart");
                        jedis.hmset("user:" + userId + ":cart", map);

                    }else{
                        //如果为空，存入一个空，防止缓存穿透
                        Map<String,String> map = new HashMap<>();
                        map.put("","");
                        jedis.hmset("user:" + userId + ":cart", map);
                        return null;
                    }
                    //为了防止一种情况，就是当下面的判断已经通过了，刚刚要删除的时候，这个key恰好失效了，这个时候删除的又将是别人的key
                    //所以引入lua脚本，在查询到的时候，直接删除，防止高并发下的意外的发生
                    String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return " +
                            "redis.call('del',KEYS[1]) else return 0 end";
                    jedis.eval(script, Collections.singletonList("userId:"+userId+":lock"),
                            Collections.singletonList(token));
                }else{
                    //没有拿到，自旋

                    return cartList(userId);

                }
            }



        } catch (Exception e) {
            //处理异常，记录系统日志
            e.getMessage();

            e.printStackTrace();
            return null;
        } finally {
            jedis.close();
        }

        return omsCartItems;
    }

    @Override
    public void checkCart(OmsCartItem omsCartItem) {
        Example e = new Example(OmsCartItem.class);
        e.createCriteria().andEqualTo("memberId",omsCartItem.getMemberId()).andEqualTo("productSkuId",omsCartItem.getProductSkuId());
        omsCartItemMapper.updateByExampleSelective(omsCartItem,e);

        //缓存同步
        flushCartCache(omsCartItem.getMemberId());
    }
}
