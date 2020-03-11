package com.athome.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.athome.gmall.bean.PmsSearchSkuInfo;
import com.athome.gmall.bean.PmsSkuInfo;
import com.athome.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchServiceApplicationTests {

    @Reference(timeout = 30000)
    SkuService skuService; //查询mysql
    @Autowired
    JestClient jestClient;

    @Test
    public void contextLoads() throws Exception {

        //查询mysql数据
        List<PmsSkuInfo> pmsSkuInfoList = new ArrayList<>();

        pmsSkuInfoList = skuService.getAllSku();
        //转化为es的数据结构
        List<PmsSearchSkuInfo> pmsSearchSkuInfoList = new ArrayList<>();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(pmsSkuInfo, pmsSearchSkuInfo);
            pmsSearchSkuInfoList.add(pmsSearchSkuInfo);
        }
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfoList) {
                //导入es
                Index put = new Index.Builder(pmsSearchSkuInfo).index("gmall0105").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId()).build();
                System.out.println(Thread.currentThread().getName());
                //Builder中source倒入的數據，最後會被轉換成json，後面的index是庫名，type中是表名，id中是主鍵
                    jestClient.execute(put);
                System.out.println(Thread.currentThread().getName());

        }

    }

}
