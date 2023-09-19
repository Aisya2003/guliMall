package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.product.model.po.SkuSaleAttrValue;
import com.example.mall.product.mapper.SkuSaleAttrValueMapper;
import com.example.mall.product.model.vo.SkuDetailVo;
import com.example.mall.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueMapper, SkuSaleAttrValue> implements SkuSaleAttrValueService {

    @Override
    public List<SkuDetailVo.SkuDetailSaleAttrVo> getSaleAttrsBySpuId(Long spuId) {
        return this.baseMapper.getSaleAttrsBySpuId(spuId);
    }

    @Override
    public List<String> getSkuSaleAttrsAsList(Long skuId) {
        return this.list(new LambdaQueryWrapper<SkuSaleAttrValue>()
                        .eq(SkuSaleAttrValue::getSkuId, skuId))
                .stream()
                .map(skuSaleAttrValue ->
                        skuSaleAttrValue.getAttrName() + ":" + skuSaleAttrValue.getAttrValue())
                .collect(Collectors.toList());
    }
}
