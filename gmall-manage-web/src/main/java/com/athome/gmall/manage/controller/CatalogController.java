package com.athome.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.athome.gmall.service.CatalogService;
import com.athome.gmall.bean.PmsBaseCatalog1;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CatalogController {
    @Reference
    CatalogService catalogService;
    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<PmsBaseCatalog1> getCatalog1(){
        List<PmsBaseCatalog1> catalog1 = catalogService.getCatalog1();
        return catalog1;
    }
}
