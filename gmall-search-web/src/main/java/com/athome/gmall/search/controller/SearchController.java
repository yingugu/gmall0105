package com.athome.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.athome.gmall.bean.PmsBaseAttrInfo;
import com.athome.gmall.bean.PmsSearchParam;
import com.athome.gmall.bean.PmsSearchSkuInfo;
import com.athome.gmall.bean.PmsSkuAttrValue;
import com.athome.gmall.service.AttrInfoService;
import com.athome.gmall.service.AttrValueService;
import com.athome.gmall.service.SearchService;
import com.athome.gmall.service.SkuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class SearchController {

    @Reference
    SearchService searchService;

    @Reference
    AttrInfoService attrInfoService;

    @RequestMapping("list.html")
    //@RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap) {// 三级分类id、关键字、

        // 调用搜索服务，返回搜索结果
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list(pmsSearchParam);
        modelMap.put("skuLsInfoList", pmsSearchSkuInfos);
        //抽取检索结果中包含的平台属性集合
        Set<String> valueSet = new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                String attrId = pmsSkuAttrValue.getAttrId();
                valueSet.add(attrId);
            }

        }
        //根据attrId将属性列表查询出来
        List<PmsBaseAttrInfo> pmsBaseAttrInfos =  attrInfoService.getAttrValueListByValueId(valueSet);
    modelMap.put("attrList",pmsBaseAttrInfos);


        return "list";
    }

    @RequestMapping("index")
    public String index() {
        return "index";

    }
}
