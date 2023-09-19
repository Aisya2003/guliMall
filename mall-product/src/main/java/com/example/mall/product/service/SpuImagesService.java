package com.example.mall.product.service;

import com.example.mall.product.model.po.SpuImages;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * spu图片 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface SpuImagesService extends IService<SpuImages> {

    void saveImagesBySpuInfoId(Long spuInfoId, List<String> imagesUrl);
}
