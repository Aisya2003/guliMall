package com.example.mall.product;

import com.example.mall.product.model.po.Brand;
import com.example.mall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MallProductApplicationTests {

    @Autowired
    private BrandService brandService;
    @Test
    void contextLoads() {
//        Brand entity = new Brand();
//        entity.setName("华为");
//        brandService.save(entity);
        brandService.list().forEach(
                System.out::println
        );
    }

}
