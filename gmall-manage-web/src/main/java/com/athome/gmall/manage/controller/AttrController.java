package com.athome.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.athome.gmall.bean.PmsBaseAttrInfo;
import com.athome.gmall.bean.PmsBaseAttrValue;
import com.athome.gmall.bean.PmsProductInfo;
import com.athome.gmall.service.AttrInfoService;
import com.athome.gmall.service.AttrValueService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class AttrController {
    @Reference
    AttrInfoService attrInfoService;
    @Reference
    AttrValueService attrValueService;

    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        List<PmsBaseAttrInfo> attrInfoList = attrInfoService.getAttrInfoList(catalog3Id);
        System.out.println(attrInfoList);
        return attrInfoList;
    }

    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        List<PmsBaseAttrValue> attrInfoList = attrValueService.getAttrValueList(attrId);
        System.out.println(attrInfoList);
        return attrInfoList;
    }

    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsProductInfo) {
        String success = attrInfoService.saveAttrInfo(pmsProductInfo);
        return success;

    }
}
