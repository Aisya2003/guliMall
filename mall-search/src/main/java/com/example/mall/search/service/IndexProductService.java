package com.example.mall.search.service;

import com.example.mall.common.model.to.es.SkuUploadESTo;

import java.util.List;

public interface IndexProductService {
    boolean indexProduct(List<SkuUploadESTo> uploadESTos);
}
