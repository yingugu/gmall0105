package com.athome.gmall.item.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.athome.gmall.bean.PmsProductSaleAttr;
import com.athome.gmall.bean.PmsSkuInfo;
import com.athome.gmall.bean.PmsSkuSaleAttrValue;
import com.athome.gmall.search.service.PmsProductInfoService;
import com.athome.gmall.search.service.SkuService;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
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
    public String item(@PathVariable String skuId, ModelMap modelMap, HttpServletRequest request) {
        //拿IP
        String remoteAddr = request.getRemoteAddr();//直接从请求中获取ip
        //另一个方式
        //String header = request.getHeader("");//使用NGINX负载均衡之后，如果用上面的方法，获得的是NGINX的IP

        //sku对象
        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId,remoteAddr);
        modelMap.put("skuInfo", pmsSkuInfo);
        //销售属性列表
        List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductInfoService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),pmsSkuInfo.getId());
        modelMap.put("spuSaleAttrListCheckBySku", pmsProductSaleAttrList);
        //下面部分可以将所有spu关联的sku也就是hash表，存成js文件，静态化，页面会缓存，降低访问数据库次数
        //查询当前的spu的集合的hash表
        HashMap<String, String> skuSaleAttrHash = new HashMap<>();
        List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());

        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String k = "";
            String v = skuInfo.getId();
            List<PmsSkuSaleAttrValue> skuSaleAttrValues = skuInfo.getSkuSaleAttrValueList();

            for (PmsSkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValues) {
                k+=skuSaleAttrValue.getSaleAttrValueId()+"|";//建议使用管道符,使用逗号的话容易被误解成一个数组
            }
            skuSaleAttrHash.put(k,v);
        }
        //讲sku销售属性的hash表放到页面，不能直接放到域（modelmap里面），因为后面在页面（客户端）取得时候，要保证能直接用，如果直接放在域里面，取出来的是一个Java对象
        //使用fastjson 和Gson一样
        String skuSaleAttrHashJSONStr = JSON.toJSONString(skuSaleAttrHash);
        modelMap.put("skuSaleAttrHashJsonStr",skuSaleAttrHashJSONStr);

        return "item";
    }
}
