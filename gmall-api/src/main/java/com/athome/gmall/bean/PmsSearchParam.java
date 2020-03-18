package com.athome.gmall.bean;

import java.io.Serializable;
import java.util.List;

public class PmsSearchParam implements Serializable {
    private String catalog3Id;
    private String keyword;
    //因为在面包屑功能中，无法直接获取值，所以将其改成String数组的形式

    //List<PmsSkuAttrValue> skuAttrValueList;
    private String[] valueId;

    public String[] getValueId() {
        return valueId;
    }

    public void setValueId(String[] valueId) {
        this.valueId = valueId;
    }

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

//    public List<PmsSkuAttrValue> getSkuAttrValueList() {
//        return skuAttrValueList;
//    }
//
//    public void setSkuAttrValueList(List<PmsSkuAttrValue> skuAttrValueList) {
//        this.skuAttrValueList = skuAttrValueList;
//    }
}
