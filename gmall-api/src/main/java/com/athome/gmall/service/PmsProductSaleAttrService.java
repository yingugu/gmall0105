package com.athome.gmall.service;

import com.athome.gmall.bean.PmsProductSaleAttr;

import java.io.Serializable;
import java.util.List;

public interface PmsProductSaleAttrService {
    List<PmsProductSaleAttr> getSaleAttrList(String spuId);
}
