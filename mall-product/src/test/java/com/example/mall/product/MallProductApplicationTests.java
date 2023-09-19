package com.example.mall.product;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.product.mapper.AttrGroupMapper;
import com.example.mall.product.mapper.SkuSaleAttrValueMapper;
import com.example.mall.product.model.po.AttrGroup;
import com.example.mall.product.model.po.Brand;
import com.example.mall.product.model.po.SkuSaleAttrValue;
import com.example.mall.product.model.vo.SkuDetailVo;
import com.example.mall.product.service.AttrGroupService;
import com.example.mall.product.service.BrandService;
import com.example.mall.product.service.SkuInfoService;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@SpringBootTest(classes = MallProductApplication.class)
class MallProductApplicationTests {

    @Autowired
    private BrandService brandService;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private AttrGroupMapper attrGroupMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SkuInfoService skuInfoService;

    @Test
    void testGetInfo() {
        System.out.println(skuInfoService.getById(9L));
    }

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

    @Test
    void testRedis() {
        stringRedisTemplate.opsForValue().set("test", "hello");
        System.out.println("set Success");
        System.out.println("get Success,test = " + stringRedisTemplate.opsForValue().get("test"));
    }

    @Test
    void testDetail() {
//        SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueMapper.selectById(1);
//        System.out.println("skuSaleAttrValue = " + skuSaleAttrValue);
        List<SkuDetailVo.SkuDetailSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueMapper.getSaleAttrsBySpuId(6L);
        System.out.println("saleAttrsBySpuId = " + saleAttrsBySpuId);
    }

    @Test
    void testAttrs() {
        List<SkuDetailVo.SpuDetailAttrGroupVo> attrGroupDetailBySpuIdAndCatalogId = attrGroupMapper.getAttrGroupDetailBySpuIdAndCatalogId(6L, 225L);
        System.out.println("attrGroupDetailBySpuIdAndCatalogId = " + attrGroupDetailBySpuIdAndCatalogId);
    }
}
