package com.example.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mall.common.model.constant.WareConstant;
import com.example.mall.ware.model.dto.FinishPurchaseDto;
import com.example.mall.ware.model.dto.MergeDto;
import com.example.mall.ware.model.po.Purchase;
import com.example.mall.ware.mapper.PurchaseMapper;
import com.example.mall.ware.model.po.PurchaseDetail;
import com.example.mall.ware.service.PurchaseDetailService;
import com.example.mall.ware.service.PurchaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.ware.service.WareSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class PurchaseServiceImpl extends ServiceImpl<PurchaseMapper, Purchase> implements PurchaseService {

    private final PurchaseDetailService purchaseDetailService;
    private final WareSkuService wareSkuService;

    public PurchaseServiceImpl(PurchaseDetailService purchaseDetailService, WareSkuService wareSkuService) {
        this.purchaseDetailService = purchaseDetailService;
        this.wareSkuService = wareSkuService;
    }

    @Override
    public QueryWrapper<Purchase> getUnReceivePurchaseListWrapper() {
        return new QueryWrapper<Purchase>()
                .eq(WareConstant.PurchaseTableNameEnum.STATUS.getTableName(), "0")//新建状态
                .or()
                .eq(WareConstant.PurchaseTableNameEnum.STATUS.getTableName(), "1");//已分配但是未领取状态
    }

    @Override
    @Transactional
    public void mergePurchaseList(MergeDto dto) {
        Long purchaseId = dto.getPurchaseId();
        //为空则新建
        if (purchaseId == null || purchaseId <= 0) {
            Purchase purchase = new Purchase();
            purchase.setCreateTime(LocalDateTime.now());
            purchase.setUpdateTime(LocalDateTime.now());
            purchase.setStatus(WareConstant.PurchaseStatusEnum.PURCHASE_CREATED.getCode());
            this.save(purchase);
            purchaseId = purchase.getId();
        } else {
            Purchase purchase = this.getById(purchaseId);
            //不为空则判断采购单状态是否可以被合并
            Integer status = purchase.getStatus();
            if (status != WareConstant.PurchaseStatusEnum.PURCHASE_CREATED.getCode() ||
                    status != WareConstant.PurchaseStatusEnum.PURCHASE_ASSIGNED.getCode()) {
                return;
            }
        }

        Long finalPurchaseId = purchaseId;
        List<PurchaseDetail> purchaseDetailList = dto.getItems()
                .stream()
                .map(item -> {
                    PurchaseDetail purchaseDetail = new PurchaseDetail();
                    purchaseDetail.setId(item);
                    purchaseDetail.setPurchaseId(finalPurchaseId);
                    purchaseDetail.setStatus(WareConstant.PurchaseDetailStatusEnum.PURCHASE_DETAIL_ASSIGNED.getCode());
                    return purchaseDetail;
                })
                .collect(Collectors.toList());
        purchaseDetailService.updateBatchById(purchaseDetailList);
        //更新采购单时间
        Purchase updatePurchase = new Purchase();
        updatePurchase.setId(finalPurchaseId);
        updatePurchase.setUpdateTime(LocalDateTime.now());
        this.updateById(updatePurchase);
    }

    @Override
    @Transactional
    public void receivePurchase(List<Long> purchaseIds) {
        //确认采购单可以领取
        List<Purchase> availablePurchase = purchaseIds.stream()
                .map(this::getById)
                .filter(purchase -> purchase.getStatus() == WareConstant.PurchaseStatusEnum.PURCHASE_CREATED.getCode()
                        || purchase.getStatus() == WareConstant.PurchaseStatusEnum.PURCHASE_ASSIGNED.getCode()
                )
                .collect(Collectors.toList());
        //改变采购单状态
        List<Purchase> changedStatus = availablePurchase.stream()
                .peek(purchase -> {
                    purchase.setStatus(WareConstant.PurchaseStatusEnum.PURCHASE_RECEIVE.getCode());
                    purchase.setUpdateTime(LocalDateTime.now());
                })
                .collect(Collectors.toList());
        this.updateBatchById(changedStatus);

        //改变采购内容状态
        availablePurchase.forEach(purchase -> purchaseDetailService.updateByPurchaseId(purchase.getId()));
    }

    @Override
    public void finishPurchase(FinishPurchaseDto dto) {
        //改变采购项状态
        boolean success = true;
        List<PurchaseDetail> update = new ArrayList<>();
        for (FinishPurchaseDto.FinishItems item : dto.getItems()) {
            PurchaseDetail detail = new PurchaseDetail();
            Integer status = item.getStatus();
            Long itemId = item.getItemId();
            if (status == WareConstant.PurchaseDetailStatusEnum.PURCHASE_DETAIL_FAILED.getCode()) {
                success = false;
                detail.setStatus(status);
                detail.setReason(item.getReason());
            } else {
                detail.setStatus(WareConstant.PurchaseDetailStatusEnum.PURCHASE_DETAIL_FINISH.getCode());
                //成功则入库
                PurchaseDetail purchaseDetail = purchaseDetailService.getById(itemId);
                wareSkuService.appendStock(purchaseDetail.getSkuId(),purchaseDetail.getSkuNum(),purchaseDetail.getWareId());
            }
            detail.setId(itemId);
            purchaseDetailService.updateBatchById(update);
        }

        //改变采购单状态
        Purchase purchase = new Purchase();
        purchase.setId(dto.getId());
        if (success) {
            purchase.setStatus(WareConstant.PurchaseStatusEnum.PURCHASE_FINISH.getCode());

        } else {
            purchase.setStatus(WareConstant.PurchaseStatusEnum.PURCHASE_EXCEPTION.getCode());
        }
        purchase.setUpdateTime(LocalDateTime.now());
        this.updateById(purchase);

    }
}
