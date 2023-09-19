package com.example.mall.product.service;

import com.example.mall.product.model.po.SkuSaleAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.product.model.vo.SkuDetailVo;

import java.util.List;

/**
 * <p>
 * sku销售属性&值 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValue> {

    List<SkuDetailVo.SkuDetailSaleAttrVo> getSaleAttrsBySpuId(Long spuId);

    List<String> getSkuSaleAttrsAsList(Long skuId);
}
