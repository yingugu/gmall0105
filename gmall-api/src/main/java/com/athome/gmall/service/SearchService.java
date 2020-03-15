package com.athome.gmall.service;

import com.athome.gmall.bean.PmsSearchParam;
import com.athome.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
