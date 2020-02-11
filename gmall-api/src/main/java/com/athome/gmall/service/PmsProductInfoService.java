package com.athome.gmall.service;

import com.athome.gmall.bean.PmsProductInfo;

import java.util.List;

public interface PmsProductInfoService {
    List<PmsProductInfo> getspuList(String catalog3Id);
}
