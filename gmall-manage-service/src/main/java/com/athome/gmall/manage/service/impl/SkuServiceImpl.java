package com.athome.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.athome.gmall.bean.PmsSkuAttrValue;
import com.athome.gmall.bean.PmsSkuImage;
import com.athome.gmall.bean.PmsSkuInfo;
import com.athome.gmall.bean.PmsSkuSaleAttrValue;
import com.athome.gmall.manage.mapper.SkuAttrValueMapper;
import com.athome.gmall.manage.mapper.SkuImageMapper;
import com.athome.gmall.manage.mapper.SkuMapper;
import com.athome.gmall.manage.mapper.SkuSaleAttrValueMapper;
import com.athome.gmall.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Override
    public PmsSkuInfo getSkuById(String skuId) {

        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = skuMapper.selectOne(pmsSkuInfo);
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = skuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);
        return skuInfo;
    }
}
