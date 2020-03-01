package com.athome.gmall.service;

import com.athome.gmall.bean.PmsProductImage;
import com.athome.gmall.bean.PmsProductInfo;
import com.athome.gmall.bean.PmsProductSaleAttr;

import java.util.List;

public interface PmsProductInfoService {
    List<PmsProductInfo> getspuList(String catalog3Id);
    List<PmsProductSaleAttr> getSaleAttrList(String spuId);
    void saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsProductImage> getspuImageList(String spuId);

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId);
}
