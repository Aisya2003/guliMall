package com.example.mall.coupon.service.impl;

import com.example.mall.coupon.model.po.CouponHistory;
import com.example.mall.coupon.mapper.CouponHistoryMapper;
import com.example.mall.coupon.service.CouponHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class CouponHistoryServiceImpl extends ServiceImpl<CouponHistoryMapper, CouponHistory> implements CouponHistoryService {

}
