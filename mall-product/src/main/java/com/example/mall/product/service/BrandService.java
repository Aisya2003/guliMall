package com.example.mall.product.service;

import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.result.Result;
import com.example.mall.product.model.po.Brand;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 品牌 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface BrandService extends IService<Brand> {
    /**
     * 分页查询品牌
     * @param params
     * @return
     */
    Result listByPage(PageRequestParams params);

    void saveOrUpdateDetail(Brand brand);
}
