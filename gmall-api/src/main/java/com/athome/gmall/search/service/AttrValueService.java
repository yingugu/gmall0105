package com.athome.gmall.search.service;

import com.athome.gmall.bean.PmsBaseAttrValue;

import java.util.List;

public interface AttrValueService {
    List<PmsBaseAttrValue> getAttrValueList(String attrId);
}
