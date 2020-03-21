package com.athome.gmall.search.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.athome.gmall.bean.*;
import com.athome.gmall.service.AttrInfoService;
import com.athome.gmall.service.AttrValueService;
import com.athome.gmall.service.SearchService;
import com.athome.gmall.service.SkuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

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
        // 抽取检索结果锁包含的平台属性集合
        Set<String> valueIdSet = new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                String valueId = pmsSkuAttrValue.getValueId();
                valueIdSet.add(valueId);
            }
        }
        // 根据valueId将属性列表查询出来
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrInfoService.getAttrValueListByValueId(valueIdSet);
        modelMap.put("attrList", pmsBaseAttrInfos);

        //面包屑,urlparam中包含的信息就是pmsSearchParam传递过来的信息
        String urlParam = getUrlParam(pmsSearchParam);
        //对平台属性集合进一步处理---》去掉当前条件中valueId所在的属性组

        String[] delValueIds = pmsSearchParam.getValueId();

        if (delValueIds != null) {
            //面包屑
//因为此处循环与下面面包屑基本一致，所以将面包屑合并到此
            //删除平台属性，同时也是制作被删的这个面包屑的时间
            List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
            for (String delValueId : delValueIds) {
                //迭代器要放在这就是循环内，因为迭代器在迭代结束之后，不会重新迭代  要重新new
                Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                //生成面包屑的参数
                pmsSearchCrumb.setValueId(delValueId);
                //这个name应该放在最内层循环，循环之后拿到的
                //pmsSearchCrumb.setValueName(attrName);
                pmsSearchCrumb.setUrlParam(getUrlParamForCrumb(pmsSearchParam, delValueId));
                String attrName = "";
                while (iterator.hasNext()) {
                    PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                    List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                    for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {

                        String valueId = pmsBaseAttrValue.getId();

                        if (delValueId.equals(valueId)) {
                            attrName = pmsBaseAttrValue.getValueName();
                            pmsSearchCrumb.setValueName(attrName);
                            iterator.remove();
                        }
                    }
                }

                pmsSearchCrumbs.add(pmsSearchCrumb);
            }
            modelMap.put("attrValueSelectedList", pmsSearchCrumbs);
        }

        modelMap.put("urlParam", urlParam);

        String keyword = pmsSearchParam.getKeyword();
        if (StringUtils.isNotEmpty(keyword)) {

            modelMap.put("keyword", keyword);

        }


//        //面包屑
//          因为循环与上面删除的方法一致，所以  将面包屑方法与上面删除属性方法合并
//        List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
//        if (delValueIds!=null){
//            //如果delvalueId不为空，代表当前请求中包含属性的参数，每一个属性参数都会生成一个面包屑
//
//            for (String delValueId : delValueIds) {
//                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
//                //生成面包屑的参数
//                pmsSearchCrumb.setValueId(delValueId);
//                pmsSearchCrumb.setValueName(delValueId);
//                pmsSearchCrumb.setUrlParam(getUrlParamForCrumb(pmsSearchParam,delValueId));
//                pmsSearchCrumbs.add(pmsSearchCrumb);
//            }
//
//        }


        return "list";
    }

    private String getUrlParam(PmsSearchParam pmsSearchParam, String... delValueId) {
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] skuAttrValueList = pmsSearchParam.getValueId();
        String urlParam = "";
        if (StringUtils.isNotEmpty(keyword)) {
            if (StringUtils.isNotEmpty(urlParam)) {
                urlParam = urlParam + "&keyword=" + keyword;
            }
            urlParam = urlParam + "keyword=" + keyword;
        }

        if (StringUtils.isNotEmpty(catalog3Id)) {
            if (StringUtils.isNotEmpty(urlParam)) {
                urlParam = urlParam + "&catalog3Id=" + catalog3Id;
            }
            urlParam = urlParam + "catalog3Id=" + catalog3Id;
        }
        if (skuAttrValueList != null) {

            for (String pmsSkuAttrValue : skuAttrValueList) {
                String valueId = pmsSkuAttrValue;
                urlParam = urlParam + "&valueId=" + valueId;
            }
        }
        return urlParam;
    }

    private String getUrlParamForCrumb(PmsSearchParam pmsSearchParam, String delValueId) {
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] skuAttrValueList = pmsSearchParam.getValueId();
        String urlParam = "";
        if (StringUtils.isNotEmpty(keyword)) {
            if (StringUtils.isNotEmpty(urlParam)) {
                urlParam = urlParam + "&keyword=" + keyword;
            }
            urlParam = urlParam + "keyword=" + keyword;
        }

        if (StringUtils.isNotEmpty(catalog3Id)) {
            if (StringUtils.isNotEmpty(urlParam)) {
                urlParam = urlParam + "catalog3Id=" + catalog3Id;
            }
            urlParam = urlParam + "&catalog3Id=" + catalog3Id;
        }
        if (skuAttrValueList != null) {

            for (String pmsSkuAttrValue : skuAttrValueList) {
                String valueId = pmsSkuAttrValue;
                if (!pmsSkuAttrValue.equals(delValueId)) {
                    urlParam = urlParam + "&valueId=" + valueId;
                }
            }
        }
        return urlParam;
    }

    @RequestMapping("index")
    public String index() {
        return "index";

    }
}
