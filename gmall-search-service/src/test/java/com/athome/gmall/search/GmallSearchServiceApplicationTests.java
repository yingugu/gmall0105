package com.athome.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.athome.gmall.bean.PmsSearchSkuInfo;
import com.athome.gmall.bean.PmsSkuInfo;
import com.athome.gmall.search.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
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

            put();


    }



    public void get(){
        //用jestapi封装dsl查询语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // filter,里面的参数是term，term里面的参数是筛选条件的字段名和值
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder(null,null);

        //还可以输入terms,里面放的是字段名称和值得可变数组
        TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder("","");
        boolQueryBuilder.filter(termQueryBuilder);
        //must,里面放的是搜索match，match里面放的是搜索的字段名和检索的词
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(null,null);

        boolQueryBuilder.must(matchQueryBuilder);
        //highlight
        searchSourceBuilder.highlight(null);
        //from
        searchSourceBuilder.from(0);
        //size
        searchSourceBuilder.size(20);
        //query
        searchSourceBuilder.query(boolQueryBuilder);


        String dslStr = searchSourceBuilder.toString();


        //用api执行复杂查询
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

        Search search = new Search.Builder(dslStr).addType("PmsSkuInfo").build();

        //下面是正常的查询，上面是使用jest工具生成dsl语句
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
        SearchResult searchResult = null;
        try {
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;
            pmsSearchSkuInfos.add(source);
        }
    }

    public void put() throws Exception {


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
