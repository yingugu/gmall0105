package com.athome.gmall.manage.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.athome.gmall.bean.PmsBaseAttrInfo;
import com.athome.gmall.bean.PmsBaseAttrValue;
import com.athome.gmall.bean.PmsBaseSaleAttr;
import com.athome.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.athome.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.athome.gmall.manage.mapper.PmsBaseSaleAttrMapper;
import com.athome.gmall.search.service.AttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
@Service

public class AttrInfoServiceImpl implements AttrInfoService {
    @Autowired
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;
    @Autowired
    PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Override
    public List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);
        for (PmsBaseAttrInfo baseAttrInfo : pmsBaseAttrInfos) {
            List<PmsBaseAttrValue> pmsBaseAttrValueList = new ArrayList<>();
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(baseAttrInfo.getId());
            pmsBaseAttrValueList = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
            baseAttrInfo.setAttrValueList(pmsBaseAttrValueList);
        }
        return pmsBaseAttrInfos;
    }


    @Override
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        String success = null;
        String id = pmsBaseAttrInfo.getId();
        try {
            if (StringUtils.isBlank(id)){
                //如果id为空，就执行保存操作
                //保存属性
                pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);//insert和insertSelective的区别：是否将null插入数据库，insertSelective是只将有值得部分插入到数据库
                //保存属性值
                List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                    pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                    pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
                    success = "SUCCESS";
                }
            }else {

                //如果不为空，就执行修改操作
                Example example = new Example(PmsBaseAttrInfo.class);
                example.createCriteria().andEqualTo("id",id);
                pmsBaseAttrInfoMapper.updateByExampleSelective(pmsBaseAttrInfo,example);
                //先将属性值相同的value删除
                PmsBaseAttrValue pmsBaseAttrValueDel = new PmsBaseAttrValue();
                pmsBaseAttrValueDel.setAttrId(id);
                pmsBaseAttrValueMapper.delete(pmsBaseAttrValueDel);
                //属性值
                List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                //逐条插入
                for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                   // pmsBaseAttrInfo.setId(null);
                    pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
                }
            }
            success = "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            success = "FALSE";
        }
        return success;
    }

    @Override
    public List<PmsBaseSaleAttr> getbaseSaleAttrList() {
        List<PmsBaseSaleAttr> pmsBaseSaleAttrs = pmsBaseSaleAttrMapper.selectAll();
        return pmsBaseSaleAttrs;
    }

}
