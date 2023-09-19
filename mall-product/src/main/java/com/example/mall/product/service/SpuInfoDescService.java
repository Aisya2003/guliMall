package com.example.mall.product.service;

import com.example.mall.product.model.po.SpuInfoDesc;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * spu信息介绍 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface SpuInfoDescService extends IService<SpuInfoDesc> {

    void saveSpuImagesInfoDescBySpuInfoId(Long spuInfoId, String imagesUrl);
}
