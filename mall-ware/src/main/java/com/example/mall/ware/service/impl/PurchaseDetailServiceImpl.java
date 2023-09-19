package com.example.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mall.common.model.constant.WareConstant;
import com.example.mall.ware.model.po.PurchaseDetail;
import com.example.mall.ware.mapper.PurchaseDetailMapper;
import com.example.mall.ware.service.PurchaseDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailMapper, PurchaseDetail> implements PurchaseDetailService {

    @Override
    public QueryWrapper<PurchaseDetail> getQueryWrapper(String wareId, String status) {
        return new QueryWrapper<PurchaseDetail>()
                .eq(StringUtils.isNotBlank(wareId), WareConstant.PurchaseDetailTableNameEnum.WARE_ID.getTableName(), wareId)
                .eq(StringUtils.isNotBlank(status), WareConstant.PurchaseDetailTableNameEnum.STATUS.getTableName(), status);
    }

    @Override
    @Transactional
    public void updateByPurchaseId(Long id) {
        List<PurchaseDetail> purchaseDetails =
                this.list(new LambdaQueryWrapper<PurchaseDetail>()
                                .eq(PurchaseDetail::getPurchaseId, id))
                        .stream()
                        .map(purchaseDetail -> {
                            PurchaseDetail detail = new PurchaseDetail();
                            detail.setId(purchaseDetail.getId());
                            detail.setStatus(WareConstant.PurchaseDetailStatusEnum.PURCHASE_DETAIL_PROCESSING.getCode());
                            return detail;
                        })
                        .collect(Collectors.toList());
        this.updateBatchById(purchaseDetails);
    }
}
