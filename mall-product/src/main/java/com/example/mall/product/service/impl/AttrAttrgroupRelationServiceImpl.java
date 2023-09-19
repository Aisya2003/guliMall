package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.example.mall.product.model.po.AttrAttrgroupRelation;
import com.example.mall.product.mapper.AttrAttrgroupRelationMapper;
import com.example.mall.product.service.AttrAttrgroupRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationMapper, AttrAttrgroupRelation> implements AttrAttrgroupRelationService {

    @Override
    public void removeBatch(List<AttrAttrgroupRelation> relationList) {
        if (relationList == null){
            return;
        }
        LambdaQueryWrapper<AttrAttrgroupRelation> queryWrapper = new LambdaQueryWrapper<>();
        for (AttrAttrgroupRelation relation : relationList) {
            queryWrapper.or(attrAttrgroupRelationLambdaQueryWrapper -> {
                attrAttrgroupRelationLambdaQueryWrapper
                        .eq(AttrAttrgroupRelation::getAttrGroupId, relation.getAttrGroupId())
                        .eq(AttrAttrgroupRelation::getAttrId, relation.getAttrId());
            });
        }
        this.remove(queryWrapper);
    }
}
