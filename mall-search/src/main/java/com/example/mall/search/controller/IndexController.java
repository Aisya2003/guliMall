package com.example.mall.search.controller;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.es.SkuUploadESTo;
import com.example.mall.search.service.IndexProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search/index")
public class IndexController {
    private final IndexProductService indexProductService;

    public IndexController(IndexProductService indexProductService) {
        this.indexProductService = indexProductService;
    }

    @PostMapping("/product")
    public Result index(@RequestBody List<SkuUploadESTo> uploadESTos){
        if (indexProductService.indexProduct(uploadESTos)){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
}
