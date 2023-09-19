package com.example.mall.product.mapper;

import com.example.mall.product.model.po.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mall.product.model.vo.SkuDetailVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    List<SkuDetailVo.SkuDetailSaleAttrVo> getSaleAttrsBySpuId(@Param("spuId") Long spuId);
}
