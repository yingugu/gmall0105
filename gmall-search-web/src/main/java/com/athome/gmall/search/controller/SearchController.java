package com.athome.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.athome.gmall.bean.PmsSearchParam;
import com.athome.gmall.bean.PmsSearchSkuInfo;
import com.athome.gmall.search.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class SearchController {

    @Reference
    SearchService searchService;


    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap){//参数可能有：三级分类ID，关键字，平台属性集合
        //调用搜索服务
        List<PmsSearchSkuInfo> pmsSearchSkuInfo = searchService.list(pmsSearchParam);

        modelMap.put("skuLsInfoList",pmsSearchSkuInfo);
        //返回搜索结果


        return "list";
    }
    @RequestMapping("index")
    public String index(){

        return "index";
    }

}
