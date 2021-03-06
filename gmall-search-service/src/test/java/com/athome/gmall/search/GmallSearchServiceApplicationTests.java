package com.athome.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.athome.gmall.bean.PmsSearchSkuInfo;
import com.athome.gmall.bean.PmsSkuInfo;
import com.athome.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.collections.list.PredicatedList;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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
    //用api执行复杂查询
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();


        //jest的dsl工具
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //bool

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //filter
//filter中加的是term，term的两个参数分别为筛选的字段名和字段值
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.skuId","120");
        boolQueryBuilder.filter(termQueryBuilder);



        //must
        //must中加的是match,两个参数是字段名和关键字
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName","测试");
        boolQueryBuilder.must(matchQueryBuilder);

        //query
        searchSourceBuilder.query(boolQueryBuilder);
        //highlight
        //from
        //size

//生成dsl语句
        String dslStr = searchSourceBuilder.toString();

//        Search search = new Search.Builder("{\n" +
//                "  \"query\": {\n" +
//                "    \"bool\": {\n" +
//                "      \"filter\": {\n" +
//                "        \"terms\": {\n" +
//                "          \"skuAttrValueList.skuId\": [\n" +
//                "            \"120\",\n" +
//                "            \"121\"\n" +
//                "          ]\n" +
//                "        }\n" +
//                "        \n" +
//                "      },\n" +
//                "      \"must\": [\n" +
//                "        {\"match\": {\n" +
//                "          \"skuName\": \"测试\"\n" +
//                "        }}\n" +
//                "      ]\n" +
//                "      \n" +
//                "    }\n" +
//                "    \n" +
//                "  }\n" +
//                "  \n" +
//                "}").addIndex("gmall0105").addType("PmsSkuInfo").build();
//上面的内容相当于：
        System.out.println(dslStr);
        Search search = new Search.Builder(dslStr).addIndex("gmall0105").addType("PmsSkuInfo").build();
        SearchResult searchResult = jestClient.execute(search);

        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;
            pmsSearchSkuInfos.add(source);
        }


    }


    public void put() throws Exception{




        //查询mysql数据
        List<PmsSkuInfo> pmsSkuInfoList = new ArrayList<>();

        pmsSkuInfoList = skuService.getAllSku();
        //转化为es的数据结构
        List<PmsSearchSkuInfo> pmsSearchSkuInfoList = new ArrayList<>();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(pmsSkuInfo, pmsSearchSkuInfo);
            pmsSearchSkuInfo.setId(Long.parseLong(pmsSkuInfo.getId()));
            pmsSearchSkuInfoList.add(pmsSearchSkuInfo);
        }
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfoList) {
            //导入es
            Index put = new Index.Builder(pmsSearchSkuInfo).index("gmall0105").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId()+"").build();
            // System.out.println(Thread.currentThread().getName());
            //Builder中source倒入的數據，最後會被轉換成json，後面的index是庫名，type中是表名，id中是主鍵
            jestClient.execute(put);
            System.out.println(Thread.currentThread().getName());

        }


    }

}
