package com.example.mall.coupon.service.impl;

import com.example.mall.common.model.constant.CouponConstant;
import com.example.mall.common.model.to.SkuFullReductionTo;
import com.example.mall.coupon.model.po.MemberPrice;
import com.example.mall.coupon.model.po.SkuFullReduction;
import com.example.mall.coupon.mapper.SkuFullReductionMapper;
import com.example.mall.coupon.model.po.SkuLadder;
import com.example.mall.coupon.service.MemberPriceService;
import com.example.mall.coupon.service.SkuFullReductionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.coupon.service.SkuLadderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionMapper, SkuFullReduction> implements SkuFullReductionService {
    private final SkuLadderService skuLadderService;
    private final MemberPriceService memberPriceService;

    public SkuFullReductionServiceImpl(SkuLadderService skuLadderService, MemberPriceService memberPriceService) {
        this.skuLadderService = skuLadderService;
        this.memberPriceService = memberPriceService;
    }

    @Override
    @Transactional
    public void saveFullReductionDetail(SkuFullReductionTo to) {
        int fullCount = to.getFullCount();
        //6.4.1保存优惠、打折信息(sms_sku_ladder)
        SkuLadder skuLadder = new SkuLadder();
        skuLadder.setSkuId(to.getSkuId());
        skuLadder.setFullCount(fullCount);
        skuLadder.setDiscount(to.getDiscount());
        skuLadder.setAddOther(to.getCountStatus());
        if (fullCount > 0) {
            log.info("开始保存优惠、打折(sku_ladder)信息[{}]", skuLadder);
            skuLadderService.save(skuLadder);
            log.info("保存优惠、打折(sku_ladder)信息完成[{}]", skuLadder.getId());
        }

        //6.4.2保存满减信息(sms_sku_full_reduction)
        SkuFullReduction skuFullReduction = new SkuFullReduction();
        BeanUtils.copyProperties(to, skuFullReduction);
        if (skuFullReduction.getFullPrice().compareTo(new BigDecimal(0)) > 0) {
            log.info("开始保存满减信息[{}]", skuLadder);
            this.save(skuFullReduction);
            log.info("保存满减信息完成[{}]", skuLadder.getId());
        }

        //6.4.3保存会员价格信息(sms_member_price)
        List<MemberPrice> memberPriceList = to.getMemberPrice()
                .stream()
                .map(memberPrice -> {
                    MemberPrice price = new MemberPrice();
                    price.setSkuId(to.getSkuId());
                    price.setMemberPrice(memberPrice.getPrice());
                    price.setMemberLevelId(memberPrice.getId());
                    price.setMemberLevelName(memberPrice.getName());
                    price.setAddOther(CouponConstant.MemberPriceEnum.ALLOW_ADD_OTHER.getCode());
                    return price;
                })
                .filter(memberPrice -> memberPrice.getMemberPrice().compareTo(new BigDecimal(0)) > 0)
                .collect(Collectors.toList());
        log.info("保存会员价格信息[{}]", memberPriceList);
        memberPriceService.saveBatch(memberPriceList);
        log.info("保存会员价格信息完成[{}]", to.getSkuId());
    }
}
