package com.athome.gmall.manage.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.athome.gmall.bean.PmsSkuInfo;
import com.athome.gmall.service.SkuService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@CrossOrigin
@Controller
public class Skucontroller {
@Reference
SkuService skuService;

    @RequestMapping("saveSkuInfo")
    @ResponseBody
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){
//因为前端页面中封装的参数名称是spuId，替换的话没有办法替换完全，
// 所以新建了一个Transient（只是可以接受参数，不会进入数据库）的参数，
// 开发过程中要讲spuId封装给productId
   pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());
   //因为前段没有拦截，所以在后端处理默认图片忘记选的情况
        String skuDefaultImg = pmsSkuInfo.getSkuDefaultImg();
        if (StringUtils.isBlank(skuDefaultImg)){
            pmsSkuInfo.setSkuDefaultImg(pmsSkuInfo.getPmsSkuImageList().get(0).getImgUrl());
        }
        skuService.saveSkuInfo(pmsSkuInfo);
        return  "SUCCESS";
    }

}
