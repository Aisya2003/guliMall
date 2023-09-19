package com.example.mall.common.model.page;

import lombok.Data;

@Data
public class PageRequestParams {
    private int page;
    private int limit = 5;
    private String key;
}
