package com.example.mall.coupon.service;

import com.example.mall.common.model.to.SkuFullReductionTo;
import com.example.mall.coupon.model.po.SkuFullReduction;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商品满减信息 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface SkuFullReductionService extends IService<SkuFullReduction> {

    void saveFullReductionDetail(SkuFullReductionTo skuFullReductionTo);
}
