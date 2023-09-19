package com.example.mall.product.service;

import com.example.mall.product.model.dto.BaseAttrs;
import com.example.mall.product.model.po.ProductAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * spu属性值 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface ProductAttrValueService extends IService<ProductAttrValue> {

    void saveBaseAttrs(Long spuInfoId, List<BaseAttrs> baseAttrs);

    List<ProductAttrValue> getBaseSpuList(Long spuId);

    void updateList(Long spuId, List<ProductAttrValue> attrValueList);
}
