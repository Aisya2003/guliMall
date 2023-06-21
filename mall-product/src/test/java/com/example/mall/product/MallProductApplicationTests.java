package com.example.mall.product;

import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.product.model.po.Brand;
import com.example.mall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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

    @Test
    void asd() {
        PageRequestParams params = new PageRequestParams();
        params.setKey("qwer");
        params.setPage(1);
        params.setLimit(10);
        PageResult<Brand> page = PageResultUtils.getPage(params, brandService.getBaseMapper(), Brand.class);
        List<Brand> list = page.getList();
        System.out.println(list.toString());
    }
}
