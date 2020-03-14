package com.athome.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.athome.gmall.bean.PmsProductImage;
import com.athome.gmall.bean.PmsProductInfo;
import com.athome.gmall.bean.PmsProductSaleAttr;
import com.athome.gmall.manage.util.PmsUploadUtil;
import com.athome.gmall.search.service.PmsProductInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@CrossOrigin
public class SpuController  {
    @Reference(timeout = 30000)
    PmsProductInfoService pmsProductInfoService;
    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(String catalog3Id){
        return pmsProductInfoService.getspuList(catalog3Id);
    }
    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public void saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){

        pmsProductInfoService.saveSpuInfo(pmsProductInfo);

        //return revalue;
    }
    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload (@RequestParam("file")MultipartFile multipartFile){
        //将图片上传到分布式文件系统

        //图片路径返回给页面
        String imgUrl = PmsUploadUtil.uploadImage(multipartFile);
        return imgUrl;
    }
    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductInfoService.getSaleAttrList(spuId);
        return pmsProductSaleAttrs;

    }
    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(String spuId){
        List<PmsProductImage> pmsProductImages = pmsProductInfoService.getspuImageList(spuId);
        return pmsProductImages;

    }
}
