package com.example.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.model.to.SeckillSessionTo;
import com.example.mall.coupon.feign.ProductFeignClient;
import com.example.mall.coupon.model.po.SeckillSession;
import com.example.mall.coupon.mapper.SeckillSessionMapper;
import com.example.mall.coupon.model.po.SeckillSkuRelation;
import com.example.mall.coupon.service.SeckillSessionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.coupon.service.SeckillSkuRelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionMapper, SeckillSession> implements SeckillSessionService {
    private final SeckillSkuRelationService seckillSkuRelationService;
    private final ProductFeignClient productFeignClient;

    @Override
    public List<SeckillSessionTo> latest3DaySkuList() {

        List<SeckillSession> seckillSessions = this.list(new LambdaQueryWrapper<SeckillSession>()
                .between(SeckillSession::getStartTime,
                        LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
                        LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.MAX)));
        if (seckillSessions == null || seckillSessions.size() == 0) {
            return null;
        }
        return seckillSessions
                .stream()
                .map(seckillSession -> {
                    SeckillSessionTo seckillSessionTo = new SeckillSessionTo();
                    BeanUtils.copyProperties(seckillSession, seckillSessionTo);
                    List<SeckillSkuRelation> seckillSkuRelations = seckillSkuRelationService.list(new LambdaQueryWrapper<SeckillSkuRelation>()
                            .eq(SeckillSkuRelation::getPromotionSessionId, seckillSessionTo.getId()));
                    if (seckillSkuRelations != null && seckillSkuRelations.size() > 0) {
                        List<SeckillSessionTo.SeckillSkuRelationTo> skuRelationToList = seckillSkuRelations.stream()
                                .map(seckillSkuRelation -> {
                                    SeckillSessionTo.SeckillSkuRelationTo skuRelationTo = new SeckillSessionTo.SeckillSkuRelationTo();
                                    BeanUtils.copyProperties(seckillSkuRelation, skuRelationTo);
                                    skuRelationTo.setSkuInfo(productFeignClient.getInfo(skuRelationTo.getSkuId()));
                                    return skuRelationTo;
                                }).collect(Collectors.toList());
                        seckillSessionTo.setSkuRelationList(skuRelationToList);
                    }
                    return seckillSessionTo;
                }).collect(Collectors.toList());
    }
}
