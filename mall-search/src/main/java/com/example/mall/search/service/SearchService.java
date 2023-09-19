package com.example.mall.search.service;

import com.example.mall.search.model.vo.SearchRequestParamVo;
import com.example.mall.search.model.vo.SearchResponseVo;

public interface SearchService {
    interface Web{
        SearchResponseVo search(SearchRequestParamVo requestParamVo);
    }
}
