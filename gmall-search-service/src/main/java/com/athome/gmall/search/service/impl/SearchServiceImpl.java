package com.athome.gmall.search.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.athome.gmall.bean.PmsSearchParam;
import com.athome.gmall.bean.PmsSearchSkuInfo;
import com.athome.gmall.bean.PmsSkuAttrValue;
import com.athome.gmall.search.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    JestClient jestClient;

    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) {

        String dslStr = getSearchDsl(pmsSearchParam);
        //用api执行复杂查询
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

        Search search = new Search.Builder(dslStr).addType("PmsSkuInfo").build();
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


        return pmsSearchSkuInfos;
    }

    private String getSearchDsl(PmsSearchParam pmsSearchParam) {

        //用jestapi封装dsl查询语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        List<PmsSkuAttrValue> skuAttrValueList = pmsSearchParam.getSkuAttrValueList();
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        if (StringUtils.isNotEmpty(catalog3Id)) {

            // filter,里面的参数是term，term里面的参数是筛选条件的字段名和值
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);

            //还可以输入terms,里面放的是字段名称和值得可变数组
            // TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder("","");
            boolQueryBuilder.filter(termQueryBuilder);
        }

        if (skuAttrValueList != null) {
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                // filter,里面的参数是term，term里面的参数是筛选条件的字段名和值
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", pmsSkuAttrValue.getValueId());

                //还可以输入terms,里面放的是字段名称和值得可变数组
                // TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder("","");
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        if (StringUtils.isNotEmpty(keyword)) {

            //must,里面放的是搜索match，match里面放的是搜索的字段名和检索的词
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);

            boolQueryBuilder.must(matchQueryBuilder);
        }


        //highlight
        searchSourceBuilder.highlight(null);
        //from
        searchSourceBuilder.from(0);
        //size
        searchSourceBuilder.size(20);
        //query
        searchSourceBuilder.query(boolQueryBuilder);

        //sort排序

        searchSourceBuilder.sort("id",SortOrder.DESC);


        String dslStr = searchSourceBuilder.toString();
        return dslStr;
    }
}
