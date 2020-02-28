package com.athome.gmall.service;

import com.athome.gmall.bean.PmsSkuInfo;

public interface SkuService {
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuById(String skuId);
}
