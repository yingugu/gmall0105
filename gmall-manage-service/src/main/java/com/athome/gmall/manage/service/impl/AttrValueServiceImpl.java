package com.athome.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.athome.gmall.bean.PmsBaseAttrValue;
import com.athome.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.athome.gmall.service.AttrValueService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class AttrValueServiceImpl implements AttrValueService {
    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;
    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        List<PmsBaseAttrValue> baseAttrValues = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
        return baseAttrValues;
    }
}
