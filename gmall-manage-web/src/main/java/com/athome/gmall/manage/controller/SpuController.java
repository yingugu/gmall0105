package com.athome.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.athome.gmall.bean.PmsProductInfo;
import com.athome.gmall.manage.util.PmsUploadUtil;
import com.athome.gmall.service.PmsProductInfoService;
import com.sun.demo.jvmti.hprof.Tracker;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@CrossOrigin
public class SpuController  {
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
    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload (@RequestParam("file")MultipartFile multipartFile){
        //将图片上传到分布式文件系统

        //图片路径返回给页面
        String imgUrl = PmsUploadUtil.uploadImage(multipartFile);
        return imgUrl;
    }
}
