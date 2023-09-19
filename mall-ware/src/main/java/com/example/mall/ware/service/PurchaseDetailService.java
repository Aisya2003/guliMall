package com.example.mall.ware.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mall.ware.model.po.PurchaseDetail;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface PurchaseDetailService extends IService<PurchaseDetail> {


    QueryWrapper<PurchaseDetail> getQueryWrapper(String wareId, String status);

    void updateByPurchaseId(Long id);
}
