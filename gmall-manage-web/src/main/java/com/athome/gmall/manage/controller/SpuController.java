package com.athome.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.athome.gmall.bean.PmsProductInfo;
import com.athome.gmall.service.PmsProductInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class SpuController {
    @Reference
    PmsProductInfoService pmsProductInfoService;
    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(String catalog3Id){
        return pmsProductInfoService.getspuList(catalog3Id);
    }
    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){

        String revalue = pmsProductInfoService.saveSpuInfo(pmsProductInfo);

        return revalue;
    }
}
