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

import java.util.List;

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
    public PmsSkuInfo getSkuById(String skuId) {
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
            pmsSkuInfo = getSkuByIdFromDb(skuId);
            //mysql查询结果存入redis
            if (pmsSkuInfo != null) {
                jedis.set(skuKey, JSON.toJSONString(pmsSkuInfo));
            } else {
                //s数据库中不存在该sku，为了防止缓存穿透  将null或者空字符串设置给redis,并设置一个较短过期时间
                jedis.setex("sku:" + skuId + ":info", 60 * 3, JSON.toJSONString(""));

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
