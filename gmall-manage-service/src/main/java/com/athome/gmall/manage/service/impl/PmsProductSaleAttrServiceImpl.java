package com.athome.gmall.manage.service.impl;

import com.athome.gmall.bean.PmsProductSaleAttr;
import com.athome.gmall.manage.mapper.PmsProductSaleAttrMapper;
import com.athome.gmall.service.PmsProductSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PmsProductSaleAttrServiceImpl implements PmsProductSaleAttrService {
    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Override
    public List<PmsProductSaleAttr> getSaleAttrList(String spuId) {
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);

        return pmsProductSaleAttrList;
    }
}
