package com.example.mall.search.service.impl;

import com.alibaba.fastjson2.JSON;
import com.example.mall.common.model.constant.ProductConstant;
import com.example.mall.common.model.exception.ESException;
import com.example.mall.common.model.to.es.SkuUploadESTo;
import com.example.mall.search.model.constant.ESConstant;
import com.example.mall.search.model.dto.ESUploadDto;
import com.example.mall.search.service.IndexProductService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IndexIndexProductServiceImpl implements IndexProductService {
    private final RestHighLevelClient esClient;

    public IndexIndexProductServiceImpl(RestHighLevelClient esClient) {
        this.esClient = esClient;
    }

    @Override
    public boolean indexProduct(List<SkuUploadESTo> uploadESTos) {
        BulkRequest indexBulkRequest = new BulkRequest();
        for (SkuUploadESTo uploadESTo : uploadESTos) {
            IndexRequest indexRequest = new IndexRequest(ESConstant.PRODUCT_INDEX);
            indexRequest.id(uploadESTo.getSkuId().toString());
            ESUploadDto esUploadDto = new ESUploadDto();
            BeanUtils.copyProperties(uploadESTo, esUploadDto);
            esUploadDto.setAttrs(
                    uploadESTo.getAttrs().stream()
                            .map(attrs -> {
                                ESUploadDto.Attrs attrDto = new ESUploadDto.Attrs();
                                attrDto.setAttrId(attrs.getAttrId());
                                attrDto.setAttrName(attrs.getAttrName());
                                String[] split = attrs.getAttrValue().split(ProductConstant.ATTR_VALUE_SPLIT);
                                attrDto.setAttrValue(Arrays.asList(split));
                                return attrDto;
                            }).collect(Collectors.toList())
            );
            indexRequest.source(JSON.toJSONString(esUploadDto), XContentType.JSON);
            indexBulkRequest.add(indexRequest);
        }

        BulkResponse bulkResult = null;
        try {
            bulkResult = esClient.bulk(indexBulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            ESException.error(e.getMessage());
        }
        boolean fail = bulkResult.hasFailures();
        if (fail) {
            log.info("商品上传至ES出错,商品id：[{}]，出错原因：[{}]"
                    , Arrays.stream(bulkResult.getItems())
                            .map(BulkItemResponse::getId)
                            .collect(Collectors.toList())
                    , bulkResult.buildFailureMessage());
            return false;
        } else {
            return true;
        }
    }
}
