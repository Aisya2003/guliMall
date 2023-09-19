package com.example.mall.product.service;

import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.product.model.dto.SkuRequestPageParams;
import com.example.mall.product.model.dto.Skus;
import com.example.mall.product.model.po.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.product.model.po.SpuInfo;
import com.example.mall.product.model.vo.SkuDetailVo;

import java.util.List;

/**
 * <p>
 * sku信息 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfo(Skus skuDto, SpuInfo spuInfo, String defaultImageUrl, SkuInfo skuInfo);

    PageResult<SkuInfo> listWithCondition(PageRequestParams pageRequestParams, SkuRequestPageParams skuRequestPageParams);

    List<SkuInfo> getSkuListBySpuId(Long spuId);

    interface Web {

        SkuDetailVo getDetail(Long skuId);
    }
}
