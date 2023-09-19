package com.example.mall.search;

import com.alibaba.fastjson2.JSON;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = MallSearchApplication.class)
class MallSearchApplicationTests {
    private final RestHighLevelClient client;

    @Autowired
    MallSearchApplicationTests(RestHighLevelClient client) {
        this.client = client;
    }

    @Test
    void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("2");
        Map<String, Object> map = new HashMap<>();
        map.put("userName", "zhangsan");
        map.put("gender", "男");
        map.put("age", 20);
        indexRequest.source(JSON.toJSONString(map), XContentType.JSON);

        client.index(indexRequest, RequestOptions.DEFAULT);
    }

    @Test
    void testSearch() throws Exception {
        SearchRequest searchRequest = new SearchRequest("users");

        SearchSourceBuilder source = new SearchSourceBuilder();
        source.query(QueryBuilders.matchAllQuery());
        source.aggregation(AggregationBuilders.terms("userNameTAggr").field("age").size(10));

        searchRequest.source(source);

        System.out.println(source.toString());

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(Arrays.toString(searchResponse.getHits().getHits()));




    }
}
