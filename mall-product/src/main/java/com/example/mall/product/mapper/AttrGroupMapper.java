package com.example.mall.product.mapper;

import com.example.mall.product.model.po.AttrGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mall.product.model.vo.SkuDetailVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface AttrGroupMapper extends BaseMapper<AttrGroup> {

    List<SkuDetailVo.SpuDetailAttrGroupVo> getAttrGroupDetailBySpuIdAndCatalogId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
