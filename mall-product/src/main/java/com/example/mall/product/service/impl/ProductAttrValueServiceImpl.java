package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.product.model.dto.BaseAttrs;
import com.example.mall.product.model.po.ProductAttrValue;
import com.example.mall.product.mapper.ProductAttrValueMapper;
import com.example.mall.product.service.AttrService;
import com.example.mall.product.service.ProductAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueMapper, ProductAttrValue> implements ProductAttrValueService {
    private final AttrService attrService;

    public ProductAttrValueServiceImpl(AttrService attrService) {
        this.attrService = attrService;
    }

    @Override
    @Transactional
    public void saveBaseAttrs(Long spuInfoId, List<BaseAttrs> baseAttrs) {
        if (baseAttrs == null || baseAttrs.isEmpty()) {
            log.info("spu保存的baseAttrs属性为空[{}]", baseAttrs);
        } else {
            List<ProductAttrValue> productAttrValueList = baseAttrs.stream()
                    .map(baseAttr -> {
                        ProductAttrValue attrValue = new ProductAttrValue();
                        Long attrId = baseAttr.getAttrId();
                        attrValue.setAttrId(attrId);
                        attrValue.setAttrValue(baseAttr.getAttrValues());
                        attrValue.setQuickShow(baseAttr.getShowDesc());
                        attrValue.setSpuId(spuInfoId);
                        String attrName = attrService.getById(attrId).getAttrName().intern();
                        attrValue.setAttrName(attrName);
                        return attrValue;
                    })
                    .collect(Collectors.toList());
            this.saveBatch(productAttrValueList);
            log.info("保存spu的productAttrValue完成[{}]", spuInfoId);
        }

    }

    @Override
    public List<ProductAttrValue> getBaseSpuList(Long spuId) {
        return this.list(new LambdaQueryWrapper<ProductAttrValue>().eq(spuId != null, ProductAttrValue::getSpuId, spuId));
    }

    @Override
    @Transactional
    public void updateList(Long spuId, List<ProductAttrValue> attrValueList) {
        //删除原有属性
        this.remove(new LambdaQueryWrapper<ProductAttrValue>().eq(ProductAttrValue::getSpuId,spuId));
        //保存新加入的属性
        List<ProductAttrValue> productAttrValues = attrValueList.stream()
                .peek(productAttrValue -> productAttrValue.setSpuId(spuId))
                .collect(Collectors.toList());
        this.saveBatch(productAttrValues);
    }
}
