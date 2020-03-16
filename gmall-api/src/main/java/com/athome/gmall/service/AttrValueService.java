package com.athome.gmall.service;

import com.athome.gmall.bean.PmsBaseAttrValue;

import java.util.List;

public interface AttrValueService {
    List<PmsBaseAttrValue> getAttrValueList(String attrId);
}
