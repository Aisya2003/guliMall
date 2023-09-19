package com.example.mall.coupon;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.model.to.SeckillSessionTo;
import com.example.mall.coupon.feign.ProductFeignClient;
import com.example.mall.coupon.model.po.SeckillSkuRelation;
import com.example.mall.coupon.service.SeckillSessionService;
import com.example.mall.coupon.service.SeckillSkuRelationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class MallCouponApplicationTests {
    @Autowired
    private SeckillSessionService seckillSessionService;

    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;
    @Autowired
    private ProductFeignClient productFeignClient;

    @Test
    void contextLoads() {
        System.out.println(seckillSessionService.latest3DaySkuList());
    }

    @Test
    void testSkuInfo() {
        System.out.println(productFeignClient.getInfo(9L));
    }

    @Test
    void testSeckillLimit() {
        List<SeckillSkuRelation> list = seckillSkuRelationService.list(new LambdaQueryWrapper<SeckillSkuRelation>()
                .eq(SeckillSkuRelation::getPromotionSessionId, 2));
        List<SeckillSessionTo.SeckillSkuRelationTo> skuRelationToList = list.stream()
                .map(seckillSkuRelation -> {
                    SeckillSessionTo.SeckillSkuRelationTo skuRelationTo = new SeckillSessionTo.SeckillSkuRelationTo();
                    BeanUtils.copyProperties(seckillSkuRelation, skuRelationTo);
                    skuRelationTo.setSkuInfo(productFeignClient.getInfo(skuRelationTo.getSkuId()));
                    return skuRelationTo;
                }).collect(Collectors.toList());
        System.out.println(skuRelationToList);
    }
}
