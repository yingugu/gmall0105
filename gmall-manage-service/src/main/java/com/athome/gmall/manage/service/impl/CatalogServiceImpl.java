package com.athome.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.athome.gmall.bean.PmsBaseCatalog1;
import com.athome.gmall.bean.PmsBaseCatalog2;
import com.athome.gmall.bean.PmsBaseCatalog3;
import com.athome.gmall.manage.mapper.PmsBaseCatalog1Mapper;
import com.athome.gmall.manage.mapper.PmsBaseCatalog2Mapper;
import com.athome.gmall.manage.mapper.PmsBaseCatalog3Mapper;
import com.athome.gmall.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;

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

    @Override
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {
        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
        pmsBaseCatalog2.setCatalog1Id(catalog1Id);

        List<PmsBaseCatalog2> catalog2s = pmsBaseCatalog2Mapper.select(pmsBaseCatalog2);
        return catalog2s;
    }

    @Override
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {
        PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3();
        pmsBaseCatalog3.setCatalog2_id(catalog2Id);
        List<PmsBaseCatalog3> catalog3s = pmsBaseCatalog3Mapper.select(pmsBaseCatalog3);
        return catalog3s;
    }


}
