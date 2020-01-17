package com.athome.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.athome.gmall.bean.PmsBaseCatalog1;
import com.athome.gmall.manage.mapper.PmsBaseCatalog1Mapper;
import com.athome.gmall.manage.mapper.PmsBaseCatalog2Mapper;
import com.athome.gmall.manage.mapper.PmsBaseCatalog3Mapper;
import com.athome.gmall.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;
    @Autowired
    PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;
    @Autowired
    PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;
    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
    List<PmsBaseCatalog1> pmsBaseCatalog1 = pmsBaseCatalog1Mapper.selectAll();
    return pmsBaseCatalog1;
    }
}
