package com.athome.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.athome.gmall.bean.PmsProductInfo;
import com.athome.gmall.bean.PmsProductSaleAttr;
import com.athome.gmall.bean.PmsProductSaleAttrValue;
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
    @Override
    public List<PmsProductInfo> getspuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfos = pmsProductInfoMapper.select(pmsProductInfo);
        return pmsProductInfos;
    }

    @Override
    public String saveSpuInfo(PmsProductInfo pmsProductInfo) {
        String revalue = null;

        try {
            pmsProductInfoMapper.insertSelective(pmsProductInfo);
            List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductInfo.getPmsProductSaleAttrList();
            for (int i=0;i<pmsProductSaleAttrList.size();i++){
              List<PmsProductSaleAttrValue> pmsProductSaleAttrValueList
                      =  pmsProductSaleAttrList.get(i).getPmsProductSaleAttrValueList();
                pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttrList.get(i));
                for (int j=0;j<pmsProductSaleAttrValueList.size();j++){
                    pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValueList.get(j));
                }
            }
            revalue = "SUCCESS";

        } catch (Exception e) {
            e.printStackTrace();
            revalue="FALSE";
        }
        return revalue;
    }
}
