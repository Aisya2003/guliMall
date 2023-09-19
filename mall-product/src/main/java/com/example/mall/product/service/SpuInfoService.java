package com.example.mall.product.service;

import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.common.model.to.GetItemWeightTo;
import com.example.mall.product.model.dto.SpuRequestPageParams;
import com.example.mall.product.model.dto.SpuSaveDto;
import com.example.mall.product.model.po.SpuInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.product.model.po.SpuInfoDesc;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * spu信息 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface SpuInfoService extends IService<SpuInfo> {

    void saveSpuInfo(SpuSaveDto dto);


    PageResult<SpuInfo> listWithConditions(PageRequestParams pageRequestParams, SpuRequestPageParams spuRequestPageParams);

    void upLoadToES(Long spuId);

    List<GetItemWeightTo> getWeight(Set<Long> skuIds);

    SpuInfo getSpuInfoBySkuId(Long skuId);
}
