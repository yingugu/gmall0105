package com.athome.gmall.search.service.impl;


import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.athome.gmall.bean.PmsSearchParam;
import com.athome.gmall.bean.PmsSearchSkuInfo;
import com.athome.gmall.bean.PmsSkuAttrValue;
import com.athome.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TemplateQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service

public class SearchServiceImpl implements SearchService {
    @Autowired
    JestClient jestClient;

    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) {
        String dslStr = getDslStr(pmsSearchParam);
        System.out.println(dslStr);
        //用api执行复杂查询
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        Search search = new Search.Builder(dslStr).addIndex("gmall0105").addType("PmsSkuInfo").build();
        SearchResult execute = null;

        try {
            execute = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<SearchResult.Hit<PmsSearchSkuInfo,Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;


            //将结果中的高亮字段取出，因为高亮和查询结果是一个层级的，不会解析到，所以要讲值取出后再赋值

            Map<String, List<String>> highlight = hit.highlight;
            if (highlight!=null){

            String skuName = highlight.get("skuName").get(0);
            source.setSkuName(skuName);
            }
            pmsSearchSkuInfos.add(source);
        }

        //return searchSourceBuilder.toString();

        return pmsSearchSkuInfos;
    }

    public  String getDslStr(PmsSearchParam pmsSearchParam){
       String[] skuAttrValueList = pmsSearchParam.getValueId();
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
//先建立一个查询，复杂查询的时候，首先是query，query中包含bool
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//查询条件中的bool，bool中包含term或terms和must，是查询中的筛选条件
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
//筛选条件中的term，用来筛选
        if (StringUtils.isNotEmpty(catalog3Id)){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id",catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        if (skuAttrValueList!=null){
            for (String pmsSkuAttrValue : skuAttrValueList) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId",pmsSkuAttrValue);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        //查询中的查询条件,就是must，里面写的是值

        if (StringUtils.isNotEmpty(keyword)){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",keyword);
            boolQueryBuilder.must(matchQueryBuilder);

        }

//        // must
//        if(StringUtils.isNotEmpty(keyword)){
//            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",keyword);
//            boolQueryBuilder.must(matchQueryBuilder);
//        }
        //query
        searchSourceBuilder.query(boolQueryBuilder);


        //highligh
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //高亮正常情况下默认是斜体，想要自己规定样式，就要自己定义一下
        highlightBuilder.preTags("<span style = 'color:red;'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlight(highlightBuilder);

        //sort
        searchSourceBuilder.sort("id", SortOrder.DESC);
        //from
        searchSourceBuilder.from(0);
        //size
        searchSourceBuilder.size(20);
        String dslStr = searchSourceBuilder.toString();
//        //aggs,因为es的聚合函数有点浪费性能，所以不用这种方式（因为会将所有查询结果做一次统计，损耗查询性能）
//        TermsBuilder groupby_attr = AggregationBuilders.terms("name").field("skuAttrValueList.valueId");
//        searchSourceBuilder.aggregation(groupby_attr);



        return dslStr;

    }
}
