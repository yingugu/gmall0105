package com.athome.gmall.manage.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.athome.gmall.bean.PmsSkuAttrValue;
import com.athome.gmall.bean.PmsSkuImage;
import com.athome.gmall.bean.PmsSkuInfo;
import com.athome.gmall.bean.PmsSkuSaleAttrValue;
import com.athome.gmall.manage.mapper.SkuAttrValueMapper;
import com.athome.gmall.manage.mapper.SkuImageMapper;
import com.athome.gmall.manage.mapper.SkuMapper;
import com.athome.gmall.manage.mapper.SkuSaleAttrValueMapper;
import com.athome.gmall.service.SkuService;
import com.athome.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    SkuMapper skuMapper;
    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    SkuImageMapper skuImageMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        String a = "SUCCESS";
        try {
            //插入spuinfo
            int i = skuMapper.insertSelective(pmsSkuInfo);
            String skuId = pmsSkuInfo.getId();
            //插入平台属性关联
            List<PmsSkuAttrValue> pmsSkuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
            List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : pmsSkuAttrValueList) {
                pmsSkuAttrValue.setSkuId(skuId);
                skuAttrValueMapper.insertSelective(pmsSkuAttrValue);
            }
            //插入销售属性关联
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : pmsSkuSaleAttrValueList) {
                pmsSkuSaleAttrValue.setSkuId(skuId);
                skuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
            }
            //插入图片信息
            List<PmsSkuImage> pmsSkuImageList = pmsSkuInfo.getSkuImageList();
            for (PmsSkuImage pmsSkuImage : pmsSkuImageList) {
                pmsSkuImage.setSkuId(skuId);
                skuImageMapper.insertSelective(pmsSkuImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            a = "FALSE";
        }
        //return a;
    }

    public PmsSkuInfo getSkuByIdFromDb(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = skuMapper.selectOne(pmsSkuInfo);
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = skuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);
        return skuInfo;
    }

    //改成缓存的查询
    @Override
    public PmsSkuInfo getSkuById(String skuId, String IP) {
        System.out.println("线程名称1" + Thread.currentThread().getName() + IP);
//连接缓存，查询缓存,如果缓存中没有，查询mysql，mysql查询结果存入redis
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        //连接缓存
        Jedis jedis = redisUtil.getJedis();
        //查询缓存
        String skuKey = "sku:" + skuId + ":info";
        String skuJson = jedis.get(skuKey);
        if (StringUtils.isNotEmpty(skuJson)) {
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
        } else {
            //如果缓存中没有，查询mysql
            System.out.println("线程名称2" + Thread.currentThread().getName() + IP);
            //存在一种情况，一条线程拿到了锁，去访问DB，但是卡住了，锁已经过期了，后面一条线程拿到了锁，这个时候，前面的一条又复活了
            //删除了前面那条数据的锁，这个情况下，把kv中的v设置为一个token
            String token = UUID.randomUUID().toString();
            System.out.println(token);
            //设置分布式锁
            String OK = jedis.set("sku:" + skuId + ":lock", token, "nx", "px", 100 * 1000);
            //加过期时间是为了防止线程死掉之后，没有释放
            if (StringUtils.isNotEmpty(OK) && OK.equals("OK")) {
                System.out.println("OK" + Thread.currentThread().getName());
                //设置成功，有权在十秒的过期时间内访问数据库
                pmsSkuInfo = getSkuByIdFromDb(skuId);
                try {
                    Thread.sleep(1000 * 20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (pmsSkuInfo != null) {
                    //mysql查询结果存入redis
                    jedis.set(skuKey, JSON.toJSONString(pmsSkuInfo));
                } else {
                    //s数据库中不存在该sku，为了防止缓存穿透  将null或者空字符串设置给redis,并设置一个较短过期时间
                    jedis.setex("sku:" + skuId + ":info", 60 * 3, JSON.toJSONString(""));

                }
                System.out.println("线程名称3" + Thread.currentThread().getName() + IP);
                String lockToken = jedis.get("sku:" + skuId + ":lock");

                //为了防止一种情况，就是当下面的判断已经通过了，刚刚要删除的时候，这个key恰好失效了，这个时候删除的又将是别人的key
                //所以引入lua脚本，在查询到的时候，直接删除，防止高并发下的意外的发生
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return " +
                        "redis.call('del',KEYS[1]) else return 0 end";
                jedis.eval(script, Collections.singletonList("sku:" + skuId + ":lock"),
                        Collections.singletonList(lockToken));


//                //判断
//                if (StringUtils.isNotEmpty(lockToken) && lockToken.equals(token)) {
//                    //访问mysql后 将mysql的分布式锁释放
//                    jedis.del("sku:" + skuId + ":lock"); //用token确认删除的是自己的锁
                //}
            } else {
                //设置失败
                try {
                    System.out.println("sleep qian");
                    Thread.sleep(3000);
                    System.out.println("sleep" + Thread.currentThread().getName());
                    System.out.println("3");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("线程名称4" + Thread.currentThread().getName() + IP);
                ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();

                while (currentGroup.getParent() != null) {
                    // 返回此线程组的父线程组
                    currentGroup = currentGroup.getParent();
                }
                //此线程组中活动线程的估计数
                int noThreads = currentGroup.activeCount();
                Thread[] lstThreads = new Thread[noThreads];
                //把对此线程组中的所有活动子组的引用复制到指定数组中。
                currentGroup.enumerate(lstThreads);
                for (Thread thread : lstThreads) {
                    System.out.println("线程数量：" + noThreads + " 线程id：" + thread.getId() + " 线程名称：" + thread.getName() + " 线程状态：" + thread.getState());
                }

                return getSkuById(skuId, IP);


            }


        }
        jedis.close();
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> pmsSkuInfos = skuMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfos;
    }
}
