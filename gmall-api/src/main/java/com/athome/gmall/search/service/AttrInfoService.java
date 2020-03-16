package com.athome.gmall.search.service;

import com.athome.gmall.bean.PmsBaseAttrInfo;
import com.athome.gmall.bean.PmsBaseSaleAttr;

import java.util.List;

public interface AttrInfoService  {
    List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseSaleAttr> getbaseSaleAttrList();
}
