package com.athome.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.athome.gmall.bean.PmsProductImage;

import com.athome.gmall.bean.PmsProductInfo;
import com.athome.gmall.bean.PmsProductSaleAttr;
import com.athome.gmall.bean.PmsProductSaleAttrValue;
import com.athome.gmall.manage.mapper.PmsProductImageMapper;
import com.athome.gmall.manage.mapper.PmsProductInfoMapper;
import com.athome.gmall.manage.mapper.PmsProductSaleAttrMapper;
import com.athome.gmall.manage.mapper.PmsProductSaleAttrValueMapper;
import com.athome.gmall.service.PmsProductInfoService;
import com.sun.net.httpserver.Authenticator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class PmsProductInfoImpl implements PmsProductInfoService {
    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;
    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;
    @Autowired
    PmsProductImageMapper pmsProductImageMapper;
    @Override
    public List<PmsProductInfo> getspuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfos = pmsProductInfoMapper.select(pmsProductInfo);
        return pmsProductInfos;
    }

    @Override
    public List<PmsProductSaleAttr> getSaleAttrList(String spuId) {
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
        for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {
            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setProductId(spuId);
            pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId());
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValuesp = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValuesp);
        }

        return pmsProductSaleAttrs;
    }

    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {
        String revalue = null;

        try {
            pmsProductInfoMapper.insertSelective(pmsProductInfo);
            List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
            List<PmsProductImage> pmsProductImageList = pmsProductInfo.getSpuImageList();
            for (PmsProductImage pmsProductImage:pmsProductImageList){
                pmsProductImage.setProductId(pmsProductInfo.getId());
                pmsProductImageMapper.insertSelective(pmsProductImage);
            }
            for (int i=0;i<pmsProductSaleAttrList.size();i++){
                pmsProductSaleAttrList.get(i).setProductId(pmsProductInfo.getId());
                List<PmsProductSaleAttrValue> pmsProductSaleAttrValueList
                        =  pmsProductSaleAttrList.get(i).getSpuSaleAttrValueList();
                pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttrList.get(i));
                for (int j=0;j<pmsProductSaleAttrValueList.size();j++){
                    pmsProductSaleAttrValueList.get(j).setProductId(pmsProductInfo.getId());
                    pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValueList.get(j));
                }
            }
            revalue = "SUCCESS";

        } catch (Exception e) {
            e.printStackTrace();
            revalue="FALSE";
        }
        //return revalue;
    }
    @Override
    public List<PmsProductImage> getspuImageList(String spuId) {
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        List<PmsProductImage> pmsProductImages = pmsProductImageMapper.select(pmsProductImage);
        return pmsProductImages;
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId,String skuId) {
//    PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
//    pmsProductSaleAttr.setProductId(productId);
//        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
//        for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {
//            String saleAttrId = productSaleAttr.getSaleAttrId();
//            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
//            pmsProductSaleAttrValue.setSaleAttrId(saleAttrId);
//            pmsProductSaleAttrValue.setProductId(productId);
//            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
//            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
//        }
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.selectspuSaleAttrListCheckBySku(productId,skuId);

        return pmsProductSaleAttrs;
    }
}
