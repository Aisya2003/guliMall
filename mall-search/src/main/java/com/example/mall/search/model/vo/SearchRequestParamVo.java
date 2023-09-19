package com.example.mall.search.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchRequestParamVo {
    private String keyword;
    private String catalog3Id;
    private String sort;
    private Integer hasStock;
    private String skuPrice;
    private List<Long> brandId;
    private List<String> attrs;
    private Integer pageNum = 1;

    private String _url;
}
