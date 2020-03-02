package com.athome.gmall.item.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.athome.gmall.bean.PmsProductSaleAttr;
import com.athome.gmall.bean.PmsSkuInfo;
import com.athome.gmall.service.PmsProductInfoService;
import com.athome.gmall.service.SkuService;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@CrossOrigin
public class ItemController {

    @Reference
    SkuService skuService;
    @Reference
    PmsProductInfoService pmsProductInfoService;

    @RequestMapping("index")
    public String index(ModelMap modelMap) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("循环数据" + i);
        }
        modelMap.put("list", list);
        modelMap.put("hello", "hello thymeleaf !!");
        modelMap.put("check", 1);
        return "index";

    }

    //将请求地址写成和京东类似的以skuId为开头的名字
    //@PathVariable是spring3.0的一个新功能：接收请求路径中占位符的值
    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap modelMap) {
        //sku对象
        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId);
        modelMap.put("skuInfo", pmsSkuInfo);
        //销售属性列表
        List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductInfoService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),pmsSkuInfo.getId());
        modelMap.put("spuSaleAttrListCheckBySku", pmsProductSaleAttrList);
        return "item";
    }
}
