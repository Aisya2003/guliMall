package com.example.mall.ware.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mall.ware.model.dto.FinishPurchaseDto;
import com.example.mall.ware.model.dto.MergeDto;
import com.example.mall.ware.model.po.Purchase;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 采购信息 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface PurchaseService extends IService<Purchase> {

    QueryWrapper<Purchase> getUnReceivePurchaseListWrapper();

    void mergePurchaseList(MergeDto dto);

    void receivePurchase(List<Long> purchaseIds);

    void finishPurchase(FinishPurchaseDto dto);
}
