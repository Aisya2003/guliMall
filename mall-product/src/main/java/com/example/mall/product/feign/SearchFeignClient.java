package com.example.mall.product.feign;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.es.SkuUploadESTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("mall-search")
@Component
public interface SearchFeignClient {
    @PostMapping("/search/index/product")
    Result index(@RequestBody List<SkuUploadESTo> uploadESTos);
}
